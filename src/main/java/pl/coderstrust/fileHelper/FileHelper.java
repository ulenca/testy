package pl.coderstrust.fileHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileHelper {
    /*
    Create class for writing/reading strings to/from file. In my opinion this class should contain following methods:
- void create(Strng filePath)                           DONE+TEST
- void delete(Strng filePath)                           DONE+TEST
- boolean exists(String filePath);                      DONE+TEST
- boolean isEmpty(String filePath)                      DONE+TEST
- void clear(String filePath)                           DONE+TEST
- void writeLine(String filePath, String line)          DONE+TEST
- List<String> readLines(String filePath)               DONE+TEST
- String readLastLine(String filePath)                  DOING
- void removeLine(String filePath, int lineNumber)      DONE+TEST
     */

    private static final Charset ENCODING = UTF_8;

    static void writeLine(String filePath, String line) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        FileUtils.writeLines(new File(filePath), ENCODING.name(), Collections.singleton(line), true);
    }

    static List<String> readLines(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        return FileUtils.readLines(new File(filePath), ENCODING);
    }

    static void create(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        Files.createFile(Paths.get(filePath));
    }

    static void delete(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        Files.delete(Paths.get(filePath));
    }

    static boolean exist(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        return Files.exists(Paths.get(filePath));
    }

    static boolean isEmpty(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        return Files.size(Paths.get(filePath)) == 0L;
    }

    static void clear(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        FileUtils.write(new File(filePath), "", ENCODING);
    }

    static void removeLine(String filePath, int lineNumber) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number cannot be lower then one");
        }
        File file = new File(filePath);
        List<String> lines = FileUtils.readLines(file, ENCODING);
        lines.remove(lineNumber - 1);
        FileUtils.writeLines(file, ENCODING.name(), Collections.singleton(lines), false);

    }

    static String readLastLine(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
//        ReversedLinesFileReader.
        List<String> result = new ArrayList<>();
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(filePath), StandardCharsets.UTF_8)) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        return String.valueOf(result);
    }
}
}
