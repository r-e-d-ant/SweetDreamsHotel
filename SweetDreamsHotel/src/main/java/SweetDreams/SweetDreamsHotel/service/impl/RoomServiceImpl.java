package SweetDreams.SweetDreamsHotel.service.impl;

import SweetDreams.SweetDreamsHotel.model.Room;
import SweetDreams.SweetDreamsHotel.repository.RoomRepository;
import SweetDreams.SweetDreamsHotel.service.RoomService;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {
    RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void saveRoom(Room room) throws IOException {
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
