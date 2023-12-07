package SweetDreams.SweetDreamsHotel.service;
import SweetDreams.SweetDreamsHotel.model.Room;
import SweetDreams.SweetDreamsHotel.model.SweetDreamsUser;

import java.util.List;

public interface RoomService {
    Room saveRoom(Room room);
    Room getRoomByNumber(String roomNumber);
    List<Room> getAllRoom(); // get all rooms
    Room updateRoom(Room room);
    Room removeRoom(Room room);
}
