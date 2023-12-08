package SweetDreams.SweetDreamsHotel.model;

import SweetDreams.SweetDreamsHotel.model.Enums.EBillStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BookedRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;
    @ManyToOne
    private Room room;
    @ManyToOne
    private Customer customer;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkinDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkoutDate;
    private EBillStatus eBillStatus;
}
