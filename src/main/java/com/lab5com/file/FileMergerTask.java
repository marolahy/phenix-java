package com.lab5com.file;

import com.lab5com.PhenixException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.lab5com.util.PhenixConstant.TEMP_DIR;

public class FileMergerTask implements Callable<Boolean> {

        private FileWriter fileWriter;
        private Set<File> fragments;
        private String filename;

    public FileMergerTask(Set<File> fragments, String filename) {
            this.fileWriter = new FileWriter();
            this.fragments = fragments;
            this.filename = filename;
        }


        @Override
        public Boolean call() throws Exception {
            executeKWayMerge(fragments, filename);
            return Boolean.TRUE;
        }

        public void executeKWayMerge(Set<File> files, String outputfile) {
            //create a temporary file
            File dir = Paths.get(TEMP_DIR, "k-way").toFile();
            dir.mkdirs();
            File output = new File(dir, outputfile);

            try (BufferedWriter bw = new BufferedWriter(new java.io.FileWriter(output, false))) {
                SortedList<BufferedReaderWrapper> autoOrderedList = getAutoSortedList(files);
                mergeSortedLines(bw, autoOrderedList);
                bw.flush();
            } catch (NoSuchFileException e) {
                throw new PhenixException("File not Found!", e);
            } catch (Exception e) {
                throw new PhenixException("Unexpected error occured!", e);
            }
            fileWriter.delete(files);

            //move to outputfile
            File destine = Paths.get(TEMP_DIR, outputfile+".data").toFile();
            fileWriter.move(output, destine);
            dir.delete();
        }

        private void mergeSortedLines( BufferedWriter bw, SortedList<BufferedReaderWrapper> autoOrderedList) throws IOException {
            Integer rowCount = 0;
            while (autoOrderedList.size() > 0) {
                BufferedReaderWrapper bfw = autoOrderedList.poll();
                String r = bfw.poll();
                bw.write(r);
                bw.newLine();
                rowCount++;
                if (bfw.isEmpty()) {
                    bfw.close();
                } else {
                    // add it back
                    autoOrderedList.add(bfw);
                }
                if (rowCount % 1000 == 0) {
                    bw.flush();
                }
            }
        }

        private SortedList<BufferedReaderWrapper> getAutoSortedList(Set<File> files) throws IOException {
            SortedList<BufferedReaderWrapper> autoOrderedList = new SortedList();
            for (File file : files) {
                BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
                autoOrderedList.add(new BufferedReaderWrapper(br));
            }
            return autoOrderedList;
        }
}
