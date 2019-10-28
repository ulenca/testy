package pl.coderstrust.fileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileHelperIT {

    private static final String INPUT_FILE = "src/main/resources/input_file.txt";
    private static final String EXPECTED_FILE = "src/main/resources/expected_file.txt";
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
    void readLinesFromPathMethodShouldReadLinesFromFile() throws IOException {
        FileHelper.writeLine(INPUT_FILE, "Something");
        List<String> result = FileHelper.readLines(INPUT_FILE);
        List<String> expected = new ArrayList<String>();
        expected.add("Something");
        assertEquals(expected, result);
    }

    @Test
    void writeLinesFromFilePathMethodShouldThrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.writeLine(null, "SomeLineOfString"));
        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void writeLinesFromFilePathMethodShouldThrowExceptionForNullAsStringLine() {
        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeLine(INPUT_FILE, null));
    }

    @Test
    void writeLinesFromFilePathMethodShouldWriteSomeValueInFile() throws IOException {
        String lineToAdd = "something";
        FileUtils.writeLines(expectedFile, Collections.singleton(lineToAdd), true);
        FileHelper.writeLine(INPUT_FILE, lineToAdd);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void isEmptyFromFilePathMethodShouldThrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.isEmpty(null));
        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void isEmptyFromFilePathMethodShouldReturnFalse() throws IOException {
        FileHelper.create(INPUT_FILE);
        assertTrue(FileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void isEmptyFromFilePathMethodShouldReturnTrue() throws IOException {
        FileHelper.writeLine(INPUT_FILE, "123");
        FileHelper.clear(INPUT_FILE);
        assertTrue(FileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void clearFromFilePathMethodShouldTrowExceptionForNullAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.clear(null));
        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void createFromFilePathMethodShouldThrowExceptionForNUllAsFilePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.create(null));
        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void createFromFilePathMethodShouldCreateNewFile() throws IOException {
        FileHelper.create(INPUT_FILE);
        assertTrue(Files.exists(Paths.get(INPUT_FILE)));
    }
    @Test
    void removeLineFromFilePathMethodShouldThrowExceptionForNullAsFilePath(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.removeLine(null,2));
        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void removeLineFromFilePathMethodShouldThrowExceptionForNullAsLineNumber(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.removeLine(INPUT_FILE,0));
        assertEquals("Line number cannot be lower then one", exception.getMessage());
    }

    @Test
    void removeLineFromFilePathMethodShouldRemoveLine() throws IOException {
       FileHelper.writeLine(INPUT_FILE,"This is first line");
       FileHelper.removeLine(INPUT_FILE,1);
       assertFalse(FileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void existFromFilePathMethodShouldThrowExceptionForNullAsFilePath(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.exist(null));
        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void existFromFilePathMethodShouldReturnFalseWhenFileDoNotExist(){
        assertFalse(FileHelper.exist(INPUT_FILE));
    }

    @Test
    void existFromFilePathMethodShouldReturnTrueWhenFileExist() throws IOException {
        FileHelper.create(INPUT_FILE);
        assertTrue(FileHelper.exist(INPUT_FILE));
    }

    @Test
    void deleteFromFilePathMethodShouldThrowExceptionForNullAsFilePath(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.delete(null));
        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void deleteFromFilePathMethodShouldDeleteFile() throws IOException {
        FileHelper.create(INPUT_FILE);
        FileHelper.delete(INPUT_FILE);
        assertFalse(FileHelper.exist(INPUT_FILE));
    }

    @Test
    void readLastLineFromFilePathMethodShouldThrowExceptionForNullAsFilePath(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> FileHelper.readLastLine(null));
        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void readLastLineFromFilePathMethodShouldReadLastLineFromFile() throws IOException {
    FileHelper.writeLine(INPUT_FILE,"FirstLine");
    FileHelper.writeLine(INPUT_FILE,"SecondLine");
    String expected = "SecondLine";
    assertEquals(expected,FileHelper.readLastLine(INPUT_FILE));
    }
}
