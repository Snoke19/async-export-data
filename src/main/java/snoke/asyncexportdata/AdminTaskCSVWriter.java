package snoke.asyncexportdata;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminTaskCSVWriter {

    public List<String[]> writeToCSV(List<User> entries) {

        List<String> header = new ArrayList<>();
        header.add("ID");
        header.add("Name");
        header.add("Email");
        header.add("Age");
        header.add("Active");
        header.add("CreatedAt");

        List<String[]> list = new ArrayList<>();
        list.add(header.toArray(new String[0]));

        for (User entry : entries) {
            List<String> stringEntry = new ArrayList<>();
            stringEntry.add(entry.getId());
            stringEntry.add(entry.getName());
            stringEntry.add(entry.getEmail());
            stringEntry.add(String.valueOf(entry.getAge()));
            stringEntry.add(String.valueOf(entry.isActive()));
            stringEntry.add(entry.getCreatedAt().toString());

            list.add(stringEntry.toArray(new String[0]));
        }

        return list;
    }
}
