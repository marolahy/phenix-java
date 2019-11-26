package com.lab5com;

import com.lab5com.file.*;
import com.lab5com.model.Product;
import com.lab5com.model.Transaction;
import com.lab5com.processor.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.Pattern;

import static com.lab5com.util.PhenixConstant.PRODUCT_FILENAME_PATTERN;
import static com.lab5com.util.PhenixConstant.TRANSACTION_FILENAME_PATTERN;

public class Launcher {
    private static final Logger logger = LogManager.getLogger(Launcher.class);
    private List<String> transactionFileName = new ArrayList<>();
    private List<String> shopIds = new ArrayList<>();
    private final String inputDir;
    private final String outputDir;

    public Launcher(String inputDir, String outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    public List<String> getShopIds() {
        return shopIds;
    }

    public static void main(String... args) throws IOException, InterruptedException, ParseException {
        if (args.length < 2) {
            System.err.println("Usage:");
            System.err.println(" java -jar inputDirectory outputDirectory");
            System.err.println();
            System.err.println("ou [inputDirectory] est un chemin absolue .");
            System.exit(1);
        }

        logger.debug("Debut traitement");
        var launcher = new Launcher(args[0], args[1]);
        long start, end;
        start = System.currentTimeMillis();
        logger.debug("Debut de la principal tache");
        List<String> fileList = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(args[0]))) {
            fileList = walk.filter(x -> x.toString().endsWith(".data")).map(x -> x.toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (var f : fileList) {
            var file = new File(f);
            var r = Pattern.compile(PRODUCT_FILENAME_PATTERN);
            var m = r.matcher(file.getName());
            if (m.find()) {
                var shopId = m.group(1);
                launcher.shopIds.add(f);
                continue;
            }
            launcher.transactionFileName.add(f);
        }
        for(var transaction : launcher.transactionFileName ){
            var merger = new ProductTransactionFileMerge(transaction,launcher.shopIds);
            merger.merge();
        }
        logger.debug("Fin de traitement");
    }

}
