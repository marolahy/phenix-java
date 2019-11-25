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

public class Launcher {
    private static final Logger logger = LogManager.getLogger(Launcher.class);
    private List<String> transactionFileName = new ArrayList<>();
    private List<String> referenceFileName = new ArrayList<>();


    private final static String  TRANSACTION_FILENAME_PATTERN = "^transactions\\_[0-9]{8}\\.data$";
    private final static String PRODUCT_FILENAME_PATTERN = "^reference\\_prod\\-(.*)\\_[0-9]{8}\\.data$";


    private final String inputDir;
    private final String outputDir;

    public Launcher(String inputDir, String outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
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
        var launcher = new  Launcher(args[0],args[1]);
        long start,end;
        start = System.currentTimeMillis();
        logger.debug("Debut de la principal tache");
        List<String> fileList = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(args[0]))) {
             fileList = walk.filter(x -> x.toString().endsWith(".data")).map(x -> x.toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(var f : fileList){
                var file = new File(f);
                var r = Pattern.compile(PRODUCT_FILENAME_PATTERN);
                var m = r.matcher(file.getName());
                if(m.find()){
                    launcher.referenceFileName.add(f);
                }
                r = Pattern.compile(TRANSACTION_FILENAME_PATTERN);
                m = r.matcher(file.getName());
                if(m.find()){
                    launcher.transactionFileName.add(f);
                }
        }
        for(var f : launcher.transactionFileName){

        }
        logger.debug("Fin de traitement");
    }

}
