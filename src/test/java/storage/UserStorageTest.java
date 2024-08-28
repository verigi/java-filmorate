package storage;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.storage.InMemoryUserService;

import java.util.Collections;
import java.util.List;

public class UserStorageTest extends StorageTest {
    InMemoryUserService service = new InMemoryUserService();

    @BeforeEach
    @Override
    public void init() {
        service.deleteAllUsers();
        service.addUser(user_1);
        service.addUser(user_2);
    }

    @Test
    @DisplayName("Adding user to user storage")
    public void shouldAddUserToUserStorage() {
        service.addUser(user_3);
        Assertions.assertEquals(3, service.getAllUsers().size());
    }

    @Test
    @DisplayName("Adding user with empty name")
    public void shouldPlaceLoginAsName() {
        service.addUser(user_4);
        Assertions.assertEquals("test_login_4", service.getUserById(3).getName());
    }

    @Test
    @DisplayName("Updating existing user")
    public void shouldUpdateDetailsOfUserProfile() {
        user_1.setName("Test_Film_1_Upd");
        service.updateUser(user_1);
        Assertions.assertEquals("Test_Film_1_Upd", user_1.getName());
    }

    @Test
    @DisplayName("Deleting user from user storage")
    public void shouldDeleteUserFromUserStorage() {
        service.deleteUserById(1);
        Assertions.assertEquals(1, service.getAllUsers().size());
        Assertions.assertEquals(List.of(user_2), service.getAllUsers());
    }

    @Test
    @DisplayName("Getting user from user storage")
    public void shouldReturnCertainUserFromStorage() {
        Assertions.assertEquals(user_1, service.getUserById(1));
    }

    @Test
    @DisplayName("Clearing user`s storage")
    public void shouldCleanUsersStorage() {
        service.addUser(user_3);
        service.addUser(user_4);
        service.deleteAllUsers();
        Assertions.assertEquals(Collections.EMPTY_LIST, service.getAllUsers());
    }
}
