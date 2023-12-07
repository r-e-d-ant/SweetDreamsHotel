package SweetDreams.SweetDreamsHotel.model;

import SweetDreams.SweetDreamsHotel.model.Enums.ERoomType;
import SweetDreams.SweetDreamsHotel.model.Enums.EStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Room {
    @Id
    private String roomNumber;
    private ERoomType eRoomType;
    private String roomImg;
    private Integer maxPerson;
    private Integer beds;
    private Double roomSize;
    private Double price;
    private EStatus eStatus;
    @CreationTimestamp
    private LocalDate createdAt;
}
