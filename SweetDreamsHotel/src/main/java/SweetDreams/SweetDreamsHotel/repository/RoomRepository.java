package SweetDreams.SweetDreamsHotel.repository;
import SweetDreams.SweetDreamsHotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findRoomByRoomNumber(String roomNr);
}
