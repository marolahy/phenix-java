package com.lab5com.file;

import com.lab5com.PhenixException;

import java.io.BufferedReader;
import java.io.IOException;

public class BufferedReaderWrapper implements Sortable {
    private final BufferedReader bufferedReader;
    private String line;

    public BufferedReaderWrapper(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
        readNextLine();
    }

    public void close() throws IOException {
        this.bufferedReader.close();
    }

    public boolean isEmpty() {
        return this.line == null;
    }

    public String peek() {
        return this.line;
    }

    public String poll() throws IOException {
        String answer = peek();
        readNextLine();
        return answer;
    }

    private void readNextLine() {
        try {
            this.line = bufferedReader.readLine();
        } catch (IOException e) {
            throw new PhenixException(e.getMessage(), e);
        }
    }

    @Override
    public String getTerm() {
        return line;
    }
}
