package app.service.interfaces;
import app.model.Advertisement;
import app.model.User;
import java.util.List;

public interface AdminService {


    void blockUser(User user);

    void unblockUser(User user);

    List<User> getAllUsers();

    List<Advertisement> getPendingAdvertisements();

    void approveAdvertisement(Advertisement advertisement);

    void rejectAdvertisement(Advertisement advertisement);

    void deleteAdvertisement(Advertisement advertisement);

    int getTotalUsers();

    int getTotalAdvertisements();

}