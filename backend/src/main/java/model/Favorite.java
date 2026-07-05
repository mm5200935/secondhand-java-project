package app.model;
import java.time.LocalDateTime;

public class Favorite {

    private int id;
    private User user;
    private Advertisement advertisement;
    private LocalDateTime addedAt;

    public Favorite() {
        addedAt = LocalDateTime.now();
    }

    public Favorite(int id,
                    User user,
                    Advertisement advertisement) {
        this();
        this.id = id;
        this.user = user;
        this.advertisement = advertisement;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAdvertisement(Advertisement adv) {
        this.advertisement = adv;
    }

    public User getUser() {
        return user;
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}