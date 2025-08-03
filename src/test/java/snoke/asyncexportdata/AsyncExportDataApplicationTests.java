package snoke.asyncexportdata;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AsyncExportDataApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    public void beforeAll() {
        if (userRepository.count() >= 50000) {
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

    @Test
    public void contextLoads() {

        ResponseEntity<String> response = restTemplate.getForEntity("/users", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("text/csv", Objects.requireNonNull(response.getHeaders().getContentType()).toString());

        String csv = response.getBody();

        Assertions.assertNotNull(csv);
        System.out.println(csv.length());
    }
}
