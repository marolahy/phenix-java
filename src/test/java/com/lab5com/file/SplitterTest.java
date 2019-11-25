package com.lab5com.file;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static com.lab5com.util.PhenixConstant.TEMP_DIR;
import static org.hamcrest.MatcherAssert.assertThat;

class SplitterTest {

    private Splitter fileSplitter;
    private String tempFilesDir = TEMP_DIR;
    private String testFilename = "test.txt";

    @BeforeEach
    public void setUp() {
        fileSplitter = new Splitter("test.txt","transition");
    }

    @AfterEach
    public void tearDown() throws IOException {
        String[] testFiles = {"a.txt", "ab.txt", "ac.txt", testFilename};
        for (String file : testFiles) {
            Files.deleteIfExists(Paths.get(TEMP_DIR, file));
        }
    }

    @Test
    public void testSplit() {
        writeFile();
        String filename = tempFilesDir + "/" + testFilename;

        Map<String, File> tempFiles = fileSplitter.split();

        assertThat(fileSplitter.getLinesQueue().size(), Is.is(0));
        assertThat(fileSplitter.isReaderDone(), Is.is(Boolean.TRUE));
        assertThat(tempFiles, IsNull.notNullValue());
        assertThat(tempFiles.size(), Is.is(1));
        assertThat(tempFiles.keySet(), CoreMatchers.hasItem("a"));
    }

    @Test
    public void testSplitWithMoreTempFiles() {
        writeFile();
        String filename = tempFilesDir + "/" + testFilename;

        Map<String, File> tempFiles = fileSplitter.split();

        assertThat(fileSplitter.getLinesQueue().size(), Is.is(0));
        assertThat(fileSplitter.isReaderDone(), Is.is(Boolean.TRUE));
        assertThat(tempFiles, IsNull.notNullValue());
        assertThat(tempFiles.size(), Is.is(3));
        assertThat(tempFiles.keySet(), CoreMatchers.hasItems("a", "ab", "ac"));

        Map<String, Set<File>> sameFirstCharFilename = fileSplitter.getSameFirstCharFilename();
        assertThat(sameFirstCharFilename.size(), Is.is(1));
        assertThat(sameFirstCharFilename.keySet(), CoreMatchers.hasItems("a"));
        assertThat(sameFirstCharFilename.get("a").size(), Is.is(3));
    }

    private void writeFile() {
        String fileContent = "abcdefghijklmnoprstuvxyz abcdefghijklmnoprstuvxyz abc\n" +
                "abc defghi jklmnopr stuvxyz abcdefghijklmnoprstuvxyz abc\n" +
                "acb zyxut defghijklmnoprstuvxyz abcdefghijklmnoprstuvxyz";

        String tempFilesDir = TEMP_DIR;
        File file = Paths.get(tempFilesDir, testFilename).toFile();
        try (BufferedWriter bw = new BufferedWriter(new java.io.FileWriter(file, false))) {

            bw.write(fileContent);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
}