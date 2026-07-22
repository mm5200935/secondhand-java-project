package app.controller;

import app.dto.request.*;
import app.dto.response.LoginResponse;
import app.dto.response.Response;
import app.model.*;
import app.repository.*;
import app.service.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ApiController {
    private final AuthService authService;
    private final AdService adService;
    private final ConversationService conversationService;
    private final RatingService ratingService;
    private final UserManagementService userManagementService;
    private final FavoriteService favoriteService;

    public ApiController() {
        UserRepository userRepository = new UserRepository();
        AdRepository adRepository = new AdRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        CityRepository cityRepository = new CityRepository();
        ConversationRepository conversationRepository = new ConversationRepository();
        MessageRepository messageRepository = new MessageRepository();
        RatingRepository ratingRepository = new RatingRepository();

        this.authService = new AuthService();
        this.adService = new AdService(authService, adRepository, categoryRepository, cityRepository);
        this.conversationService = new ConversationService(conversationRepository, messageRepository,
                userRepository, adService);
        this.ratingService = new RatingService(ratingRepository, userRepository, adService);
        this.userManagementService = new UserManagementService(userRepository);
        this.favoriteService = new FavoriteService(userRepository, adService);
    }

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String requestType = (String) ois.readObject();

            switch (requestType) {
                case "REGISTER":
                    RegisterRequest registerRequest = (RegisterRequest) ois.readObject();
                    Response<User> registerResponse = authService.register(registerRequest);
                    oos.writeObject(registerResponse);
                    break;

                // ===== این بخش اصلاح شده است =====
                case "LOGIN":
                    AuthRequest loginRequest = (AuthRequest) ois.readObject();
                    LoginResponse loginResponse = authService.login(loginRequest);
                    oos.writeObject(loginResponse);
                    break;

                case "GET_USER":
                    Long userId = (Long) ois.readObject();
                    Response<User> userResponse = authService.getUserById(userId);
                    oos.writeObject(userResponse);
                    break;

                case "CREATE_AD":
                    Long ownerId = (Long) ois.readObject();
                    AdRequest createAdRequest = (AdRequest) ois.readObject();
                    Response<Ad> createAdResponse = adService.createAd(createAdRequest, ownerId);
                    oos.writeObject(createAdResponse);
                    break;

                case "UPDATE_AD":
                    Long updateAdId = (Long) ois.readObject();
                    Long updateUserId = (Long) ois.readObject();
                    AdRequest updateAdRequest = (AdRequest) ois.readObject();
                    Response<Ad> updateAdResponse = adService.updateAd(updateAdId, updateAdRequest, updateUserId);
                    oos.writeObject(updateAdResponse);
                    break;

                case "DELETE_AD":
                    Long deleteAdId = (Long) ois.readObject();
                    Long deleteUserId = (Long) ois.readObject();
                    Response<Ad> deleteAdResponse = adService.deleteAd(deleteAdId, deleteUserId);
                    oos.writeObject(deleteAdResponse);
                    break;

                case "MARK_AS_SOLD":
                    Long soldAdId = (Long) ois.readObject();
                    Long soldUserId = (Long) ois.readObject();
                    Response<Ad> soldResponse = adService.markAsSold(soldAdId, soldUserId);
                    oos.writeObject(soldResponse);
                    break;

                case "APPROVE_AD":
                    Long approveAdId = (Long) ois.readObject();
                    Long approveAdminId = (Long) ois.readObject();
                    Response<Ad> approveResponse = adService.approveAd(approveAdId, approveAdminId);
                    oos.writeObject(approveResponse);
                    break;

                case "REJECT_AD":
                    Long rejectAdId = (Long) ois.readObject();
                    String rejectReason = (String) ois.readObject();
                    Long rejectAdminId = (Long) ois.readObject();
                    Response<Ad> rejectResponse = adService.rejectAd(rejectAdId, rejectReason, rejectAdminId);
                    oos.writeObject(rejectResponse);
                    break;

                case "GET_ACTIVE_ADS":
                    Response<List<Ad>> activeAdsResponse = adService.getAllActiveAds();
                    oos.writeObject(activeAdsResponse);
                    break;

                case "GET_PENDING_ADS":
                    Long pendingAdminId = (Long) ois.readObject();
                    Response<List<Ad>> pendingAdsResponse = adService.getPendingAds(pendingAdminId);
                    oos.writeObject(pendingAdsResponse);
                    break;

                case "GET_AD_DETAILS":
                    Long detailAdId = (Long) ois.readObject();
                    Response<Ad> detailResponse = adService.getAdDetails(detailAdId);
                    oos.writeObject(detailResponse);
                    break;

                case "GET_USER_ADS":
                    Long userAdsId = (Long) ois.readObject();
                    Response<List<Ad>> userAdsResponse = adService.getUserAds(userAdsId);
                    oos.writeObject(userAdsResponse);
                    break;

                case "SEARCH_ADS":
                    SearchRequest searchRequest = (SearchRequest) ois.readObject();
                    Response<List<Ad>> searchResponse = adService.searchAds(searchRequest);
                    oos.writeObject(searchResponse);
                    break;

                case "SEND_MESSAGE":
                    MessageRequest messageRequest = (MessageRequest) ois.readObject();
                    Response<Conversation> sendMessageResponse = conversationService.sendMessage(messageRequest);
                    oos.writeObject(sendMessageResponse);
                    break;

                case "GET_CONVERSATIONS":
                    Long convUserId = (Long) ois.readObject();
                    Response<List<Conversation>> conversationsResponse = conversationService.getUserConversations(convUserId);
                    oos.writeObject(conversationsResponse);
                    break;

                case "GET_CONVERSATION_MESSAGES":
                    Long convId = (Long) ois.readObject();
                    Long msgUserId = (Long) ois.readObject();
                    Response<List<Message>> messagesResponse = conversationService.getConversationMessages(convId, msgUserId);
                    oos.writeObject(messagesResponse);
                    break;

                case "GET_CONVERSATION":
                    Long getConvId = (Long) ois.readObject();
                    Long getConvUserId = (Long) ois.readObject();
                    Response<Conversation> convResponse = conversationService.getConversationById(getConvId, getConvUserId);
                    oos.writeObject(convResponse);
                    break;

                case "RATE_SELLER":
                    RatingRequest ratingRequest = (RatingRequest) ois.readObject();
                    Response<Rating> rateResponse = ratingService.rateSeller(ratingRequest);
                    oos.writeObject(rateResponse);
                    break;

                case "GET_SELLER_RATINGS":
                    Long sellerId = (Long) ois.readObject();
                    Response<List<Rating>> sellerRatingsResponse = ratingService.getSellerRatings(sellerId);
                    oos.writeObject(sellerRatingsResponse);
                    break;

                case "GET_SELLER_AVERAGE_RATING":
                    Long avgSellerId = (Long) ois.readObject();
                    Response<Double> avgRatingResponse = ratingService.getSellerAverageRating(avgSellerId);
                    oos.writeObject(avgRatingResponse);
                    break;

                case "HAS_USER_RATED_AD":
                    Long ratedAdId = (Long) ois.readObject();
                    Long raterId = (Long) ois.readObject();
                    Response<Boolean> hasRatedResponse = ratingService.hasUserRatedAd(ratedAdId, raterId);
                    oos.writeObject(hasRatedResponse);
                    break;

                case "GET_ALL_USERS":
                    Long adminId = (Long) ois.readObject();
                    Response<List<User>> allUsersResponse = userManagementService.getAllUsers(adminId);
                    oos.writeObject(allUsersResponse);
                    break;

                case "BLOCK_USER":
                    Long blockUserId = (Long) ois.readObject();
                    Long blockAdminId = (Long) ois.readObject();
                    Response<User> blockResponse = userManagementService.blockUser(blockUserId, blockAdminId);
                    oos.writeObject(blockResponse);
                    break;

                case "UNBLOCK_USER":
                    Long unblockUserId = (Long) ois.readObject();
                    Long unblockAdminId = (Long) ois.readObject();
                    Response<User> unblockResponse = userManagementService.unblockUser(unblockUserId, unblockAdminId);
                    oos.writeObject(unblockResponse);
                    break;

                case "ADD_FAVORITE":
                    Long favAdId = (Long) ois.readObject();
                    Long favUserId = (Long) ois.readObject();
                    Response<Boolean> addFavResponse = favoriteService.addFavorite(favAdId, favUserId);
                    oos.writeObject(addFavResponse);
                    break;

                case "REMOVE_FAVORITE":
                    Long removeFavAdId = (Long) ois.readObject();
                    Long removeFavUserId = (Long) ois.readObject();
                    Response<Boolean> removeFavResponse = favoriteService.removeFavorite(removeFavAdId, removeFavUserId);
                    oos.writeObject(removeFavResponse);
                    break;

                case "GET_FAVORITES":
                    Long getFavUserId = (Long) ois.readObject();
                    Response<List<Ad>> getFavResponse = favoriteService.getFavorites(getFavUserId);
                    oos.writeObject(getFavResponse);
                    break;

                case "IS_FAVORITE":
                    Long isFavAdId = (Long) ois.readObject();
                    Long isFavUserId = (Long) ois.readObject();
                    Response<Boolean> isFavResponse = favoriteService.isFavorite(isFavAdId, isFavUserId);
                    oos.writeObject(isFavResponse);
                    break;

                case "GET_ALL_CATEGORIES":
                    Response<List<Category>> categoriesResponse =
                            Response.success("لیست دسته‌بندی‌ها",
                                    new CategoryRepository().findAll());
                    oos.writeObject(categoriesResponse);
                    break;

                case "GET_ALL_CITIES":
                    Response<List<City>> citiesResponse =
                            Response.success("لیست شهرها",
                                    new CityRepository().findAll());
                    oos.writeObject(citiesResponse);
                    break;

                default:
                    oos.writeObject(Response.error("درخواست نامعتبر"));
                    break;
            }

        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
            try (ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {
                oos.writeObject(Response.error("خطا در پردازش درخواست: " + e.getMessage()));
            } catch (IOException ignored) {}
        }
    }
}