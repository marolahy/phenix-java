package com.lab5com.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.lab5com.util.PhenixConstant.TEMP_DIR;
import static com.lab5com.util.PhenixConstant.THREAD_COUNT;

public class FileSplitter implements QueueHandler {

    private static final Logger logger = LogManager.getLogger(FileSplitter.class);
    private BlockingQueue<String> linesQueue = new ArrayBlockingQueue<>(30);
    private Map<String, File> tempFiles = new ConcurrentHashMap<>();
    private final String filename;
    private final Set<String> exhaustedTempFiles = ConcurrentHashMap.newKeySet();
    private final Map<String, Set<File>> sameFirstCharFilename = new ConcurrentHashMap<>();
    private boolean isReaderDone = false;
    private static AtomicLong count = new AtomicLong(0);

    public FileSplitter(String filename) {
        this.filename = filename;
    }

    /**
     * Starts the Reader and Writer Executors
     * @return a map where the key is the filename without the extension and the value is the file instance.
     */
    public Map<String, File> split() {
        createTempDir();
        ExecutorService readerPool = Executors.newFixedThreadPool(1);
        Future<Boolean> readerFuture = readerPool.submit(getFileReader(filename));
        readerPool.shutdown();

        ExecutorService writerPool = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future> futures = new ArrayList();
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(writerPool.submit(getFileSplitterWriter()));
        }
        writerPool.shutdown();

        setIsReaderDone(FutureHelper.waitExecution(readerFuture));
        FutureHelper.waitExecution(futures);
        print();
        return tempFiles;
    }

    protected synchronized void checkSameFirstCharFilename(File file) {
        String firstChar = ""+file.getName().charAt(0);
        Set<File> temp = sameFirstCharFilename.get(firstChar);
        if (temp == null) {
            temp = ConcurrentHashMap.newKeySet();
        }
        temp.add(file);
        sameFirstCharFilename.put(firstChar, temp);
    }

    public void addTempFile(String filename, File file) {
        if (!tempFiles.containsKey(filename)) {
            tempFiles.put(filename, file);
        }
    }

    private void createTempDir() {
        File tempDir = Paths.get(TEMP_DIR).toFile();
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    private void print() {
        logger.debug("Lines processed: {} ",getCount().get());
        logger.debug("Number of temp files: {}", tempFiles.size());
    }

    private FileReader getFileReader(String filename) {
        return new FileReader(this, filename);
    }

    private FileSplitterWriter getFileSplitterWriter() {
        return new FileSplitterWriter(this);
    }

    public BlockingQueue<String> getLinesQueue() {
        return linesQueue;
    }

    public void setIsReaderDone(boolean isReaderDone) {
        this.isReaderDone = isReaderDone;
    }

    public boolean isReaderDone() {
        return isReaderDone;
    }

    public void increment() {
        count.incrementAndGet();
    }

    public static AtomicLong getCount() {
        return count;
    }


    public Map<String, File> getTempFiles() {
        return tempFiles;
    }

    @Override
    public void addLineToQueue(String line) throws InterruptedException {
        linesQueue.put(line);
    }

    public void addExhaustedFile(String filename) {
        exhaustedTempFiles.add(filename);
    }

    public boolean isFileExhausted(String filename) {
        return exhaustedTempFiles.contains(filename);
    }

    public Map<String, Set<File>> getSameFirstCharFilename() {
        return sameFirstCharFilename;
    }
}
