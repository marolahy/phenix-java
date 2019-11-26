package com.lab5com.processor;

import com.lab5com.model.Product;
import com.lab5com.model.ResultCa;
import com.lab5com.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.lab5com.util.PhenixConstant.CARREFOUR_DATE_PATTERN;

public class CalculationCaPerWeek implements Processor {
    private static final Logger logger = LogManager.getLogger(CalculationCaPerDays.class);
    private final List<Transaction> transaction;
    private final Map<String, List<Product>> productMap;

    public CalculationCaPerWeek(Map<String, List<Transaction>> transactionMap, Map<String, List<Product>> productMap) throws ParseException {
        this.transaction = mergeLastWeek(transactionMap);
        this.productMap = productMap;
    }

    @Override
    public Map<String, String> process() {
        var results = transaction
                .stream()
                .map(t -> {
                    var optionalProduct = productMap
                            .get(t.getShop())
                            .stream()
                            .filter(prd -> prd.getId() == t.getProduct())
                            .findFirst();
                    if (optionalProduct.isPresent()) {
                        var product = optionalProduct.get();
                        var result = new ResultCa();
                        result.setId(t.getProduct());
                        result.setCa(product.getPrice() * t.getQuantity());
                        return result;
                    }
                    return null;
                })
                .filter(x -> x != null)
                .collect(Collectors.groupingBy(
                        resultCa -> resultCa.getId(),
                        Collectors.summingDouble(resultCa -> resultCa.getCa())
                ));
        var product_per_days = results.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(x -> String.valueOf(x.getKey())).limit(100)
                .collect(Collectors.joining("\n"));

        var now = new Date();
        var filename = "top_100_ca_GLOBAL_"
                .concat(new SimpleDateFormat(CARREFOUR_DATE_PATTERN).format(now))
                .concat("-J7.data");
        Map<String, String> response = new HashMap<>();
        response.put(filename, product_per_days);
        return response;
    }
}
