package SweetDreams.SweetDreamsHotel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketName {
    SWEET_DREAMS_HOTEL_IMAGE("spring-sweet-dreams-storage");
    private final String bucketName;
}