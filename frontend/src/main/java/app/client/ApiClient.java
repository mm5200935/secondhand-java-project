package app.client;

import app.dto.request.*;
import app.dto.response.LoginResponse;
import app.dto.response.Response;
import app.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Timeout;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {
    private String host;
    private int port;
    private User currentUser;

    // توکن JWT که بعد از لاگین/ثبت‌نام موفق از بک‌اند دریافت و برای
    // درخواست‌های بعدی به‌صورت هدر Authorization: Bearer <token> ارسال می‌شود.
    private String authToken;

    private final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules() // برای پشتیبانی از LocalDateTime که بک‌اند برمی‌گردونه
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public ApiClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getAuthToken() {
        return authToken;
    }

    private String baseUrl() {
        return "http://" + host + ":" + port;
    }

    // ============================================================
    //  هسته‌ی مشترک HTTP: هر درخواست از اینجا رد می‌شه، هدر
    //  Authorization در صورت وجود توکن به‌صورت خودکار اضافه می‌شه.
    // ============================================================
    private String execute(ClassicHttpRequest request) throws Exception {
        if (authToken != null) {
            request.setHeader("Authorization", "Bearer " + authToken);
        }
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(10))
                .build();
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            return client.execute(request, httpResponse -> {
                int status = httpResponse.getCode();
                String body = httpResponse.getEntity() != null
                        ? EntityUtils.toString(httpResponse.getEntity())
                        : "";

                if (status == 401) {
                    // توکن نامعتبره یا منقضی شده -> کاربر رو لاگ‌اوت می‌کنیم
                    // که برنامه بره سراغ صفحه‌ی لاگین به‌جای کرش کردن
                    logout();
                    throw new RuntimeException("نشست شما منقضی شده، لطفاً دوباره وارد شوید.");
                }
                if (status == 403) {
                    throw new RuntimeException(body.isBlank() ? "اجازه‌ی دسترسی ندارید." : body);
                }
                if (status >= 200 && status < 300) {
                    return body;
                }
                throw new RuntimeException(body.isBlank() ? ("خطای سرور (" + status + ")") : body);
            });
        }
    }

    private URI uri(String path, Map<String, String> query) throws Exception {
        URIBuilder builder = new URIBuilder(baseUrl() + path);
        if (query != null) {
            query.forEach((k, v) -> { if (v != null) builder.addParameter(k, v); });
        }
        return builder.build();
    }

    // ===== لاگین واقعی روی بک‌اند اسپرینگ‌بوت (POST /api/users/login) با JWT =====
    public LoginResponse login(AuthRequest request) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", request.getUsername());
        body.put("password", request.getPassword());

        HttpPost post = new HttpPost(baseUrl() + "/api/users/login");
        post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));

        try {
            String responseBody = execute(post);
            JsonNode node = mapper.readTree(responseBody);
            User user = userFromAuthJson(node);

            this.authToken = node.get("token").asText();
            this.currentUser = user;

            return LoginResponse.success("ورود موفقیت‌آمیز بود", user);
        } catch (RuntimeException e) {
            return LoginResponse.error(e.getMessage());
        }
    }

    public void logout() {
        this.currentUser = null;
        this.authToken = null;
    }

    // ===== ثبت‌نام واقعی روی بک‌اند اسپرینگ‌بوت (POST /api/users/register) با JWT =====
    public Response<User> register(RegisterRequest request) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", request.getUsername());
        body.put("password", request.getPassword());
        body.put("fullName", request.getFullName());
        body.put("email", request.getEmail());
        // نکته مهم: بک‌اند فیلد را "phone" می‌خواند، نه "phoneNumber"
        body.put("phone", request.getPhoneNumber());

        HttpPost post = new HttpPost(baseUrl() + "/api/users/register");
        post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));

        try {
            String responseBody = execute(post);
            JsonNode node = mapper.readTree(responseBody);
            User user = userFromAuthJson(node);

            // ثبت‌نام موفق هم توکن برمی‌گرداند، پس کاربر را مستقیم لاگین می‌کنیم
            this.authToken = node.get("token").asText();
            this.currentUser = user;

            return Response.success("ثبت‌نام با موفقیت انجام شد", user);
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    // بک‌اند نقش را به صورت "USER"/"ADMIN" برمی‌گرداند، در حالی که مدل فرانت‌اند
    // از Role.REGULAR/Role.ADMIN استفاده می‌کند؛ این متد آن را درست تبدیل می‌کند.
    private User userFromAuthJson(JsonNode node) {
        User user = new User();
        user.setId(node.get("userId").asLong());
        user.setUsername(node.get("username").asText());
        user.setFullName(node.get("fullName").asText());

        String role = node.get("role").asText();
        user.setRole("ADMIN".equalsIgnoreCase(role) ? User.Role.ADMIN : User.Role.USER);

        return user;
    }

    // ===== آگهی‌ها: /api/ads/* روی بک‌اند (نیاز به Bearer token) =====

    public Response<Ad> createAd(Long ownerId, AdRequest request) {
        try {
            HttpPost post = new HttpPost(baseUrl() + "/api/ads");
            post.setEntity(new StringEntity(mapper.writeValueAsString(request), ContentType.APPLICATION_JSON));
            Ad ad = mapper.readValue(execute(post), Ad.class);
            return Response.success("آگهی با موفقیت ثبت شد", ad);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Ad> updateAd(Long adId, Long userId, AdRequest request) {
        try {
            HttpPut put = new HttpPut(baseUrl() + "/api/ads/" + adId);
            put.setEntity(new StringEntity(mapper.writeValueAsString(request), ContentType.APPLICATION_JSON));
            Ad ad = mapper.readValue(execute(put), Ad.class);
            return Response.success("آگهی ویرایش شد", ad);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Ad> deleteAd(Long adId, Long userId) {
        try {
            HttpDelete delete = new HttpDelete(baseUrl() + "/api/ads/" + adId);
            execute(delete);
            return Response.success("آگهی حذف شد", null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Ad> markAsSold(Long adId, Long userId) {
        try {
            HttpPost post = new HttpPost(baseUrl() + "/api/ads/" + adId + "/sold");
            Ad ad = mapper.readValue(execute(post), Ad.class);
            return Response.success("آگهی فروخته‌شده علامت‌گذاری شد", ad);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Ad> approveAd(Long adId, Long adminId) {
        try {
            HttpPost post = new HttpPost(baseUrl() + "/api/ads/" + adId + "/approve");
            Ad ad = mapper.readValue(execute(post), Ad.class);
            return Response.success("آگهی تایید شد", ad);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Ad> rejectAd(Long adId, String reason, Long adminId) {
        try {
            HttpPost post = new HttpPost(baseUrl() + "/api/ads/" + adId + "/reject");
            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("reason", reason);
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));

            Ad ad = mapper.readValue(execute(post), Ad.class);
            return Response.success("آگهی رد شد", ad);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<Ad>> getActiveAds() {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/ads");
            List<Ad> ads = mapper.readValue(execute(get), new TypeReference<List<Ad>>() {});
            return Response.success("OK", ads);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<Ad>> getPendingAds(Long adminId) {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/ads/pending");
            List<Ad> ads = mapper.readValue(execute(get), new TypeReference<List<Ad>>() {});
            return Response.success("OK", ads);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<Ad>> getAllAdsForAdmin(Long adminId) {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/ads/admin/all");
            List<Ad> ads = mapper.readValue(execute(get), new TypeReference<List<Ad>>() {});
            return Response.success("OK", ads);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Ad> adminDeleteAd(Long adId, Long adminId) {
        try {
            HttpDelete delete = new HttpDelete(baseUrl() + "/api/ads/" + adId + "/admin");
            execute(delete);
            return Response.success("آگهی توسط مدیر حذف شد", null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Ad> getAdDetails(Long adId) {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/ads/" + adId);
            Ad ad = mapper.readValue(execute(get), Ad.class);
            return Response.success("OK", ad);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<Ad>> getUserAds(Long userId) {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/ads/user/" + userId);
            List<Ad> ads = mapper.readValue(execute(get), new TypeReference<List<Ad>>() {});
            return Response.success("OK", ads);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<Ad>> searchAds(SearchRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("keyword", request.getKeyword() == null ? "" : request.getKeyword());
            if (request.getCategoryId() != null) params.put("categoryId", String.valueOf(request.getCategoryId()));
            if (request.getCityId() != null) params.put("cityId", String.valueOf(request.getCityId()));
            if (request.getMinPrice() != null) params.put("minPrice", String.valueOf(request.getMinPrice()));
            if (request.getMaxPrice() != null) params.put("maxPrice", String.valueOf(request.getMaxPrice()));

            URI target = uri("/api/ads/search", params);
            HttpGet get = new HttpGet(target);
            List<Ad> ads = mapper.readValue(execute(get), new TypeReference<List<Ad>>() {});
            return Response.success("OK", ads);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ============================================================
    //  بخش‌های زیر (پیام‌رسانی، علاقه‌مندی‌ها، امتیازدهی، مدیریت
    //  کاربران) هنوز روی بک‌اند Controller ندارن — سرویس و ریپازیتوری
    //  آماده‌ست ولی هیچ endpoint ای بهشون وصل نیست. تا وقتی که مثل
    //  AdvertisementController یه @RestController براشون نسازیم،
    //  این متدها عمداً یک خطای روشن پرتاب می‌کنن به‌جای کرش خام سوکت.
    // ============================================================
    private Object notImplemented(String feature) {
        throw new UnsupportedOperationException(
                "قابلیت «" + feature + "» هنوز روی بک‌اند پیاده نشده (کنترلر REST مربوطه ساخته نشده).");
    }

    // ===== امتیازدهی: /api/ratings/* روی بک‌اند (نیاز به Bearer token برای ثبت) =====

    public Response<Rating> rateSeller(RatingRequest request) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("adId", request.getAdId());
            body.put("score", request.getScore());
            body.put("comment", request.getComment());

            HttpPost post = new HttpPost(baseUrl() + "/api/ratings");
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));
            Rating rating = mapper.readValue(execute(post), Rating.class);
            return Response.success("امتیاز با موفقیت ثبت شد", rating);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<Rating>> getSellerRatings(Long sellerId) {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/ratings/seller/" + sellerId);
            List<Rating> ratings = mapper.readValue(execute(get), new TypeReference<List<Rating>>() {});
            return Response.success("OK", ratings);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Double> getSellerAverageRating(Long sellerId) {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/ratings/seller/" + sellerId + "/average");
            Double avg = Double.valueOf(execute(get));
            return Response.success("OK", avg);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Boolean> hasUserRatedAd(Long adId, Long userId) {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/ratings/has-rated/" + adId);
            Boolean rated = Boolean.valueOf(execute(get));
            return Response.success("OK", rated);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Response<User> getUserById(Long userId) throws Exception {
        return (Response<User>) notImplemented("GET_USER");
    }

    @SuppressWarnings("unchecked")
    public Response<Conversation> sendMessage(MessageRequest request) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("adId", request.getAdId());
        body.put("content", request.getContent());
        HttpPost post = new HttpPost(baseUrl() + "/api/conversations/messages");
        post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));
        Conversation conv = mapper.readValue(execute(post), Conversation.class);
        return Response.success("پیام ارسال شد", conv);
    }

    public Response<List<Conversation>> getConversations(Long userId) throws Exception {
        HttpGet get = new HttpGet(baseUrl() + "/api/conversations");
        List<Conversation> list = mapper.readValue(execute(get), new TypeReference<List<Conversation>>() {});
        return Response.success("OK", list);
    }

    public Response<List<Message>> getConversationMessages(Long conversationId, Long userId) throws Exception {
        HttpGet get = new HttpGet(baseUrl() + "/api/conversations/" + conversationId + "/messages");
        List<Message> list = mapper.readValue(execute(get), new TypeReference<List<Message>>() {});
        return Response.success("OK", list);
    }

    public Response<Conversation> getConversation(Long conversationId, Long userId) throws Exception {
        HttpGet get = new HttpGet(baseUrl() + "/api/conversations/" + conversationId);
        Conversation conv = mapper.readValue(execute(get), Conversation.class);
        return Response.success("OK", conv);
    }

    public Response<Boolean> deleteConversation(Long conversationId) {
        try {
            HttpDelete delete = new HttpDelete(baseUrl() + "/api/conversations/" + conversationId);
            execute(delete);
            return Response.success("گفت‌وگو حذف شد", true);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Conversation> startConversation(Long adId) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("adId", adId);
            HttpPost post = new HttpPost(baseUrl() + "/api/conversations/start");
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));
            Conversation conv = mapper.readValue(execute(post), Conversation.class);
            return Response.success("OK", conv);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Response<List<User>> getAllUsers(Long adminId) {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/users");
            List<User> users = mapper.readValue(execute(get), new TypeReference<List<User>>() {});
            return Response.success("OK", users);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<User> blockUser(Long userId, Long adminId) {
        try {
            HttpPut put = new HttpPut(baseUrl() + "/api/users/" + userId + "/block");
            User user = mapper.readValue(execute(put), User.class);
            return Response.success("کاربر مسدود شد", user);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<User> unblockUser(Long userId, Long adminId) {
        try {
            HttpPut put = new HttpPut(baseUrl() + "/api/users/" + userId + "/unblock");
            User user = mapper.readValue(execute(put), User.class);
            return Response.success("کاربر فعال شد", user);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
//    public Response<Boolean> addFavorite(Long adId, Long userId) throws Exception {
//        return (Response<Boolean>) notImplemented("ADD_FAVORITE");
//    }
//
//    @SuppressWarnings("unchecked")
//    public Response<Boolean> removeFavorite(Long adId, Long userId) throws Exception {
//        return (Response<Boolean>) notImplemented("REMOVE_FAVORITE");
//    }
//
//    @SuppressWarnings("unchecked")
//    public Response<List<Ad>> getFavorites(Long userId) throws Exception {
//        return (Response<List<Ad>>) notImplemented("GET_FAVORITES");
//    }
//
//    @SuppressWarnings("unchecked")
//    public Response<Boolean> isFavorite(Long adId, Long userId) throws Exception {
//        return (Response<Boolean>) notImplemented("IS_FAVORITE");
//    }

    public Response<Boolean> addFavorite(Long adId, Long userId) throws Exception {
        try {
            HttpPost post = new HttpPost(baseUrl() + "/api/favorites/" + adId);
            boolean result = mapper.readValue(execute(post), Boolean.class);
            return Response.success("به علاقه‌مندی‌ها اضافه شد", result);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Boolean> removeFavorite(Long adId, Long userId) throws Exception {
        try {
            HttpDelete delete = new HttpDelete(baseUrl() + "/api/favorites/" + adId);
            boolean result = mapper.readValue(execute(delete), Boolean.class);
            return Response.success("از علاقه‌مندی‌ها حذف شد", result);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<Ad>> getFavorites(Long userId) throws Exception {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/favorites/user/" + userId);
            List<Ad> ads = mapper.readValue(execute(get), new TypeReference<List<Ad>>() {});
            return Response.success("OK", ads);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Boolean> isFavorite(Long adId, Long userId) throws Exception {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/favorites/check/" + adId);
            boolean result = mapper.readValue(execute(get), Boolean.class);
            return Response.success("OK", result);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ===== دریافت واقعی دسته‌بندی‌ها از بک‌اند (GET /api/categories) =====
    public Response<List<Category>> getAllCategories() {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/categories");
            List<Category> categories = mapper.readValue(execute(get), new TypeReference<List<Category>>() {});
            return Response.success("OK", categories);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ===== دریافت واقعی شهرها از بک‌اند (GET /api/cities) =====
    public Response<List<City>> getAllCities() {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/cities");
            List<City> cities = mapper.readValue(execute(get), new TypeReference<List<City>>() {});
            return Response.success("OK", cities);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Message> sendMessageToConversation(Long conversationId, String content) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("content", content);
            HttpPost post = new HttpPost(baseUrl() + "/api/conversations/" + conversationId + "/messages");
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));
            Message msg = mapper.readValue(execute(post), Message.class);
            return Response.success("پیام ارسال شد", msg);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<Rating>> getAdRatings(Long adId) {
        try {
            HttpGet get = new HttpGet(baseUrl() + "/api/ratings/ad/" + adId);
            List<Rating> ratings = mapper.readValue(execute(get), new TypeReference<List<Rating>>() {});
            return Response.success("OK", ratings);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<Ad>> searchAllAdsForAdmin(SearchRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("keyword", request.getKeyword() == null ? "" : request.getKeyword());
            if (request.getCategoryId() != null) params.put("categoryId", String.valueOf(request.getCategoryId()));
            if (request.getCityId() != null) params.put("cityId", String.valueOf(request.getCityId()));
            if (request.getMinPrice() != null) params.put("minPrice", String.valueOf(request.getMinPrice()));
            if (request.getMaxPrice() != null) params.put("maxPrice", String.valueOf(request.getMaxPrice()));

            URI target = uri("/api/ads/admin/search", params);
            HttpGet get = new HttpGet(target);
            List<Ad> ads = mapper.readValue(execute(get), new TypeReference<List<Ad>>() {});
            return Response.success("OK", ads);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Category> createCategory(String name) {
        try {
            HttpPost post = new HttpPost(baseUrl() + "/api/categories");
            Category body = new Category();
            body.setName(name);
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));

            Category category = mapper.readValue(execute(post), Category.class);
            return Response.success("دسته‌بندی با موفقیت اضافه شد", category);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> deleteCategory(Long categoryId) {
        try {
            HttpDelete delete = new HttpDelete(baseUrl() + "/api/categories/" + categoryId);
            execute(delete);
            return Response.success("دسته‌بندی با موفقیت حذف شد", null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Category> createCategoryFull(String name, String description) {
        try {
            HttpPost post = new HttpPost(baseUrl() + "/api/categories");
            Category body = new Category();
            body.setName(name);
            body.setDescription(description);
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));

            Category category = mapper.readValue(execute(post), Category.class);
            return Response.success("دسته‌بندی با موفقیت اضافه شد", category);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Category> updateCategory(Long categoryId, String name, String description) {
        try {
            HttpPut put = new HttpPut(baseUrl() + "/api/categories/" + categoryId);
            Category body = new Category();
            body.setName(name);
            body.setDescription(description);
            put.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));

            Category category = mapper.readValue(execute(put), Category.class);
            return Response.success("دسته‌بندی با موفقیت ویرایش شد", category);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
