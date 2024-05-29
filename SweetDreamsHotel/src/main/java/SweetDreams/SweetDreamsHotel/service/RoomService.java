package SweetDreams.SweetDreamsHotel.service;
import SweetDreams.SweetDreamsHotel.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RoomService {
    void saveRoom(Room room) throws IOException;
    Room getRoomByNumber(String roomNumber);
    List<Room> getAllRoom(); // get all rooms
    void updateRoom(Room room);
    void removeRoom(Room room);
}
