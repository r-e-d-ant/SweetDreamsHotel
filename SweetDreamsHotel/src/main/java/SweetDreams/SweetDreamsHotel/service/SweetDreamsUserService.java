package SweetDreams.SweetDreamsHotel.service;
import SweetDreams.SweetDreamsHotel.model.SweetDreamsUser;

import java.util.List;
import java.util.UUID;

public interface SweetDreamsUserService {
    void saveUser(SweetDreamsUser sweetDreamsUser); // save user
    SweetDreamsUser getUserByID(UUID userId); // get one user
    SweetDreamsUser getUserByEmail(String userEmail); // get one user
    List<SweetDreamsUser> getAllUsers(); // get all users
    void updateUser(SweetDreamsUser sweetDreamsUser); // update user
    void removeUser(UUID userId); // remove user
}
