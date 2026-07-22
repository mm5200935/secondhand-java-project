package app.service.impl;

import app.enums.AdvertisementStatus;
import app.enums.UserStatus;
import app.model.Advertisement;
import app.model.User;
import app.repository.interfaces.AdvertisementRepository;
import app.repository.interfaces.UserRepository;
import app.service.interfaces.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            AdvertisementRepository advertisementRepository) {
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
    }



    @Override
    public void blockUser(User user) {

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        user.setStatus(UserStatus.BLOCKED);
        userRepository.update(user);
    }

    @Override
    public void unblockUser(User user) {

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.update(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



    @Override
    public List<Advertisement> getPendingAdvertisements() {
        return advertisementRepository.findByStatus(AdvertisementStatus.PENDING);
    }

    @Override
    public void approveAdvertisement(Advertisement advertisement) {

        if (advertisement == null) {
            throw new RuntimeException("Advertisement not found.");
        }

        advertisementRepository.updateStatus(
                advertisement.getId(),
                AdvertisementStatus.ACTIVE
        );
    }

    @Override
    public void rejectAdvertisement(Advertisement advertisement) {

        if (advertisement == null) {
            throw new RuntimeException("Advertisement not found.");
        }

        advertisementRepository.updateStatus(
                advertisement.getId(),
                AdvertisementStatus.REJECTED
        );
    }

    @Override
    public void deleteAdvertisement(Advertisement advertisement) {

        if (advertisement == null) {
            throw new RuntimeException("Advertisement not found.");
        }

        advertisementRepository.updateStatus(
                advertisement.getId(),
                AdvertisementStatus.DELETED
        );
    }

    @Override
    public int getTotalUsers() {
        return userRepository.findAll().size();
    }

    @Override
    public int getTotalAdvertisements() {
        return advertisementRepository.findAll().size();
    }
}