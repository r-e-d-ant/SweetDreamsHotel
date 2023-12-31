package SweetDreams.SweetDreamsHotel.controller;

import SweetDreams.SweetDreamsHotel.MailService;
import SweetDreams.SweetDreamsHotel.model.BookedRoom;
import SweetDreams.SweetDreamsHotel.model.BookedReceipt;
import SweetDreams.SweetDreamsHotel.model.Enums.EBillStatus;
import SweetDreams.SweetDreamsHotel.model.Enums.EStatus;
import SweetDreams.SweetDreamsHotel.model.Room;
import SweetDreams.SweetDreamsHotel.service.BookedReceiptService;
import SweetDreams.SweetDreamsHotel.service.BookedRoomService;
import SweetDreams.SweetDreamsHotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/booked-receipt")
@PreAuthorize("hasRole('ADMIN')")
public class BookedReceiptController {
    BookedReceiptService bookedReceiptService;
    BookedRoomService bookedRoomService;
    RoomService roomService;
    MailService mailService;

    @Autowired
    public BookedReceiptController(BookedReceiptService bookedReceiptService, BookedRoomService bookedRoomService, RoomService roomService, MailService mailService) {
        this.bookedReceiptService = bookedReceiptService;
        this.bookedRoomService = bookedRoomService;
        this.roomService = roomService;
        this.mailService = mailService;
    }

    // register new booked room receipt
    @PostMapping("/register")
    public ResponseEntity<?> createBookedReceipt(@RequestBody BookedReceipt bookedReceipt) {
        if (bookedReceipt == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        /* ----------- get all foreign keys and add them -------------- */
        // Booked Room
        BookedRoom bookedRoom = bookedRoomService.getBookedRoomByBookingId(bookedReceipt.getBookedRoom().getBookingId());
        bookedReceipt.setBookedRoom(bookedRoom);
        /* ---------------------------------------------------------- */
        bookedReceiptService.saveBookedReceipt(bookedReceipt);
        Room room = roomService.getRoomByNumber(bookedRoom.getRoom().getRoomNumber());
        // update room status to taken
        room.setEStatus(EStatus.AVAILABLE);
        roomService.updateRoom(room);
        // update booked room status to BILLED
        bookedRoom.setEBillStatus(EBillStatus.BILLED);
        bookedRoomService.updateBookedRoom(bookedRoom);

        // Calculate total nights
        long totalNights = ChronoUnit.DAYS.between(bookedRoom.getCheckinDate(), bookedRoom.getCheckoutDate());

        // Calculate total price
        double totalPrice = totalNights * room.getPrice();

        mailService.sendEmail(bookedRoom.getCustomer().getEmail(),
                "Sweet Dreams Hotel - Booked Room Receipt",
                "<h2>Dear "+bookedRoom.getCustomer().getFullname() +" below is your room bill.</h2>" +
                        "<p>Hotel Room Nr: "+room.getRoomNumber()+"</p>" +
                        "<p>Hotel Room Type: "+room.getERoomType()+"</p>" +
                        "<p>Check In date: "+bookedRoom.getCheckinDate()+" Check Out date: "+bookedRoom.getCheckoutDate()+"" +
                        "<p>Nights: "+totalNights+"</p>" +
                        "<p>Total Price: "+totalPrice+"$</p>" +
                        "<h2>Welcome back.</h2>");
        return new ResponseEntity<>(bookedReceipt, HttpStatus.CREATED);
    }

    // get one receipt
    @GetMapping("/{receiptId}")
    public ResponseEntity<?> oneReceipt(@PathVariable UUID receiptId) {
        if (receiptId == null)
            return new ResponseEntity<>("Missing Receipt Id", HttpStatus.BAD_REQUEST);
        BookedReceipt bookedReceipt = bookedReceiptService.getBookedReceiptByReceiptId(receiptId);
        return new ResponseEntity<>(bookedReceipt, HttpStatus.FOUND);
    }

    // get all receipt
    @GetMapping("/all")
    public ResponseEntity<List<BookedReceipt>> allBookedReceipts() {
        List<BookedReceipt> bookedReceiptList = bookedReceiptService.getAllBookedRoomReceipt();
        return new ResponseEntity<>(bookedReceiptList, HttpStatus.OK);
    }

    // update receipt
    @PutMapping("/update/{receiptId}")
    public ResponseEntity<?> modifyReceipt(@PathVariable UUID receiptId, @RequestBody BookedReceipt bookedReceipt) {
        if (receiptId == null)
            return new ResponseEntity<>("Missing receipt Id", HttpStatus.BAD_REQUEST);
        BookedReceipt existingReceipt = bookedReceiptService.getBookedReceiptByReceiptId(receiptId);
        if (existingReceipt != null) {
            existingReceipt.setReceiptId(bookedReceipt.getReceiptId());
            bookedReceiptService.updateBookedReceipt(bookedReceipt);
            return new ResponseEntity<>(bookedReceipt, HttpStatus.OK);
        }
        return new ResponseEntity<>(bookedReceipt, HttpStatus.NOT_FOUND);
    }

    // delete receipt
    @DeleteMapping("/delete/{receiptId}")
    public ResponseEntity<?> deleteReceipt(@PathVariable UUID receiptId) {
        if (receiptId == null)
            return new ResponseEntity<>("Missing receipt Id", HttpStatus.BAD_REQUEST);
        BookedReceipt bookedReceipt = bookedReceiptService.getBookedReceiptByReceiptId(receiptId);
        if (bookedReceipt != null) {
            bookedReceiptService.removeBookedReceipt(receiptId);
            return new ResponseEntity<>("Receipt deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("No such Receipt", HttpStatus.NOT_FOUND);
    }
}
