package SweetDreams.SweetDreamsHotel.controller;

import SweetDreams.SweetDreamsHotel.JwtTokenProvider;
import SweetDreams.SweetDreamsHotel.model.Customer;
import SweetDreams.SweetDreamsHotel.service.CustomerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/sweet-dream-user")
public class SweetDreamsUserController {
    private final JwtTokenProvider jwtTokenProvider;
    CustomerService customerService;

    @Autowired
    public SweetDreamsUserController(JwtTokenProvider jwtTokenProvider, CustomerService customerService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customerService = customerService;
    }

    // login existing user
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Customer customer, HttpServletRequest request, HttpServletResponse response) {
        Customer existingCustomerEmail = customerService.getCustomerByEmail(customer.getEmail());

        if (existingCustomerEmail != null && existingCustomerEmail.getEmail() != null && !existingCustomerEmail.getEmail().isEmpty()) {
            String hashedPassword = existingCustomerEmail.getUserPassword();

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            if (passwordEncoder.matches(customer.getUserPassword(), hashedPassword)) {
                UUID customer_id = existingCustomerEmail.getCustomerId();
                String usr_email = existingCustomerEmail.getEmail();
                String user_role = String.valueOf(existingCustomerEmail.getEUserRole());
                // generate JWT token
                String token = jwtTokenProvider.createToken(usr_email, user_role);

                // set token as HttpOnly cookie
                Cookie cookie = new Cookie("sweetDreamsUserToken", token);
                System.out.println(token);
                cookie.setHttpOnly(true);
                response.addCookie(cookie);

                // include role in the response body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("customer_id", customer_id);
                responseBody.put("usr_email", usr_email);
                responseBody.put("role", user_role);

                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        // Create a new cookie that will immediately expire
        Cookie cookie = new Cookie("sweetDreamsUserToken", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // The cookie will immediately expire
        response.addCookie(cookie);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
