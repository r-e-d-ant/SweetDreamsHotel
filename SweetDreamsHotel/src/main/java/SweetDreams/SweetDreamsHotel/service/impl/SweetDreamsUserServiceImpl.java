package SweetDreams.SweetDreamsHotel.service.impl;
import SweetDreams.SweetDreamsHotel.model.SweetDreamsUser;
import SweetDreams.SweetDreamsHotel.repository.SweetDreamsUserRepository;
import SweetDreams.SweetDreamsHotel.service.SweetDreamsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SweetDreamsUserServiceImpl implements SweetDreamsUserService {
    SweetDreamsUserRepository sweetDreamsUserRepository;

    @Autowired
    private SweetDreamsUserServiceImpl(SweetDreamsUserRepository sweetDreamsUserRepository) {
        this.sweetDreamsUserRepository = sweetDreamsUserRepository;
    }

    @Override
    public void saveUser(SweetDreamsUser sweetDreamsUser) {
        sweetDreamsUserRepository.save(sweetDreamsUser);
    }

    @Override
    public SweetDreamsUser getUserByID(UUID userId) {
        return sweetDreamsUserRepository.getSweetDreamsUserByUserId(userId);
    }

    @Override
    public SweetDreamsUser getUserByEmail(String userEmail) {
        return sweetDreamsUserRepository.getSweetDreamsUserByUserEmail(userEmail);
    }

    @Override
    public List<SweetDreamsUser> getAllUsers() {
        return sweetDreamsUserRepository.findAll();
    }

    @Override
    public void updateUser(SweetDreamsUser sweetDreamsUser) {
        sweetDreamsUserRepository.save(sweetDreamsUser);
    }

    @Override
    public void removeUser(UUID userId) {
        sweetDreamsUserRepository.deleteById(userId);
    }
}
