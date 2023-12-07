package SweetDreams.SweetDreamsHotel.repository;

import SweetDreams.SweetDreamsHotel.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookedRoomRepository extends JpaRepository<BookedRoom, UUID> {
    BookedRoom getBookedRoomByBookingId(UUID bookingId);
}
