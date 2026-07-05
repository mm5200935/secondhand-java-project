package app.model;
import app.enums.AdvertisementStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Advertisement {

    private int id;
    private String title;
    private String description;
    private double price;
    private Category category;
    private City city;
    private User owner;
    private AdvertisementStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean negotiable;
    private List<AdvertisementImage> images;

    public Advertisement() {
        images = new ArrayList<>();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = AdvertisementStatus.PENDING;

        category = new Category();
        city = new City();
    }

    public Advertisement(int id,
                         String title,
                         String description,
                         double price,
                         Category category,
                         City city,
                         User seller,
                         boolean negotiable) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.city = city;
        this.owner = seller;
        this.negotiable = negotiable;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setStatus(AdvertisementStatus adv) {
        this.status = adv;
    }

    public AdvertisementStatus getStatus() {
        return this.status;
    }

    public int getId() {
        return this.id;
    }

    public User getOwner() {
        return this.owner;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public double getPrice() {
        return this.price;
    }

    public void setAdvId(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Category getCategory() {
        return category;
    }

    public City getCity() {
        return city;
    }

    public boolean isNegotiable() {
        return negotiable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setNegotiable(boolean negotiable) {
        this.negotiable = negotiable;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCategoryId() {
        return category.getId();
    }

    public void setCategoryId(int categoryId) {
        this.category.setId(categoryId);
    }

    public void setCityId(int id) {
        this.city.setId(id);
    }

    public int getCityId() {
        return this.city.getId();
    }


}