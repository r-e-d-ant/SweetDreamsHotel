package SweetDreams.SweetDreamsHotel.controller;

import SweetDreams.SweetDreamsHotel.BucketName;
import SweetDreams.SweetDreamsHotel.model.Enums.EStatus;
import SweetDreams.SweetDreamsHotel.model.Room;
import SweetDreams.SweetDreamsHotel.service.RoomService;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;

@RestController
@RequestMapping(value = "/room")
public class RoomController {
    private final RoomService roomService;
    private final AmazonS3 amazonS3;

    @Autowired
    public RoomController(RoomService roomService, AmazonS3 amazonS3) {
        this.roomService = roomService;
        this.amazonS3 = amazonS3;
    }

    public void upload(String path,
                       String fileName,
                       Optional<Map<String, String>> optionalMetaData,
                       InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });

        try {
            // Use a buffer to read the file in chunks
            byte[] buffer = new byte[1024];
            int bytesRead;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Convert byte array to BufferedImage
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
            BufferedImage originalImage = ImageIO.read(byteArrayInputStream);

            // Calculate the new height to maintain the aspect ratio
            int width = 800;
            int height = (int) (originalImage.getHeight() * (800.0 / originalImage.getWidth()));

            // Resize the image
            BufferedImage resizedImage = Thumbnails.of(originalImage)
                    .size(width, height)
                    .asBufferedImage();

            // Convert BufferedImage to InputStream
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
            ImageIO.write(resizedImage, formatName, os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            // Rest of your code remains the same
            amazonS3.putObject(path, fileName, is, objectMetadata);

            byteArrayInputStream.close();
            is.close();
            outputStream.close();
            os.close();
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException(e);
        }
    }


    // create new room
    @PostMapping(value="/create", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRoom(@RequestPart("room") String roomString,
                                        @RequestParam("roomImg") MultipartFile file) throws JsonProcessingException {
        Room room = new ObjectMapper().readValue(roomString, Room.class);
        System.out.println(room);
        if (room == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        if (uploadImageToBucket(file, room)) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        /* check if room with that nr is already there */
        Room existingRoomNr = roomService.getRoomByNumber(room.getRoomNumber());
        if (existingRoomNr != null && existingRoomNr.getRoomNumber() != null && !existingRoomNr.getRoomNumber().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FOUND);
        }
        /* ---------------------------------------------- */
        room.setCreatedAt(room.getCreatedAt());
        room.setERoomType(room.getERoomType());
        room.setEStatus(EStatus.AVAILABLE);
        System.out.println(room);
        roomService.saveRoom(room);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    // get one room
    @GetMapping("/{roomNr}")
    public ResponseEntity<?> oneRoom(@PathVariable String roomNr) {
        if (roomNr == null)
            return new ResponseEntity<>("Missing room number", HttpStatus.BAD_REQUEST);
        Room room = roomService.getRoomByNumber(roomNr);
        return new ResponseEntity<>(room, HttpStatus.FOUND);
    }

    // get all rooms
    @GetMapping("/all")
    public ResponseEntity<List<Room>> allRooms() {
        List<Room> roomList = roomService.getAllRoom();
        return new ResponseEntity<>(roomList, HttpStatus.OK);
    }

    // update room
    @PutMapping(value = "/{roomNr}/update", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modifyUser(@PathVariable String roomNr, @RequestPart("room") String roomString,
                                        @RequestParam("roomImg") MultipartFile file) throws JsonProcessingException {
        Room room = new ObjectMapper().readValue(roomString, Room.class);
        if (roomNr == null)
            return new ResponseEntity<>("Missing room Nr", HttpStatus.BAD_REQUEST);
        Room existingRoom = roomService.getRoomByNumber(roomNr);
        if (existingRoom != null) {
            if (room.getRoomNumber() != null) {
                existingRoom.setRoomNumber(room.getRoomNumber());
            }
            if (room.getERoomType() != null) {
                existingRoom.setERoomType(room.getERoomType());
            }
            if (room.getMaxPerson() != null) {
                existingRoom.setMaxPerson(room.getMaxPerson());
            }
            if (room.getBeds() != null) {
                existingRoom.setBeds(room.getBeds());
            }
            if (room.getRoomSize() != null) {
                existingRoom.setRoomSize(room.getRoomSize());
            }
            if (room.getPrice() != null) {
                existingRoom.setPrice(room.getPrice());
            }
            if (room.getRoomImg() != null) {
                existingRoom.setRoomImg(room.getRoomImg());
            }

            if (uploadImageToBucket(file, existingRoom)) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            roomService.updateRoom(existingRoom);
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        return new ResponseEntity<>(room, HttpStatus.NOT_FOUND);
    }

    private boolean uploadImageToBucket(@RequestParam("roomImg") MultipartFile file, Room room) {
        try {
            /* save it to amazon bucket */

            // get file metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("Content-Type", file.getContentType());
            metadata.put("Content-Length", String.valueOf(file.getSize()));

            // Save Image in S3
            UUID uniqueKey = UUID.randomUUID();
            String path = String.format("%s/%s", BucketName.SWEET_DREAMS_HOTEL_IMAGE.getBucketName(), uniqueKey);
            String fileName = String.format("%s", file.getOriginalFilename());

            try {
                upload(path, fileName, Optional.of(metadata), file.getInputStream());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to upload file", e);
            }

            // Construct the S3 URL
            String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s/%s", BucketName.SWEET_DREAMS_HOTEL_IMAGE.getBucketName(), Regions.EU_NORTH_1.getName(), uniqueKey.toString(), fileName);

            // Set the image name in the Room entity
            room.setRoomImg(s3Url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return true;
        }
        return false;
    }

    // delete room
    @DeleteMapping("/{roomNr}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String roomNr) {
        if (roomNr == null)
            return new ResponseEntity<>("Missing room Nr", HttpStatus.BAD_REQUEST);
        Room room = roomService.getRoomByNumber(roomNr);
        if (room != null) {
            roomService.removeRoom(room);
            return new ResponseEntity<>("Room deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("No such room", HttpStatus.NOT_FOUND);
    }
}
