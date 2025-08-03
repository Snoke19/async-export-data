package snoke.asyncexportdata;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/async/users")
    public ResponseEntity<StreamingResponseBody> findAllAsync() {

        StreamingResponseBody responseBody = outputStream -> {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                 CSVWriter csvWriter = new CSVWriter(writer)
            ) {

                csvWriter.writeNext(defineHeader());

                int page = 0;
                Page<User> userPage;

                do {
                    userPage = userRepository.findAll(PageRequest.of(page++, 5000));
                    List<User> users = userPage.getContent();

                    for (User user : users) {
                        csvWriter.writeNext(defineRecord(user));
                    }

                    csvWriter.flush();
                    writer.flush();

                } while (userPage.hasNext());

            } catch (IOException ex) {
                log.error("Async stream closed by client: {}", ex.getMessage());
            } catch (Exception e) {
                log.error("Async exception", e);
            }
        };

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(responseBody);
    }

    @GetMapping("/users")
    public ResponseEntity<String> findAll() {

        try (Writer writer = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(writer)
        ) {

            csvWriter.writeNext(defineHeader());

            List<User> users = userRepository.findAll();

            for (User user : users) {
                csvWriter.writeNext(defineRecord(user));
            }

            csvWriter.flush();
            writer.flush();

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(writer.toString());

        } catch (IOException ex) {
            log.error("Sync stream closed by client: {}", ex.getMessage());
        } catch (Exception e) {
            log.error("Sync exception", e);
        }

        return ResponseEntity.ok().build();
    }

    private String[] defineRecord(User user) {
        return new String[]{
            user.getId(),
            user.getName(),
            user.getEmail(),
            String.valueOf(user.getAge()),
            String.valueOf(user.isActive()),
            user.getCreatedAt().toString(),
        };
    }

    private String[] defineHeader() {
        return new String[]{"ID", "Name", "Email", "Age", "Active", "CreatedAt"};
    }
}
