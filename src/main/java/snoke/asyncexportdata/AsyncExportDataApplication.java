package snoke.asyncexportdata;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class AsyncExportDataApplication implements CommandLineRunner {

    private final UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(AsyncExportDataApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() >= 1_000_000) {
            System.out.println("Users already seeded.");
            return;
        }

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            User user = new User(
                "User" + i,
                "user" + i + "@example.com",
                18 + (i % 50),
                i % 2 == 0,
                LocalDateTime.now().minusDays(i % 365)
            );
            users.add(user);

            if (users.size() == 1000) {
                userRepository.saveAll(users);
                users.clear();
                System.out.println("Inserted 1,000 users...");
            }
        }

        if (!users.isEmpty()) {
            userRepository.saveAll(users);
            System.out.println("Inserted remaining users.");
        }

        System.out.println("Finished inserting 50,000 users.");
    }
}
