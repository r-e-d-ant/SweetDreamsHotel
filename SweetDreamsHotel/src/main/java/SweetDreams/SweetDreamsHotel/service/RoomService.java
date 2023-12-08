package SweetDreams.SweetDreamsHotel.service;
import SweetDreams.SweetDreamsHotel.model.Room;

import java.util.List;

public interface RoomService {
    void saveRoom(Room room);
    Room getRoomByNumber(String roomNumber);
    List<Room> getAllRoom(); // get all rooms
    void updateRoom(Room room);
    void removeRoom(Room room);
}
