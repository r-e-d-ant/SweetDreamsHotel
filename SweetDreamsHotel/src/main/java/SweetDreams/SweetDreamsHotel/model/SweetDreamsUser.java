package SweetDreams.SweetDreamsHotel.model;

import SweetDreams.SweetDreamsHotel.model.Enums.EUserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SweetDreamsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    private String userEmail;
    private String userPassword;
    private EUserRole eUserRole;
    @CreationTimestamp
    private LocalDate createdAt;
}
