package SweetDreams.SweetDreamsHotel.repository;

import SweetDreams.SweetDreamsHotel.model.BookedRoom;
import SweetDreams.SweetDreamsHotel.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookedRoomRepository extends JpaRepository<BookedRoom, UUID> {
    BookedRoom getBookedRoomByBookingId(UUID bookingId);
    List<BookedRoom> getBookedRoomByCustomer(Customer customer);
}
