package SweetDreams.SweetDreamsHotel.repository;

import SweetDreams.SweetDreamsHotel.model.BookedReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookedReceiptRepository extends JpaRepository<BookedReceipt, UUID> {
    BookedReceipt getBookedReceiptByReceiptId(UUID bookingId);
}
