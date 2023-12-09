package SweetDreams.SweetDreamsHotel.service.impl;

import SweetDreams.SweetDreamsHotel.model.BookedRoom;
import SweetDreams.SweetDreamsHotel.model.Customer;
import SweetDreams.SweetDreamsHotel.repository.BookedRoomRepository;
import SweetDreams.SweetDreamsHotel.service.BookedRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookedRoomServiceImpl implements BookedRoomService {
    BookedRoomRepository bookedRoomRepository;

    @Autowired
    public BookedRoomServiceImpl(BookedRoomRepository bookedRoomRepository) {
        this.bookedRoomRepository = bookedRoomRepository;
    }

    @Override
    public BookedRoom saveBookedRoom(BookedRoom bookedRoom) {
        return bookedRoomRepository.save(bookedRoom);
    }

    @Override
    public BookedRoom getBookedRoomByBookingId(UUID bookingId) {
        return bookedRoomRepository.getBookedRoomByBookingId(bookingId);
    }

    @Override
    public List<BookedRoom> getAllBookedRooms() {
        return bookedRoomRepository.findAll();
    }

    @Override
    public List<BookedRoom> allBookedRoomsByCustomer(Customer customer) {
        return bookedRoomRepository.getBookedRoomByCustomer(customer);
    }

    @Override
    public BookedRoom updateBookedRoom(BookedRoom bookedRoom) {
        return bookedRoomRepository.save(bookedRoom);
    }

    @Override
    public void removeBookedRoom(UUID bookingId) {
        bookedRoomRepository.deleteById(bookingId);
    }
}
