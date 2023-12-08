package SweetDreams.SweetDreamsHotel.controller;

import SweetDreams.SweetDreamsHotel.model.Customer;
import SweetDreams.SweetDreamsHotel.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // create new customer
    @PostMapping("/create")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        if (customer == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        /* check if user with that email is already there */
        Customer existingCustomerEmail = customerService.getCustomerByEmail(customer.getEmail());
        if (existingCustomerEmail != null && existingCustomerEmail.getEmail() != null && !existingCustomerEmail.getEmail().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FOUND);
        }
        /* ---------------------------------------------- */

        customer.setCreatedAt(customer.getCreatedAt());
        customerService.saveCustomer(customer);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // get one customer
    @GetMapping("/{customerId}/info")
    public ResponseEntity<?> oneCustomer(@PathVariable UUID customerId) {
        if (customerId == null)
            return new ResponseEntity<>("Missing customer Id", HttpStatus.BAD_REQUEST);
        Customer customer = customerService.getCustomerByCustomerId(customerId);
        return new ResponseEntity<>(customer, HttpStatus.FOUND);
    }

    @GetMapping("/{customerEmail}")
    public ResponseEntity<?> oneCustomerEmail(@PathVariable String customerEmail) {
        if (customerEmail == null)
            return new ResponseEntity<>("Missing customer email", HttpStatus.BAD_REQUEST);
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        if(customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(customer, HttpStatus.FOUND);
    }

    // get all customers
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Customer>> allCustomers() {
        List<Customer> customerList = customerService.getAllCustomer();
        return new ResponseEntity<>(customerList, HttpStatus.OK);
    }

    // update customer
    @PutMapping("/update/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modifyCustomer(@PathVariable UUID customerId, @RequestBody Customer customer) {
        if (customerId == null)
            return new ResponseEntity<>("Missing customer ID", HttpStatus.BAD_REQUEST);
        Customer existingCustomer = customerService.getCustomerByCustomerId(customerId);
        if (existingCustomer != null) {
            customer.setCustomerId(customer.getCustomerId());
            customerService.updateCustomer(customer);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        }
        return new ResponseEntity<>(customer, HttpStatus.NOT_FOUND);
    }

    // delete customer
    @DeleteMapping("/delete/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID customerId) {
        if (customerId == null)
            return new ResponseEntity<>("Missing customer ID", HttpStatus.BAD_REQUEST);
        Customer customer = customerService.getCustomerByCustomerId(customerId);
        if (customer != null) {
            customerService.removeCustomer(customerId);
            return new ResponseEntity<>("Customer deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("No such Customer", HttpStatus.NOT_FOUND);
    }
}
