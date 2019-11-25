package com.lab5com;

import com.lab5com.model.Product;
import com.lab5com.model.Transaction;
import com.lab5com.processor.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.Pattern;

import static com.lab5com.util.PhenixConstant.THREAD_COUNT;
import static com.lab5com.util.PhenixConstant.WORKERS;

public class Launcher {
    private static final Logger logger = LogManager.getLogger(Launcher.class);
    private static Map<String,List<Transaction>> transactionMap = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, List<Product>> productMap = Collections.synchronizedMap(new HashMap<>());


    private final static String  TRANSACTION_FILENAME_PATTERN = "^transactions\\_[0-9]{8}\\.data$";
    private final static String PRODUCT_FILENAME_PATTERN = "^reference\\_prod\\-(.*)\\_[0-9]{8}\\.data$";


    private final String inputDir;
    private final String outputDir;
    private List<String> fileList = new ArrayList<>();

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
        long start,end;
        start = System.currentTimeMillis();
        logger.debug("Debut de la principal tache");
        ;
        for (int i = 1; i <= THREAD_COUNT; i++) {
            var launcher = new Launcher(args[0],args[1]);
            launcher.run();
        }
        generateFile(new CalculationCaPerDays(Launcher.transactionMap ,Launcher.productMap ),args[1]);
        generateFile(new CalculationCaPerShop(Launcher.transactionMap ,Launcher.productMap ),args[1]);
        generateFile(new CalculationPerQuantityPerDays(Launcher.transactionMap ,Launcher.productMap ),args[1]);
        generateFile(new CalculationPerQuantityShopPerDays(Launcher.transactionMap ,Launcher.productMap ),args[1]);
        generateFile(new CalculationCaPerWeek(Launcher.transactionMap ,Launcher.productMap ),args[1]);
        generateFile(new CalculationCaShopPerWeek(Launcher.transactionMap ,Launcher.productMap ),args[1]);
        end = System.currentTimeMillis();
        logger.debug("Principale tache complete en {} ms",  (end - start));
        logger.debug("Fin de traitement");
    }

    public void run() throws IOException, InterruptedException {
        try (Stream<Path> walk = Files.walk(Paths.get(inputDir))) {
            fileList = walk.map(x -> x.toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        var threads = new ArrayList<Thread>(WORKERS);
        for (int i = 0; i < WORKERS; i++) {
            var processor = new Thread(this::processFiles);
            processor.start();
            threads.add(processor);
        }
        for (var processor : threads) {
            processor.join();
        }


    }
    private static void generateFile(Processor processor, String outputPath ) throws IOException {
        for(var perDays : processor.process().entrySet()){
            var filename = outputPath
                    .concat(File.separator)
                    .concat(perDays.getKey());
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(perDays.getValue());
            writer.close();
        }

    }
    private void processFiles() {
        for(var f : fileList){
            var file = new File(f);
            var r = Pattern.compile(PRODUCT_FILENAME_PATTERN);
            var m = r.matcher(file.getName());
            if(m.find()){
                var key =m.group(1);
                productMap.put(key,Collections.synchronizedList(getProduct(f)));
            }

            r = Pattern.compile(TRANSACTION_FILENAME_PATTERN);
            m = r.matcher(file.getName());
            if(m.find()){
                transactionMap.put(
                        file.getName()
                                .replaceAll("[^0-9]", "")
                        , Collections.synchronizedList(getTransaction(f)));
            }
        }
    }

    private List<Product> getProduct(String file) {
        List<Product> products = new ArrayList<>();
        try (var b = Files.newBufferedReader(Path.of(file))) {
            var readLine = "";
            while ((readLine = b.readLine()) != null) {
                var split = readLine.split("\\|");
                products.add(Product.of(Long.valueOf(split[0]),Double.valueOf(split[1])));
            }

        } catch (IOException ignored) {
        }
        return products;
    }
    private List<Transaction> getTransaction(String file){
        List<Transaction> transactions = new ArrayList<>();
        try (var b = Files.newBufferedReader(Path.of(file))) {
            var readLine = "";
            while ((readLine = b.readLine()) != null) {
                var split = readLine.split("\\|");
                transactions.add(Transaction.of(split[0],split[1],split[2],split[3],split[4]));
            }

        } catch (IOException ignored) {
        }
        return transactions;

    }

}
