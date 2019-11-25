package com.lab5com.file;

import org.apache.logging.log4j.core.util.Assert;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.lab5com.util.PhenixConstant.TEMP_DIR;

class BufferedReaderWrapperTest {

    private String testFilename = "test.txt";
    private String fileContent = "a\nb\nz\nm\nc\nac\nab";
    private File file;
    private BufferedReader br;
    private BufferedReaderWrapper wrapper;

    @BeforeEach
    public void setUp() throws IOException {
        file = writeFile(testFilename, fileContent);
        br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
        wrapper = new BufferedReaderWrapper(br);
    }

    @AfterEach
    public void tearDown() throws IOException {
        String[] testFiles = {testFilename};
        for (String file : testFiles) {
            Files.deleteIfExists(Paths.get(TEMP_DIR, file));
        }
        if (br != null) {
            br.close();
        }
    }

    @Test
    public void testPeek() throws IOException {
        assertThat(wrapper.peek(), IsEqual.equalTo("a"));
        assertThat(wrapper.peek(), IsEqual.equalTo("a"));
    }

    @Test
    public void testPeekAndPoll() throws IOException {
        assertThat(wrapper.peek(), IsEqual.equalTo("a"));
        assertThat(wrapper.poll(), IsEqual.equalTo("a"));
        assertThat(wrapper.peek(), IsEqual.equalTo("b"));
        assertThat(wrapper.poll(), IsEqual.equalTo("b"));
    }

    @Test
    public void testPoll() throws IOException {
        assertThat(wrapper.poll(), IsEqual.equalTo("a"));
        assertThat(wrapper.poll(), IsEqual.equalTo("b"));
        assertThat(wrapper.poll(), IsEqual.equalTo("z"));
        assertThat(wrapper.poll(), IsEqual.equalTo("m"));
    }

    @Test
    public void testGetTerm() throws IOException {
        assertThat(wrapper.peek(), IsEqual.equalTo(wrapper.getTerm()));
        assertThat(wrapper.getTerm(), IsEqual.equalTo("a"));
        wrapper.poll();
        assertThat(wrapper.peek(), IsEqual.equalTo(wrapper.getTerm()));
        assertThat(wrapper.getTerm(), IsEqual.equalTo("b"));
    }

    @Test
    public void testIsEmpty() throws IOException {
        assertThat(wrapper.isEmpty(), IsEqual.equalTo(Boolean.FALSE));
    }

    @Test
    public void testIsEmptyTrue() throws IOException {
        while (!wrapper.isEmpty()) {
            wrapper.poll();
        }
        assertThat(wrapper.isEmpty(), IsEqual.equalTo(Boolean.TRUE));
    }

    @Test
    public void testClose() throws IOException {
        wrapper.close();
    }

    private File writeFile(String filename, String fileContent) {
        String tempFilesDir = TEMP_DIR;
        File file = Paths.get(tempFilesDir, filename).toFile();
        file.deleteOnExit();
        try (BufferedWriter bw = new BufferedWriter(new java.io.FileWriter(file, false))) {
            bw.write(fileContent);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return file;
    }

    void close() {
    }

}