package SweetDreams.SweetDreamsHotel.repository;

import SweetDreams.SweetDreamsHotel.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Customer findCustomerByCustomerId(UUID customerId);
    Customer getCustomerByEmail(String email);
}
