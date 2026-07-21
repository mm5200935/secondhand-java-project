package app.view.page;

import app.client.ApiClient;
import app.dto.response.Response;
import app.model.Ad;
import app.model.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;

public class AdminPage extends BorderPane {

    private final ApiClient apiClient;
    private final TableView<Ad> adsTable;
    private final TableView<User> usersTable;
    private final TableView<Category> categoriesTable = new TableView<>();
    private final TableView<Ad> allAdsTable = new TableView<>();

    public AdminPage(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.adsTable = new TableView<>();
        this.usersTable = new TableView<>();

        TabPane adminTabs = new TabPane();
        adminTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab adsTab = new Tab("مدیریت آگهی‌ها", createAdsManagementPanel());
        Tab allAdsTab = new Tab("جستجوی کل آگهی‌ها", createAllAdsManagementPanel());
        Tab usersTab = new Tab("مدیریت کاربران", createUsersManagementPanel());
        Tab categoriesTab = new Tab("مدیریت دسته‌بندی‌ها", createCategoryManagementPanel());
        Tab dashboardTab = new Tab("داشبورد آماری", createDashboardPanel());

        adminTabs.getTabs().addAll(
            dashboardTab,
            categoriesTab,
            adsTab,
            allAdsTab,
            usersTab
        );

        this.setCenter(adminTabs);

        // بارگذاری اولیه داده‌ها
        refreshAds();
        refreshUsers();
        refreshCategories();
        
        // بارگذاری اولیه همه آگهی‌ها (با یک جستجوی خالی)
        performSearch(new app.dto.request.SearchRequest());
    }
    

    // ==========================================
    // بخش مدیریت آگهی‌ها
    // ==========================================
    private VBox createAdsManagementPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("لیست آگهی‌های در انتظار بررسی");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // پیکربندی ستون‌های جدول آگهی‌ها
        TableColumn<Ad, String> titleCol = new TableColumn<>("عنوان");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        
        TableColumn<Ad, String> priceCol = new TableColumn<>("قیمت");
        priceCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPrice())));
        
        TableColumn<Ad, String> statusCol = new TableColumn<>("وضعیت");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().name()));

        adsTable.getColumns().addAll(titleCol, priceCol, statusCol);
        adsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox actionButtons = new HBox(10);
        Button approveBtn = new Button("تایید آگهی");
        Button rejectBtn = new Button("رد آگهی");
        Button deleteBtn = new Button("حذف کامل");
        Button refreshBtn = new Button("بروزرسانی لیست");

        approveBtn.setOnAction(e -> handleApproveAd());
        rejectBtn.setOnAction(e -> handleRejectAd());
        deleteBtn.setOnAction(e -> handleDeleteAd());
        refreshBtn.setOnAction(e -> refreshAds());

        actionButtons.getChildren().addAll(approveBtn, rejectBtn, deleteBtn, refreshBtn);
        layout.getChildren().addAll(title, adsTable, actionButtons);

        return layout;
    }

    private VBox createAllAdsManagementPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("جستجوی پیشرفته و نظارت بر کل آگهی‌ها");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // ساخت نوار جستجو
        HBox searchBar = new HBox(10);
        searchBar.setStyle("-fx-alignment: center-left;");
        
        TextField keywordField = new TextField();
        keywordField.setPromptText("کلمه کلیدی...");
        
        TextField minPriceField = new TextField();
        minPriceField.setPromptText("حداقل قیمت");
        minPriceField.setPrefWidth(100);
        
        TextField maxPriceField = new TextField();
        maxPriceField.setPromptText("حداکثر قیمت");
        maxPriceField.setPrefWidth(100);
        
        Button searchBtn = new Button("جستجو");

        searchBar.getChildren().addAll(
            new Label("فیلترها:"), keywordField, minPriceField, maxPriceField, searchBtn
        );

        // پیکربندی ستون‌های جدول
        TableColumn<Ad, String> titleCol = new TableColumn<>("عنوان");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        
        TableColumn<Ad, String> priceCol = new TableColumn<>("قیمت");
        priceCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPrice())));
        
        TableColumn<Ad, String> statusCol = new TableColumn<>("وضعیت");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().name()));

        allAdsTable.getColumns().addAll(titleCol, priceCol, statusCol);
        allAdsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // دکمه‌های عملیاتی برای آگهی‌های جستجو شده
        HBox actionButtons = new HBox(10);
        Button deleteBtn = new Button("حذف آگهی (نظارت مدیر)");
        deleteBtn.setStyle("-fx-text-fill: red;");

        deleteBtn.setOnAction(e -> handleAdminDeleteAnyAd());
        
        // منطق دکمه جستجو
        searchBtn.setOnAction(e -> {
            app.dto.request.SearchRequest request = new app.dto.request.SearchRequest();
            request.setKeyword(keywordField.getText().trim());
            
            try {
                if (!minPriceField.getText().trim().isEmpty()) {
                    request.setMinPrice(Double.parseDouble(minPriceField.getText().trim()));
                }
                if (!maxPriceField.getText().trim().isEmpty()) {
                    request.setMaxPrice(Double.parseDouble(maxPriceField.getText().trim()));
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "لطفاً مقادیر قیمت را به صورت عدد معتبر وارد کنید.");
                return;
            }
            
            performSearch(request);
        });

        actionButtons.getChildren().addAll(deleteBtn);
        layout.getChildren().addAll(title, searchBar, allAdsTable, actionButtons);

        return layout;
    }

    private void performSearch(app.dto.request.SearchRequest request) {
        new Thread(() -> {
            // فراخوانی جستجوی مخصوص مدیران از طریق ApiClient
            Response<List<Ad>> response = apiClient.searchAllAdsForAdmin(request);
            Platform.runLater(() -> {
                if (response.isSuccess()) {
                    ObservableList<Ad> data = FXCollections.observableArrayList(response.getData());
                    allAdsTable.setItems(data);
                } else {
                    showAlert(Alert.AlertType.ERROR, "خطا در جستجو: " + response.getMessage());
                }
            });
        }).start();
    }

    private void handleAdminDeleteAnyAd() {
        Ad selected = allAdsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("حذف قطعی آگهی");
            confirm.setHeaderText("نظارت و حذف آگهی متخلف");
            confirm.setContentText("آیا از حذف کامل این آگهی اطمینان دارید؟ این عملیات غیرقابل بازگشت است.");
            
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    Long adminId = apiClient.getCurrentUser().getId();
                    // استفاده از متد حذف ادمین
                    Response<Ad> response = apiClient.adminDeleteAd(selected.getId(), adminId);
                    
                    showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.getMessage());
                    if (response.isSuccess()) {
                        // حذف مستقیم از جدول بدون نیاز به رفرش کامل
                        allAdsTable.getItems().remove(selected);
                        // در صورت نیاز رفرش جدول لیست در انتظارها هم انجام شود
                        refreshAds(); 
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "لطفاً یک آگهی را از لیست انتخاب کنید.");
        }
    }

    private VBox createCategoryManagementPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("مدیریت دسته‌بندی‌های سامانه");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // پیکربندی ستون‌های جدول
        TableColumn<Category, String> nameCol = new TableColumn<>("نام دسته‌بندی");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Category, String> descCol = new TableColumn<>("توضیحات");
        descCol.setCellValueFactory(data -> {
            String desc = data.getValue().getDescription();
            return new SimpleStringProperty(desc != null ? desc : "بدون توضیحات");
        });

        categoriesTable.getColumns().addAll(nameCol, descCol);
        categoriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // دکمه‌های عملیاتی
        HBox actionButtons = new HBox(10);
        Button addBtn = new Button("افزودن دسته‌بندی جدید");
        Button editBtn = new Button("ویرایش");
        Button deleteBtn = new Button("حذف");
        Button refreshBtn = new Button("بروزرسانی");

        addBtn.setOnAction(e -> showCategoryDialog(null));
        editBtn.setOnAction(e -> {
            Category selected = categoriesTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showCategoryDialog(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "لطفاً یک دسته‌بندی را برای ویرایش انتخاب کنید.");
            }
        });
        deleteBtn.setOnAction(e -> handleDeleteCategory());
        refreshBtn.setOnAction(e -> refreshCategories());

        actionButtons.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        layout.getChildren().addAll(title, categoriesTable, actionButtons);

        return layout;
    }

    private void showCategoryDialog(Category categoryToEdit) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(categoryToEdit == null ? "افزودن دسته‌بندی جدید" : "ویرایش دسته‌بندی");
        dialog.setHeaderText("لطفاً مشخصات دسته‌بندی را وارد کنید:");

        // ساخت فرم ورودی
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("نام دسته‌بندی");
        
        TextArea descArea = new TextArea();
        descArea.setPromptText("توضیحات دسته‌بندی");
        descArea.setPrefRowCount(3);

        if (categoryToEdit != null) {
            nameField.setText(categoryToEdit.getName());
            descArea.setText(categoryToEdit.getDescription() != null ? categoryToEdit.getDescription() : "");
        }

        grid.add(new Label("نام:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("توضیحات:"), 0, 1);
        grid.add(descArea, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // پس از فشردن دکمه تایید
        dialog.showAndWait().ifPresent(responseBtn -> {
            if (responseBtn == ButtonType.OK) {
                String name = nameField.getText().trim();
                String desc = descArea.getText().trim();
                
                if (name.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "نام دسته‌بندی نمی‌تواند خالی باشد.");
                    return;
                }

                Response<Category> response;
                if (categoryToEdit == null) {
                    // افزودن دسته‌بندی جدید با فراخوانی متد سمت سرور
                    response = apiClient.createCategoryFull(name, desc);
                } else {
                    // ویرایش دسته‌بندی موجود
                    response = apiClient.updateCategory(categoryToEdit.getId(), name, desc);
                }

                showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.getMessage());
                if (response.isSuccess()) refreshCategories();
            }
        });
    }

    private void handleDeleteCategory() {
        Category selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("حذف دسته‌بندی");
            confirmDialog.setHeaderText("آیا از حذف این دسته‌بندی اطمینان دارید؟");
            confirmDialog.setContentText("این عملیات غیرقابل بازگشت است.");

            confirmDialog.showAndWait().ifPresent(responseBtn -> {
                if (responseBtn == ButtonType.OK) {
                    Response<Void> response = apiClient.deleteCategory(selected.getId());
                    showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.getMessage());
                    if (response.isSuccess()) refreshCategories();
                }
            });
        }
    }

    private void refreshCategories() {
        new Thread(() -> {
            // فراخوانی متد دریافت تمام دسته‌بندی‌ها از بک‌اند
            Response<List<Category>> response = apiClient.getAllCategories();
            Platform.runLater(() -> {
                if (response.isSuccess()) {
                    ObservableList<Category> data = FXCollections.observableArrayList(response.getData());
                    categoriesTable.setItems(data);
                } else {
                    showAlert(Alert.AlertType.ERROR, "خطا در بارگذاری دسته‌بندی‌ها: " + response.getMessage());
                }
            });
        }).start();
    }

    private void handleApproveAd() {
        Ad selected = adsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Long adminId = apiClient.getCurrentUser().getId();
            Response<Ad> response = apiClient.approveAd(selected.getId(), adminId);
            showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.getMessage());
            if (response.isSuccess()) refreshAds();
        }
    }

    private void handleRejectAd() {
        Ad selected = adsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("رد آگهی");
            dialog.setHeaderText("دلیل رد کردن آگهی را وارد کنید:");
            Optional<String> result = dialog.showAndWait();
            
            result.ifPresent(reason -> {
                Long adminId = apiClient.getCurrentUser().getId();
                Response<Ad> response = apiClient.rejectAd(selected.getId(), reason, adminId);
                showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.getMessage());
                if (response.isSuccess()) refreshAds();
            });
        }
    }

    private void handleDeleteAd() {
        Ad selected = adsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Long adminId = apiClient.getCurrentUser().getId();
            Response<Ad> response = apiClient.adminDeleteAd(selected.getId(), adminId);
            showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.getMessage());
            if (response.isSuccess()) refreshAds();
        }
    }

    private void refreshAds() {
        new Thread(() -> {
            Long adminId = apiClient.getCurrentUser().getId();
            // بر اساس کد ApiClient، آگهی‌های در انتظار بررسی دریافت می‌شوند
            Response<List<Ad>> response = apiClient.getPendingAds(adminId);
            Platform.runLater(() -> {
                if (response.isSuccess()) {
                    ObservableList<Ad> data = FXCollections.observableArrayList(response.getData());
                    adsTable.setItems(data);
                } else {
                    showAlert(Alert.AlertType.ERROR, "خطا در دریافت آگهی‌ها: " + response.getMessage());
                }
            });
        }).start();
    }

    // ==========================================
    // بخش مدیریت کاربران
    // ==========================================
    private VBox createUsersManagementPanel() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("مدیریت کاربران سامانه");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableColumn<User, String> userCol = new TableColumn<>("نام کاربری");
        userCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<User, String> nameCol = new TableColumn<>("نام کامل");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));

        usersTable.getColumns().addAll(userCol, nameCol);
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox actionButtons = new HBox(10);
        Button blockBtn = new Button("مسدود کردن");
        Button unblockBtn = new Button("فعال‌سازی");
        Button refreshBtn = new Button("بروزرسانی");

        blockBtn.setOnAction(e -> handleBlockUser(true));
        unblockBtn.setOnAction(e -> handleBlockUser(false));
        refreshBtn.setOnAction(e -> refreshUsers());

        actionButtons.getChildren().addAll(blockBtn, unblockBtn, refreshBtn);
        layout.getChildren().addAll(title, usersTable, actionButtons);

        return layout;
    }

    private void handleBlockUser(boolean block) {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Long adminId = apiClient.getCurrentUser().getId();
            Response<User> response = block 
                ? apiClient.blockUser(selected.getId(), adminId) 
                : apiClient.unblockUser(selected.getId(), adminId);
                
            showAlert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.getMessage());
            if (response.isSuccess()) refreshUsers();
        }
    }

    private void refreshUsers() {
        new Thread(() -> {
            Long adminId = apiClient.getCurrentUser().getId();
            Response<List<User>> response = apiClient.getAllUsers(adminId);
            Platform.runLater(() -> {
                if (response.isSuccess()) {
                    ObservableList<User> data = FXCollections.observableArrayList(response.getData());
                    usersTable.setItems(data);
                }
            });
        }).start();
    }

    // ==========================================
    // بخش داشبورد آماری
    // ==========================================
    private VBox createDashboardPanel() {
        VBox layout = new VBox(25);
        layout.setPadding(new Insets(20));

        Label title = new Label("خلاصه وضعیت سیستم");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // ساخت یک کانتینر افقی برای نگهداری کارت‌های آماری
        HBox cardsBox = new HBox(20);
        
        // ایجاد سه کارت آماری برای کاربران، کل آگهی‌ها و آگهی‌های در انتظار
        VBox usersCard = createStatCard("کل کاربران سامانه", "در حال محاسبه...");
        VBox totalAdsCard = createStatCard("کل آگهی‌های ثبت شده", "در حال محاسبه...");
        VBox pendingAdsCard = createStatCard("آگهی‌های در انتظار بررسی", "در حال محاسبه...");

        cardsBox.getChildren().addAll(usersCard, totalAdsCard, pendingAdsCard);

        Button refreshBtn = new Button("بروزرسانی آمار");
        refreshBtn.setStyle("-fx-font-weight: bold;");
        refreshBtn.setOnAction(e -> loadDashboardStats(usersCard, totalAdsCard, pendingAdsCard));

        layout.getChildren().addAll(title, cardsBox, refreshBtn);

        // فراخوانی اولیه برای بارگذاری داده‌ها به محض باز شدن برنامه
        loadDashboardStats(usersCard, totalAdsCard, pendingAdsCard);

        return layout;
    }

    // متد کمکی برای ساختن ظاهر کارت‌های آماری (استایل‌دهی شده)
    private VBox createStatCard(String title, String initialValue) {
        VBox card = new VBox(10);
        // استفاده از استایل‌های CSS درون‌خطی برای زیبایی بیشتر
        card.setStyle("-fx-background-color: #f4f6f9; -fx-padding: 20px; " +
                      "-fx-border-radius: 8px; -fx-background-radius: 8px; " +
                      "-fx-border-color: #d1d5db; -fx-min-width: 200px; -fx-alignment: center;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b5563;");
        
        Label valueLabel = new Label(initialValue);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    // متدی برای دریافت داده‌ها از بک‌اند و تزریق آن‌ها به کارت‌ها
    private void loadDashboardStats(VBox usersCard, VBox totalAdsCard, VBox pendingAdsCard) {
        new Thread(() -> {
            // دریافت شناسه ادمین جاری برای ارسال به بک‌اند
            Long adminId = apiClient.getCurrentUser().getId();
            
            // فراخوانی متدهای دریافت تمام کاربران و تمام آگهی‌ها
            Response<List<User>> usersResp = apiClient.getAllUsers(adminId);
            Response<List<Ad>> adsResp = apiClient.getAllAdsForAdmin(adminId);
            
            Platform.runLater(() -> {
                // استخراج Labelهای مربوط به مقادیر (دومین فرزند هر کارت)
                Label usersVal = (Label) usersCard.getChildren().get(1);
                Label totalAdsVal = (Label) totalAdsCard.getChildren().get(1);
                Label pendingAdsVal = (Label) pendingAdsCard.getChildren().get(1);

                // تنظیم آمار کاربران
                if (usersResp.isSuccess()) {
                    usersVal.setText(String.valueOf(usersResp.getData().size()));
                } else {
                    usersVal.setText("خطا");
                }

                // تنظیم آمار آگهی‌ها و فیلتر کردن وضعیت‌ها
                if (adsResp.isSuccess()) {
                    List<Ad> allAds = adsResp.getData();
                    totalAdsVal.setText(String.valueOf(allAds.size()));
                    
                    // استفاده از Stream API برای شمارش آگهی‌های PENDING
                    long pendingCount = allAds.stream()
                        .filter(ad -> ad.getStatus() == Ad.AdStatus.PENDING)
                        .count();
                        
                    pendingAdsVal.setText(String.valueOf(pendingCount));
                } else {
                    totalAdsVal.setText("خطا");
                    pendingAdsVal.setText("خطا");
                }
            });
        }).start();
    }
}