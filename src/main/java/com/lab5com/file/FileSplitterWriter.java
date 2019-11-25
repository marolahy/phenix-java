package com.lab5com.file;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.lab5com.util.PhenixConstant.MAX_TEMP_FILE_SIZE;
import static com.lab5com.util.PhenixConstant.TEMP_DIR;

public class FileSplitterWriter extends FileWriter implements Callable<Boolean> {
    private FileSplitter fileSplitter;

    public FileSplitterWriter(FileSplitter fileSplitter) {
        this.fileSplitter = fileSplitter;
    }

    @Override
    public Boolean call() {
        try {
            while (!fileSplitter.isReaderDone() || (fileSplitter.isReaderDone() && !fileSplitter.getLinesQueue().isEmpty())) {
                String lineToProcess = fileSplitter.getLinesQueue().poll(500L, TimeUnit.MILLISECONDS);
                if (lineToProcess != null) {
                    proccessLine(lineToProcess);
                    fileSplitter.increment();
                }
            }
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    protected void proccessLine(String line) {
        var file = getFile(line);
        appendLine(line, file);
    }

    protected File getFile(String line) {
        return getFile("", line);
    }

    protected File getFile(String prefix, String line) {
        char start = line.charAt(0);
        String filename = (prefix + start).toLowerCase();

        var file = fileSplitter.getTempFiles().get(filename);
        if (file == null) {
            file = Paths.get(TEMP_DIR, filename + ".temp").toFile();
            file.deleteOnExit();
            fileSplitter.addTempFile(filename, file);
        } else {
            if (fileSplitter.isFileExhausted(filename) || file.length() >= MAX_TEMP_FILE_SIZE) {
                fileSplitter.addExhaustedFile(filename);
                file = getFile(filename, line.substring(1));
            }
        }
        fileSplitter.checkSameFirstCharFilename(file);
        return file;
    }
}
