package snoke.asyncexportdata;

import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class CsvResponseUtils {

    public static String csvDataToResponse(List<String[]> csvData) throws IOException {

        Writer writer = new StringWriter();
        CSVWriter csvLineWriter = new CSVWriter(
            writer,
            CSVWriter.DEFAULT_SEPARATOR,
            CSVWriter.DEFAULT_QUOTE_CHARACTER,
            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
            CSVWriter.DEFAULT_LINE_END
        );

        csvLineWriter.writeAll(csvData);

        csvLineWriter.flush();
        csvLineWriter.close();

        return writer.toString();
    }
}
