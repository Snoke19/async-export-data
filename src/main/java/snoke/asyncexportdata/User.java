package snoke.asyncexportdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private int age;

    private boolean active;

    private LocalDateTime createdAt;

    public User(String name, String email, int age, boolean active, LocalDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.active = active;
        this.createdAt = createdAt;
    }
}
