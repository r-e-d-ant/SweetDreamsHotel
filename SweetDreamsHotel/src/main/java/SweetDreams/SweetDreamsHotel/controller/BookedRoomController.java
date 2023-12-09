package SweetDreams.SweetDreamsHotel.controller;

import SweetDreams.SweetDreamsHotel.MailService;
import SweetDreams.SweetDreamsHotel.model.BookedRoom;
import SweetDreams.SweetDreamsHotel.model.Customer;
import SweetDreams.SweetDreamsHotel.model.Enums.EBillStatus;
import SweetDreams.SweetDreamsHotel.model.Enums.EStatus;
import SweetDreams.SweetDreamsHotel.model.Room;
import SweetDreams.SweetDreamsHotel.service.BookedRoomService;
import SweetDreams.SweetDreamsHotel.service.CustomerService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/booked-room")
public class BookedRoomController {
    BookedRoomService bookedRoomService;
    RoomService roomService;
    CustomerService customerService;
    MailService mailService;

    @Autowired
    public BookedRoomController(BookedRoomService bookedRoomService, RoomService roomService, CustomerService customerService, MailService mailService) {
        this.bookedRoomService = bookedRoomService;
        this.roomService = roomService;
        this.customerService = customerService;
        this.mailService = mailService;
    }


    // register new booked room
    @PostMapping("/book")
    public ResponseEntity<?> createBookedRoom(@RequestBody BookedRoom bookedRoom) {
        if (bookedRoom == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        /* ----------- get all foreign keys and add them -------------- */
        // Room
        Room room = roomService.getRoomByNumber(bookedRoom.getRoom().getRoomNumber());
        if (room.getEStatus().toString().equals("TAKEN")) {
            return new ResponseEntity<>(HttpStatus.FOUND);
        }
        bookedRoom.setRoom(room);
        // Customer
        Customer customer = customerService.getCustomerByCustomerId(bookedRoom.getCustomer().getCustomerId());
        bookedRoom.setCustomer(customer);
        /* ---------------------------------------------------------- */
        // update booked room status to PENDING
        bookedRoom.setEBillStatus(EBillStatus.PENDING);
        bookedRoomService.saveBookedRoom(bookedRoom);

        // update room status to taken
        room.setEStatus(EStatus.TAKEN);
        roomService.updateRoom(room);

        // send email confirmation.
        mailService.sendEmail(customer.getEmail(),
                "Sweet Dreams Hotel - Room Booking",
                "<h2>Dear "+customer.getFullname() +" your room is successfully booked, Happy stay</h2><p></p><p>Check In Date: "+bookedRoom.getCheckinDate()+" Check Out Date: "+bookedRoom.getCheckoutDate()+"</p>");
        return new ResponseEntity<>(bookedRoom, HttpStatus.CREATED);
    }

    // get one booked room
    @GetMapping("/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> oneRoom(@PathVariable UUID bookingId) {
        if (bookingId == null)
            return new ResponseEntity<>("Missing booking Id", HttpStatus.BAD_REQUEST);
        BookedRoom bookedRoom = bookedRoomService.getBookedRoomByBookingId(bookingId);
        return new ResponseEntity<>(bookedRoom, HttpStatus.FOUND);
    }

    // get all booked room
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookedRoom>> allBookedRooms() {
        List<BookedRoom> bookedRoomList = bookedRoomService.getAllBookedRooms();
        return new ResponseEntity<>(bookedRoomList, HttpStatus.OK);
    }

    @GetMapping("/{customerId}/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BookedRoom>> allBookedRoomsByCustomer(@PathVariable UUID customerId) {
        Customer customer = customerService.getCustomerByCustomerId(customerId);
        List<BookedRoom> bookedRoomList = bookedRoomService.allBookedRoomsByCustomer(customer);
        return new ResponseEntity<>(bookedRoomList, HttpStatus.OK);
    }

    // update booked room
    @PutMapping("/{bookingId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modifyUser(@PathVariable UUID bookingId, @RequestBody BookedRoom bookedRoom) {
        if (bookingId == null)
            return new ResponseEntity<>("Missing booking Id", HttpStatus.BAD_REQUEST);
        BookedRoom existingBooking = bookedRoomService.getBookedRoomByBookingId(bookingId);
        if (existingBooking != null) {
            existingBooking.setBookingId(bookedRoom.getBookingId());
            bookedRoomService.updateBookedRoom(bookedRoom);
            return new ResponseEntity<>(bookedRoom, HttpStatus.OK);
        }
        return new ResponseEntity<>(bookedRoom, HttpStatus.NOT_FOUND);
    }

    // delete room
    @DeleteMapping("/{bookingId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable UUID bookingId) {
        if (bookingId == null)
            return new ResponseEntity<>("Missing booking Id", HttpStatus.BAD_REQUEST);
        BookedRoom bookedRoom = bookedRoomService.getBookedRoomByBookingId(bookingId);
        if (bookedRoom != null) {
            bookedRoomService.removeBookedRoom(bookingId);
            Room room = roomService.getRoomByNumber(bookedRoom.getRoom().getRoomNumber());
            // update room status to taken
            room.setEStatus(EStatus.AVAILABLE);
            roomService.updateRoom(room);

            // update booked room status to CANCELLED
            bookedRoom.setEBillStatus(EBillStatus.CANCELLED);
            bookedRoomService.updateBookedRoom(bookedRoom);
            // send email confirmation.
            mailService.sendEmail(bookedRoom.getCustomer().getEmail(),
                    "Sweet Dreams Hotel - Room Booking",
                    "<h2>Dear "+bookedRoom.getCustomer().getEmail() +" Your Room is cancelled, Welcome back</h2>");
            return new ResponseEntity<>("Booked Room cancelled", HttpStatus.OK);
        }
        return new ResponseEntity<>("No such Booked Room", HttpStatus.NOT_FOUND);
    }
}
