package pl.coderstrust.fileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

class FileHelperIT {

    private static final String INPUT_FILE = "src/main/resources/input_file.txt";
    private static final String EXPECTED_FILE = "src/main/resources/expected_file.txt";
    private static final Charset ENCODING = UTF_8;
    private FileHelper fileHelper;
    private File inputFile;
    private File expectedFile;

    @BeforeEach
    void setup() {
        fileHelper = new FileHelper();
        inputFile = new File(INPUT_FILE);
        expectedFile = new File(EXPECTED_FILE);
        if (inputFile.exists()) {
            inputFile.delete();
        }
        if (expectedFile.exists()) {
            expectedFile.delete();
        }
    }

    @Test
    void readLinesFromPathMethodShouldThrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.readLines(null));

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void shouldReadLinesFromFile() throws IOException {
        List<String> lines = Arrays.asList(
                "ID",
                "22/2019",
                "2019-06-25",
                "2019-07-25",
                "Seller's details",
                "Buyer's details"
        );
        FileUtils.writeLines(new File(INPUT_FILE), ENCODING.name(), lines, true);

        List<String> result = FileHelper.readLines(INPUT_FILE);

        assertEquals(lines, result);
    }

    @Test
    void writeLineMethodShouldThrowExceptionForNullAsLine() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.writeLine(null, "SomeLineOfString"));

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void writeLinesFromFilePathMethodShouldThrowExceptionForNullAsStringLine() {
        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeLine(INPUT_FILE, null));
    }

    @Test
    void shouldWriteLineToFile() throws IOException {
        String lineToAdd = "something";

        FileUtils.writeLines(expectedFile, Collections.singleton(lineToAdd), true);
        FileHelper.writeLine(INPUT_FILE, lineToAdd);

        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void isEmptyMethodShouldThrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.isEmpty(null));

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void isEmptyMethodShouldThrowExceptionForNonExistingFile() {
        Exception exception = assertThrows(NoSuchFileException.class, () -> FileHelper.isEmpty(INPUT_FILE));

        assertEquals("File does not exist", exception.getMessage());
    }

    @Test
    void shouldReturnTrueIfFileIsEmpty() throws IOException {
        FileHelper.create(INPUT_FILE);

        assertTrue(FileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void shouldReturnFalseIfFileIsNotEmpty() throws IOException {
        FileHelper.writeLine(INPUT_FILE, "Bla bla bla");

        assertFalse(FileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void clearMethodShouldTrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.clear(null));

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void shouldClearFile() throws IOException {
        FileHelper.writeLine(INPUT_FILE, "SomeLines");

        FileHelper.clear(INPUT_FILE);

        assertTrue(FileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void createMethodShouldThrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.create(null));

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void shouldCreateFile() throws IOException {
        FileHelper.create(INPUT_FILE);

        assertTrue(Files.exists(Paths.get(INPUT_FILE)));
    }

    @Test
    void removeLineMethodShouldThrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.removeLine(null, 2));

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"-1, Line number cannot be lower then one"
            , "0, Line number cannot be lower then one"
            , "1, This file cannot remove this line because this line does not exist"})
    void removeLineShouldThrowExceptionForInvalidLineNumber(int lineNumber, String exceptionMessage) throws IOException {
        FileHelper.create(INPUT_FILE);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.removeLine(INPUT_FILE, lineNumber));

        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void shouldRemoveLineFromFile() throws IOException {
        FileUtils.writeLines(inputFile, ENCODING.name(), Arrays.asList("bla1", "bla2", "bla3"), true);
        FileUtils.writeLines(expectedFile, ENCODING.name(), Arrays.asList("bla2", "bla3"), true);

        FileHelper.removeLine(INPUT_FILE, 1);

        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void existMethodShouldThrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.exist(null));

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void shouldReturnFalseIfFileDoesNotExist() {
        assertFalse(FileHelper.exist(INPUT_FILE));
    }

    @Test
    void shouldReturnTrueIfFileExists() throws IOException {
        FileHelper.create(INPUT_FILE);

        assertTrue(FileHelper.exist(INPUT_FILE));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.delete(null));

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void shouldDeleteExistingFile() throws IOException {
        inputFile.createNewFile();

        FileHelper.delete(INPUT_FILE);

        assertFalse(FileHelper.exist(INPUT_FILE));
    }

    @Test
    void readLastLineMethodShouldThrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.readLastLine(null));

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void shouldReadLastLineFromFile() throws IOException {
        FileUtils.writeLines(inputFile, ENCODING.name(), Arrays.asList("Seller's details", "2019-06-25", "Buyer's details"), false);

        assertEquals("Buyer's details", FileHelper.readLastLine(INPUT_FILE));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNonExistingFile() {
        Exception exception = assertThrows(NoSuchFileException.class, () -> FileHelper.delete(INPUT_FILE));

        assertEquals("File does not exist", exception.getMessage());
    }
}
