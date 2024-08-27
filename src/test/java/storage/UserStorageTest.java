package storage;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserService;
import ru.yandex.practicum.filmorate.storage.utils.UserInterface;

import java.util.Collection;
import java.util.List;

public class UserStorageTest extends StorageTest {

    InMemoryUserService userService;

    @BeforeEach
    @Override
    public void init() {
        userService = new InMemoryUserService();
        userService.addUser(user_1);
        userService.addUser(user_2);
    }

    @Test
    @DisplayName("Adding user to user storage")
    public void shouldAddUserToUserStorage() {
        userService.addUser(user_3);
        Assertions.assertEquals(3, userService.getAllUsers().size());
    }

    @Test
    @DisplayName("Adding user with empty name")
    public void shouldPlaceLoginAsName(){
        userService.addUser(user_4);
        Assertions.assertEquals("test_login_4", userService.getUserById(3).getName());
    }

    @Test
    @DisplayName("Updating existing user")
    public void shouldUpdateDetailsOfUserProfile() {
        user_1.setName("Test_Film_1_Upd");
        userService.updateUser(user_1);
        Assertions.assertEquals("Test_Film_1_Upd", user_1.getName());
    }

    @Test
    @DisplayName("Deleting user from user storage")
    public void shouldDeleteUserFromUserStorage() {
        userService.deleteUserById(1);
        Assertions.assertEquals(1, userService.getAllUsers().size());
        Assertions.assertEquals(List.of(user_2), userService.getAllUsers());
    }

    @Test
    @DisplayName("Getting user from user storage")
    public void shouldReturnCertainUserFromStorage() {
        Assertions.assertEquals(user_1, userService.getUserById(1));
    }




}
