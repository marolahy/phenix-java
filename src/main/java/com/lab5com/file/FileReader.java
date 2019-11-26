package com.lab5com.file;

import com.lab5com.PhenixException;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * FileReader contains the logic to read each file line and add it to the Queue to be processed.
 * Implements Callable to execute in parallel way.
 */
public class FileReader implements Callable<Boolean> {
    private QueueHandler fileHandler;
    private String filename;

    public FileReader(QueueHandler fileHandler, String filename) {
        this.fileHandler = fileHandler;
        this.filename = filename;
    }

    @Override
    public Boolean call() {
        return execute();
    }

    /**
     * Read each line and add to the queue
     * @return @{code true} if runs ok
     */
    public Boolean execute() {
        Path path = Paths.get(filename);
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null ) {
                fileHandler.addLineToQueue(line);
            }
        } catch (NoSuchFileException e) {
            throw new PhenixException("File not Found!", e);
        } catch (Exception e) {
            throw new PhenixException("Unexpected error occured!", e);
        }
        return Boolean.TRUE;
    }
}