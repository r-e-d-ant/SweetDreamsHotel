package SweetDreams.SweetDreamsHotel.service;
import SweetDreams.SweetDreamsHotel.model.BookedRoom;
import SweetDreams.SweetDreamsHotel.model.Customer;

import java.util.List;
import java.util.UUID;

public interface BookedRoomService {
    BookedRoom saveBookedRoom(BookedRoom bookedRoom);
    BookedRoom getBookedRoomByBookingId(UUID bookingId);
    List<BookedRoom> getAllBookedRooms();
    List<BookedRoom> allBookedRoomsByCustomer(Customer customer);
    BookedRoom updateBookedRoom(BookedRoom bookedRoom);
    void removeBookedRoom(UUID bookingId);
}
