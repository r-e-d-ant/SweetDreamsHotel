package SweetDreams.SweetDreamsHotel.service.impl;

import SweetDreams.SweetDreamsHotel.model.Room;
import SweetDreams.SweetDreamsHotel.repository.RoomRepository;
import SweetDreams.SweetDreamsHotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void saveRoom(Room room) {
        roomRepository.save(room);
    }

    @Override
    public Room getRoomByNumber(String roomNumber) {
        return roomRepository.findRoomByRoomNumber(roomNumber);
    }

    @Override
    public List<Room> getAllRoom() {
        return roomRepository.findAll();
    }

    @Override
    public void updateRoom(Room room) {
        roomRepository.save(room);
    }

    @Override
    public void removeRoom(Room room) {
        roomRepository.delete(room);
    }
}
