package SweetDreams.SweetDreamsHotel.service;
import SweetDreams.SweetDreamsHotel.model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    void saveCustomer(Customer customer);
    Customer getCustomerByCustomerId(UUID customerId);
    Customer getCustomerByEmail(String email);
    List<Customer> getAllCustomer();
    void updateCustomer(Customer customer);
    void removeCustomer(UUID customerId);
}
