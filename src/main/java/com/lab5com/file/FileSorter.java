package com.lab5com.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.lab5com.util.PhenixConstant.THREAD_COUNT;

public class FileSorter {

    public void sort(Set<File> files) {

        ExecutorService writerPool = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future> futures = new ArrayList();
        for (var file : files) {
            futures.add(writerPool.submit(new FileSorterTask(file)));
        }
        writerPool.shutdown();
        FutureHelper.waitExecution(futures);
    }
}