package service.impl;

import enums.AdvertisementStatus;
import enums.UserStatus;
import model.Advertisement;
import model.User;
import repository.interfaces.AdvertisementRepository;
import repository.interfaces.UserRepository;
import service.interfaces.AdminService;

import java.util.List;

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

        advertisement.setStatus(AdvertisementStatus.ACTIVE);
        advertisementRepository.update(advertisement);
    }

    @Override
    public void rejectAdvertisement(Advertisement advertisement) {

        if (advertisement == null) {
            throw new RuntimeException("Advertisement not found.");
        }

        advertisement.setStatus(AdvertisementStatus.REJECTED);
        advertisementRepository.update(advertisement);
    }

    @Override
    public void deleteAdvertisement(Advertisement advertisement) {

        if (advertisement == null) {
            throw new RuntimeException("Advertisement not found.");
        }

        advertisement.setStatus(AdvertisementStatus.DELETED);
        advertisementRepository.update(advertisement);
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