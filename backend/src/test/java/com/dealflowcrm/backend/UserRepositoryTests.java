package com.dealflowcrm.backend;

import com.dealflowcrm.backend.entities.User;
import com.dealflowcrm.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser() {
        // Create a new user with test data
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setRole(User.UserRole.SALESREP);
        user.setStatus(User.UserStatus.Active);

        // Save the user to the database
        User savedUser = userRepository.save(user);

        // Verify the user was saved correctly with all properties
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@test.com");
        assertThat(savedUser.getFirstName()).isEqualTo("Test");
        assertThat(savedUser.getLastName()).isEqualTo("Test");
        assertThat(savedUser.getLastLogin()).isNull();
        assertThat(savedUser.getRole()).isEqualTo(User.UserRole.SALESREP);
        assertThat(savedUser.getStatus()).isEqualTo(User.UserStatus.Active);
    }

    @Test
    public void testUpdateUser() {
        // Create and save a user
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setRole(User.UserRole.SALESREP);
        user.setStatus(User.UserStatus.Active);
        User savedUser = userRepository.save(user);

        // Retrieve the user and update properties
        User updatedUser = userRepository.findById(savedUser.getUserId()).get();
        updatedUser.setEmail("updated@test.com");
        updatedUser.setPassword("updatedpassword");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Updated");

        // Save the changes
        userRepository.save(updatedUser);

        // Verify the updates were persisted
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
        assertThat(updatedUser.getPassword()).isEqualTo("updatedpassword");
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        assertThat(updatedUser.getLastName()).isEqualTo("Updated");
    }

    @Test
    public void testDeleteUser() {
        // Create and save a user
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setRole(User.UserRole.SALESREP);
        user.setStatus(User.UserStatus.Active);
        User savedUser = userRepository.save(user);

        // Retrieve the user to delete
        User deletedUser = userRepository.findById(savedUser.getUserId()).get();

        // Delete the user
        userRepository.delete(deletedUser);

        // Verify the user has been deleted (no users in database)
        assertThat(userRepository.count()).isZero();
    }


    @Test
    public void testFindByEmail() {
        // Create and save a user
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setRole(User.UserRole.SALESREP);
        user.setStatus(User.UserStatus.Active);
        User savedUser = userRepository.save(user);

        // Verify user can be found by email
        assertThat(userRepository.findByEmail("test@test.com")).isPresent();
        assertThat(userRepository.findByEmail("test@test.com").get()).isEqualTo(savedUser);
    }


    @Test
    public void testFindTop10ByOrderByCreatedAtDesc() {
        // Create 15 users with different creation timestamps
        for (int i = 0; i < 15; i++) {
            User user = new User();
            user.setEmail("user" + i + "@test.com");
            user.setPassword("password");
            user.setFirstName("First" + i);
            user.setLastName("Last" + i);
            user.setRole(User.UserRole.SALESREP);
            user.setStatus(User.UserStatus.Active);
            userRepository.save(user);

            // Add a small delay to ensure different timestamps
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        // Get the top 10 most recently created users
        List<User> recentUsers = userRepository.findTop10ByOrderByCreatedAtDesc();

        // Verify we got exactly 10 users
        assertThat(recentUsers).hasSize(10);

        // Verify they are in descending order by creation timestamp
        for (int i = 0; i < recentUsers.size() - 1; i++) {
            Timestamp current = recentUsers.get(i).getCreatedAt();
            Timestamp next = recentUsers.get(i + 1).getCreatedAt();
            assertThat(current).isAfterOrEqualTo(next);
        }
    }

    @Test
    public void testFindByRole() {
        // Create users with different roles
        User salesRep1 = new User();
        salesRep1.setEmail("salesrep1@test.com");
        salesRep1.setPassword("password");
        salesRep1.setFirstName("Sales");
        salesRep1.setLastName("Rep1");
        salesRep1.setRole(User.UserRole.SALESREP);
        salesRep1.setStatus(User.UserStatus.Active);
        userRepository.save(salesRep1);

        User salesRep2 = new User();
        salesRep2.setEmail("salesrep2@test.com");
        salesRep2.setPassword("password");
        salesRep2.setFirstName("Sales");
        salesRep2.setLastName("Rep2");
        salesRep2.setRole(User.UserRole.SALESREP);
        salesRep2.setStatus(User.UserStatus.Active);
        userRepository.save(salesRep2);

        User manager = new User();
        manager.setEmail("manager@test.com");
        manager.setPassword("password");
        manager.setFirstName("Sales");
        manager.setLastName("Manager");
        manager.setRole(User.UserRole.SALESMANAGER);
        manager.setStatus(User.UserStatus.Active);
        userRepository.save(manager);

        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword("password");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(User.UserRole.ADMIN);
        admin.setStatus(User.UserStatus.Active);
        userRepository.save(admin);

        // Test finding by SALESREP role
        List<User> salesReps = userRepository.findByRole(User.UserRole.SALESREP);
        assertThat(salesReps).hasSize(2);
        assertThat(salesReps).extracting(User::getEmail).containsExactlyInAnyOrder("salesrep1@test.com", "salesrep2@test.com");

        // Test finding by SALESMANAGER role
        List<User> managers = userRepository.findByRole(User.UserRole.SALESMANAGER);
        assertThat(managers).hasSize(1);
        assertThat(managers.get(0).getEmail()).isEqualTo("manager@test.com");

        // Test finding by ADMIN role
        List<User> admins = userRepository.findByRole(User.UserRole.ADMIN);
        assertThat(admins).hasSize(1);
        assertThat(admins.get(0).getEmail()).isEqualTo("admin@test.com");

        // Test finding by non-existent role should return empty list
        List<User> nonExistent = userRepository.findByRole(null);
        assertThat(nonExistent).isEmpty();
    }

    @Test
    public void testFindByStatus() {
        // Create users with different statuses
        User activeUser1 = new User();
        activeUser1.setEmail("active1@test.com");
        activeUser1.setPassword("password");
        activeUser1.setFirstName("Active");
        activeUser1.setLastName("User1");
        activeUser1.setRole(User.UserRole.SALESREP);
        activeUser1.setStatus(User.UserStatus.Active);
        userRepository.save(activeUser1);

        User activeUser2 = new User();
        activeUser2.setEmail("active2@test.com");
        activeUser2.setPassword("password");
        activeUser2.setFirstName("Active");
        activeUser2.setLastName("User2");
        activeUser2.setRole(User.UserRole.SALESREP);
        activeUser2.setStatus(User.UserStatus.Active);
        userRepository.save(activeUser2);

        User inactiveUser = new User();
        inactiveUser.setEmail("inactive@test.com");
        inactiveUser.setPassword("password");
        inactiveUser.setFirstName("Inactive");
        inactiveUser.setLastName("User");
        inactiveUser.setRole(User.UserRole.SALESREP);
        inactiveUser.setStatus(User.UserStatus.Inactive);
        userRepository.save(inactiveUser);

        // Test finding active users
        List<User> activeUsers = userRepository.findByStatus(User.UserStatus.Active);
        assertThat(activeUsers).hasSize(2);
        assertThat(activeUsers).extracting(User::getEmail).containsExactlyInAnyOrder("active1@test.com", "active2@test.com");

        // Test finding inactive users
        List<User> inactiveUsers = userRepository.findByStatus(User.UserStatus.Inactive);
        assertThat(inactiveUsers).hasSize(1);
        assertThat(inactiveUsers.get(0).getEmail()).isEqualTo("inactive@test.com");

        // Test finding by non-existent status should return empty list
        List<User> nonExistent = userRepository.findByStatus(null);
        assertThat(nonExistent).isEmpty();
    }

    @Test
    public void testUserConstructor() {
        // Create a user with the constructor
        User user = new User("test@example.com", "password123", "John", "Doe",
                User.UserRole.SALESREP, User.UserStatus.Active);

        // Save the user to verify the constructor creates a valid entity
        User savedUser = userRepository.save(user);

        // Verify all properties were set correctly
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("password123");
        assertThat(savedUser.getFirstName()).isEqualTo("John");
        assertThat(savedUser.getLastName()).isEqualTo("Doe");
        assertThat(savedUser.getRole()).isEqualTo(User.UserRole.SALESREP);
        assertThat(savedUser.getStatus()).isEqualTo(User.UserStatus.Active);

        // Verify timestamps were generated
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();

        // Last login should be null as I didn't set it
        assertThat(savedUser.getLastLogin()).isNull();
    }


}
