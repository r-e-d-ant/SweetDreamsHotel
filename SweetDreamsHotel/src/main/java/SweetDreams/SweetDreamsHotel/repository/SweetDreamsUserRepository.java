package SweetDreams.SweetDreamsHotel.repository;

import SweetDreams.SweetDreamsHotel.model.SweetDreamsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SweetDreamsUserRepository extends JpaRepository<SweetDreamsUser, UUID> {
    SweetDreamsUser getSweetDreamsUserByUserEmail(String userEmail);
    SweetDreamsUser getSweetDreamsUserByUserId(UUID userId);
}
