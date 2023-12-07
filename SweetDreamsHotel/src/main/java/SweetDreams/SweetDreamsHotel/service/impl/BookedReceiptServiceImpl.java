package SweetDreams.SweetDreamsHotel.service.impl;

import SweetDreams.SweetDreamsHotel.model.BookedReceipt;
import SweetDreams.SweetDreamsHotel.repository.BookedReceiptRepository;
import SweetDreams.SweetDreamsHotel.service.BookedReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookedReceiptServiceImpl implements BookedReceiptService {
    BookedReceiptRepository bookedReceiptRepository;

    @Autowired
    public BookedReceiptServiceImpl(BookedReceiptRepository bookedReceiptRepository) {
        this.bookedReceiptRepository = bookedReceiptRepository;
    }

    @Override
    public BookedReceipt saveBookedReceipt(BookedReceipt bookedReceipt) {
        return bookedReceiptRepository.save(bookedReceipt);
    }

    @Override
    public BookedReceipt getBookedReceiptByReceiptId(UUID receiptId) {
        return bookedReceiptRepository.getBookedReceiptByReceiptId(receiptId);
    }

    @Override
    public List<BookedReceipt> getAllBookedRoomReceipt() {
        return bookedReceiptRepository.findAll();
    }

    @Override
    public BookedReceipt updateBookedReceipt(BookedReceipt bookedReceipt) {
        return bookedReceiptRepository.save(bookedReceipt);
    }

    @Override
    public void removeBookedReceipt(UUID receiptId) {
        bookedReceiptRepository.deleteById(receiptId);
    }
}
