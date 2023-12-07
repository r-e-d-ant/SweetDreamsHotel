package SweetDreams.SweetDreamsHotel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class BookedReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID receiptId;
    @ManyToOne
    private BookedRoom bookedRoom;
    private Double totalPrice;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate billedDate;
}
