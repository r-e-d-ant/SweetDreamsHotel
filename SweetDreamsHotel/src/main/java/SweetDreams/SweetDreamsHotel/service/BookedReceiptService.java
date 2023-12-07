package SweetDreams.SweetDreamsHotel.service;
import SweetDreams.SweetDreamsHotel.model.BookedReceipt;

import java.util.List;
import java.util.UUID;

public interface BookedReceiptService {
    BookedReceipt saveBookedReceipt(BookedReceipt bookedReceipt);
    BookedReceipt getBookedReceiptByReceiptId(UUID receiptId);
    List<BookedReceipt> getAllBookedRoomReceipt();
    BookedReceipt updateBookedReceipt(BookedReceipt bookedReceipt);
    void removeBookedReceipt(UUID receiptId);
}
