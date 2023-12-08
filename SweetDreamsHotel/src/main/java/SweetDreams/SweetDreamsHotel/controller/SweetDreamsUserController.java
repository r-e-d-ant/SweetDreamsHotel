package SweetDreams.SweetDreamsHotel.controller;

import SweetDreams.SweetDreamsHotel.JwtTokenProvider;
import SweetDreams.SweetDreamsHotel.MailService;
import SweetDreams.SweetDreamsHotel.model.Enums.EUserRole;
import SweetDreams.SweetDreamsHotel.model.SweetDreamsUser;
import SweetDreams.SweetDreamsHotel.service.SweetDreamsUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/sweet-dream-user")
public class SweetDreamsUserController {
    private final JwtTokenProvider jwtTokenProvider;
    private final SweetDreamsUserService sweetDreamsUserService;
    private final MailService mailService;

    @Autowired
    public SweetDreamsUserController(JwtTokenProvider jwtTokenProvider, SweetDreamsUserService sweetDreamsUserService, MailService mailService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.sweetDreamsUserService = sweetDreamsUserService;
        this.mailService = mailService;
    }

    // create new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SweetDreamsUser sweetDreamsUser) {
        if (sweetDreamsUser == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        /* check if user with that email is already there */
        SweetDreamsUser existingEmail = sweetDreamsUserService.getUserByEmail(sweetDreamsUser.getUserEmail());
        if (existingEmail != null && existingEmail.getUserEmail() != null && !existingEmail.getUserEmail().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FOUND);
        }
        /* ---------------------------------------------- */

        sweetDreamsUser.setEUserRole(EUserRole.CUSTOMER); // set the role
        sweetDreamsUser.setCreatedAt(sweetDreamsUser.getCreatedAt());

        // encrypt password using bcrypt and save it
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String enteredPassword = sweetDreamsUser.getUserPassword();
        String hashedPassword = passwordEncoder.encode(enteredPassword);

        sweetDreamsUser.setUserPassword(hashedPassword);
        sweetDreamsUserService.saveUser(sweetDreamsUser);

        // send email confirmation.
        mailService.sendEmail(sweetDreamsUser.getUserEmail(),
                "Sweet Dreams Hotel - Account Signup",
                "<h2>Your account is created successfully</h2><p>Please proceed to give your full information before booking a room.</p><p><a href='http://localhost:3000/customer'>Click here to provide full information</a></p>");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // get one user
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<?> oneUser(@PathVariable String userEmail) {
        if (userEmail == null)
            return new ResponseEntity<>("Missing user email", HttpStatus.BAD_REQUEST);
        SweetDreamsUser sweetDreamsUser = sweetDreamsUserService.getUserByEmail(userEmail);
        return new ResponseEntity<>(sweetDreamsUser, HttpStatus.FOUND);
    }

    // get all users
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SweetDreamsUser>> allUsers() {
        List<SweetDreamsUser> sweetDreamsUserList = sweetDreamsUserService.getAllUsers();
        return new ResponseEntity<>(sweetDreamsUserList, HttpStatus.OK);
    }

    // update user
    @PutMapping("/update/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modifyUser(@PathVariable UUID userId, @RequestBody SweetDreamsUser sweetDreamsUser) {
        if (userId == null)
            return new ResponseEntity<>("Missing user email", HttpStatus.BAD_REQUEST);
        SweetDreamsUser user = sweetDreamsUserService.getUserByID(userId);
        if (user != null) {
            sweetDreamsUser.setUserId(user.getUserId());
            sweetDreamsUserService.updateUser(sweetDreamsUser);
            return new ResponseEntity<>(sweetDreamsUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(sweetDreamsUser, HttpStatus.NOT_FOUND);
    }

    // delete user
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        if (userId == null)
            return new ResponseEntity<>("Missing user Id", HttpStatus.BAD_REQUEST);
        SweetDreamsUser user = sweetDreamsUserService.getUserByID(userId);
        if (user != null) {
            sweetDreamsUserService.removeUser(userId);
            return new ResponseEntity<>("Account deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("No such account", HttpStatus.NOT_FOUND);
    }

    // login existing user
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody SweetDreamsUser sweetDreamsUser, HttpServletRequest request, HttpServletResponse response) {
        SweetDreamsUser existingUserEmail = sweetDreamsUserService.getUserByEmail(sweetDreamsUser.getUserEmail());

        if (existingUserEmail != null && existingUserEmail.getUserEmail() != null && !existingUserEmail.getUserEmail().isEmpty()) {
            String hashedPassword = existingUserEmail.getUserPassword();

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            if (passwordEncoder.matches(sweetDreamsUser.getUserPassword(), hashedPassword)) {
                UUID usr_id = existingUserEmail.getUserId();
                String usr_email = existingUserEmail.getUserEmail();
                String user_role = String.valueOf(existingUserEmail.getEUserRole());
                // generate JWT token
                String token = jwtTokenProvider.createToken(usr_email, user_role);

                // set token as HttpOnly cookie
                Cookie cookie = new Cookie("sweetDreamsUserToken", token);
                cookie.setHttpOnly(true);
                response.addCookie(cookie);

                // include role in the response body
                Map<String, Object> responseBody = new HashMap<>();
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
