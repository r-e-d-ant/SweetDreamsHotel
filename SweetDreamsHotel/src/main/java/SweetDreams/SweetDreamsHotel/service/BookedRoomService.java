package SweetDreams.SweetDreamsHotel.service;
import SweetDreams.SweetDreamsHotel.model.BookedRoom;

import java.util.List;
import java.util.UUID;

public interface BookedRoomService {
    BookedRoom saveBookedRoom(BookedRoom bookedRoom);
    BookedRoom getBookedRoomByBookingId(UUID bookingId);
    List<BookedRoom> getAllBookedRooms();
    BookedRoom updateBookedRoom(BookedRoom bookedRoom);
    void removeBookedRoom(UUID bookingId);
}
