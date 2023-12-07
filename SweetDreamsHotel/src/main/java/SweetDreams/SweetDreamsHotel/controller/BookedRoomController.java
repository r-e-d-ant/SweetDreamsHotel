package SweetDreams.SweetDreamsHotel.controller;

import SweetDreams.SweetDreamsHotel.model.BookedRoom;
import SweetDreams.SweetDreamsHotel.model.Customer;
import SweetDreams.SweetDreamsHotel.model.Room;
import SweetDreams.SweetDreamsHotel.service.BookedRoomService;
import SweetDreams.SweetDreamsHotel.service.CustomerService;
import SweetDreams.SweetDreamsHotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public BookedRoomController(BookedRoomService bookedRoomService, RoomService roomService) {
        this.bookedRoomService = bookedRoomService;
        this.roomService = roomService;
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
        bookedRoomService.saveBookedRoom(bookedRoom);
        return new ResponseEntity<>(bookedRoom, HttpStatus.CREATED);
    }

    // get one booked room
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> oneRoom(@PathVariable UUID bookingId) {
        if (bookingId == null)
            return new ResponseEntity<>("Missing booking Id", HttpStatus.BAD_REQUEST);
        BookedRoom bookedRoom = bookedRoomService.getBookedRoomByBookingId(bookingId);
        return new ResponseEntity<>(bookedRoom, HttpStatus.FOUND);
    }

    // get all booked room
    @GetMapping("/all")
    public ResponseEntity<List<BookedRoom>> allBookedRooms() {
        List<BookedRoom> bookedRoomList = bookedRoomService.getAllBookedRooms();
        return new ResponseEntity<>(bookedRoomList, HttpStatus.OK);
    }

    // update booked room
    @PutMapping("/update/{bookingId}")
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
    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID bookingId) {
        if (bookingId == null)
            return new ResponseEntity<>("Missing booking Id", HttpStatus.BAD_REQUEST);
        BookedRoom bookedRoom = bookedRoomService.getBookedRoomByBookingId(bookingId);
        if (bookedRoom != null) {
            bookedRoomService.removeBookedRoom(bookingId);
            return new ResponseEntity<>("Booked Room deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("No such Booked Room", HttpStatus.NOT_FOUND);
    }
}
