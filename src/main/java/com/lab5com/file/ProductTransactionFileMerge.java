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

public class ProductTransactionFileMerge implements QueueHandler {
    private static final Logger logger = LogManager.getLogger(ProductTransactionFileMerge.class);
    private BlockingQueue<String> linesQueue = new ArrayBlockingQueue<>(30);
    private Map<String, File> tempFiles = new ConcurrentHashMap<>();
    private final Set<String> exhaustedTempFiles = ConcurrentHashMap.newKeySet();
    private final Map<String, Set<File>> sameTransactionId = new ConcurrentHashMap<>();
    private boolean isReaderDone = false;
    private static AtomicLong count = new AtomicLong(0);
    private final String transactionFileName;
    private List<String> shopFileName;

    public String getTransactionFileName() {
        return transactionFileName;
    }

    public ProductTransactionFileMerge(String transactionFileName, List<String> shopFileName) {
        this.transactionFileName = transactionFileName;
        this.shopFileName = shopFileName;
    }

    protected synchronized void checkSameTransactionFilename(File file) {
        String firstChar = ""+file.getName().charAt(0);
        Set<File> temp = sameTransactionId.get(firstChar);
        if (temp == null) {
            temp = ConcurrentHashMap.newKeySet();
        }
        temp.add(file);
        sameTransactionId.put(firstChar, temp);
    }

    /**
     * Add a temporary file to the temporary files map.
     * @param filename filename as a key
     * @param file the file instance as value
     */
    public void addTempFile(String filename, File file) {
        if (!tempFiles.containsKey(filename)) {
            tempFiles.put(filename, file);
        }
    }

    private void print() {
        logger.debug("Lines processed: {}", getCount().get());
        logger.debug("Number of temp files: {}", tempFiles.size() );
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

    public Map<String, Set<File>> getSameTransactionId() {
        return sameTransactionId;
    }
    public BlockingQueue<String> getLinesQueue() {
        return linesQueue;
    }

    public void setIsReaderDone(boolean isReaderDone) {
        this.isReaderDone = isReaderDone;
    }


    public void merge() {
        createTempDir();
        ExecutorService readerPool = Executors.newFixedThreadPool(1);
        Future<Boolean> readerFuture = readerPool.submit(getFileReader(transactionFileName));
        readerPool.shutdown();
        ExecutorService writerPool = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future> futures = new ArrayList();
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(writerPool.submit(getFileMergeWriter()));
        }
        writerPool.shutdown();

        setIsReaderDone(FutureHelper.waitExecution(readerFuture));
        FutureHelper.waitExecution(futures);
        print();

    }

    private FileMergeWriter getFileMergeWriter() {
        return new FileMergeWriter(this);
    }

    private FileReader getFileReader(String filename) {
        return new FileReader(this, filename);
    }

    private void createTempDir() {
        var file = new File(transactionFileName);
        var dir = TEMP_DIR.concat(File.separator ).concat(file.getName());
        File tempDir = Paths.get(dir).toFile();
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }
}
