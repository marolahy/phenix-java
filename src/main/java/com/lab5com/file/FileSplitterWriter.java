package com.lab5com.file;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.lab5com.util.PhenixConstant.MAX_TEMP_FILE_SIZE;
import static com.lab5com.util.PhenixConstant.TEMP_DIR;

public class FileSplitterWriter extends FileWriter implements Callable<Boolean> {
    private final Splitter fileSplitter;
    private String tempFilesDir = TEMP_DIR;

    public FileSplitterWriter(Splitter fileSplitter, String outpoutFile) {
        super(outpoutFile);
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
        File file = getFile(line);
        appendLine(line, file);
    }

    protected File getFile(String line) {
        return getFile("", line);
    }


    protected File getFile(String prefix, String line) {
        char start = line.charAt(0);
        String filename = (prefix + start).toLowerCase();

        File file = fileSplitter.getTempFiles().get(filename);
        if (file == null) {
            file = Paths.get(tempFilesDir, filename + ".txt").toFile();
            file.deleteOnExit();
            fileSplitter.addTempFile(filename, file);
        } else {
            Long maxTempFileSize = MAX_TEMP_FILE_SIZE;
            if (fileSplitter.isFileExhausted(filename) || file.length() >= maxTempFileSize) {
                fileSplitter.addExhaustedFile(filename);
                file = getFile(filename, line.substring(1));
            }
        }
        fileSplitter.checkSameFirstCharFilename(file);
        return file;
    }
}