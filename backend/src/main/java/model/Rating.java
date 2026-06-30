package model;

import java.time.LocalDateTime;

public class Rating {

    private int id;
    private User buyer;
    private User seller;
    private Advertisement advertisement;
    private int score;
    private String comment;
    private LocalDateTime createdAt;

    public Rating() {
        createdAt = LocalDateTime.now();
    }

    public Rating(int id,
                  User buyer,
                  User seller,
                  Advertisement advertisement,
                  int score,
                  String comment) {

        this();
        this.id = id;
        this.buyer = buyer;
        this.seller = seller;
        this.advertisement = advertisement;
        this.score = score;
        this.comment = comment;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public void setAdvertisement(Advertisement adv) {
        this.advertisement = adv;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getBuyer() {
        return this.buyer;
    }

    public int getScore() {
        return this.score;
    }



}