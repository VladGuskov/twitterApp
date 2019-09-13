package vladeater.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import vladeater.domain.Role;
import vladeater.domain.User;
import vladeater.repos.UserRepo;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Vlados Guskov
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private MailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void addUser() {
        User user = new User();
        user.setEmail("some@email.com");
        boolean isUserCreated = userService.addUser(user);
        assertTrue(isUserCreated);
        assertNotNull(user.getActivationCode());
        assertTrue(is(user.getRoles()).matches(Collections.singleton(Role.USER)));
        verify(userRepo, times(1)).save(user);
        verify(mailSender, times(1)).send(
                eq(user.getEmail()),
                anyString(),
                anyString()
        );
    }

    @Test
    public void addUserFailTest(){
        User user = new User();
        user.setUsername("Влад");
        doReturn(new User()).when(userRepo).findByUsername("Влад");
        boolean isUserCreated = userService.addUser(user);
        assertFalse(isUserCreated);
        verify(userRepo, times(0)).save(ArgumentMatchers.any(User.class));
        verify(mailSender, times(0)).send(
                anyString(),
                anyString(),
                anyString()
        );
    }

    @Test
    public void activateUser(){
        User user = new User();
        user.setActivationCode("code");
        doReturn(user).when(userRepo).findByActivationCode("activate");
        boolean isUserActivated = userService.activateUser("activate");
        assertTrue(isUserActivated);
        assertNull(user.getActivationCode());
        verify(userRepo, times(1)).save(user);
    }

    @Test
    public void activateUserFailTest(){
        boolean isUserActivated = userService.activateUser("activate");
        assertFalse(isUserActivated);
        verify(userRepo, times(0)).save(ArgumentMatchers.any(User.class));
    }
}