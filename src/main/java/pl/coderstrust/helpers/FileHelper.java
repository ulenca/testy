package pl.coderstrust.helpers;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.stereotype.Component;

@Component
public class FileHelper {

    private static final Charset ENCODING = UTF_8;

    public void writeLine(String filePath, String line) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        FileUtils.writeLines(new File(filePath), ENCODING.name(), Collections.singleton(line), true);
    }

    public List<String> readLines(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        return FileUtils.readLines(new File(filePath), ENCODING);
    }

    public void create(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        Files.createFile(Paths.get(filePath));
    }

    public void delete(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        if (!Files.exists(Paths.get(filePath))) {
            throw new NoSuchFileException("File does not exist");
        }
        Files.delete(Paths.get(filePath));
    }

    public boolean exist(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        return Files.exists(Paths.get(filePath));
    }

    public boolean isEmpty(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        if (!Files.exists(Paths.get(filePath))) {
            throw new NoSuchFileException("File does not exist");
        }
        return Files.size(Paths.get(filePath)) == 0L;
    }

    public void clear(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        FileUtils.write(new File(filePath), "", ENCODING);
    }

    public void removeLine(String filePath, int lineNumber) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number cannot be lower then one");
        }
        File file = new File(filePath);
        List<String> lines = FileUtils.readLines(file, ENCODING);
        if (lineNumber > lines.size()) {
            throw new IllegalArgumentException("Line number cannot be greater than number of lines in file");
        }
        lines.remove(lineNumber - 1);
        FileUtils.writeLines(file, ENCODING.name(), lines, false);
    }

    public String readLastLine(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(filePath), UTF_8)) {
            return reader.readLine();
        }
    }
}
