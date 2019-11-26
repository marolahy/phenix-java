package com.lab5com.processor;

import com.lab5com.model.Product;
import com.lab5com.model.ResultCa;
import com.lab5com.model.ResultQuantity;
import com.lab5com.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lab5com.util.PhenixConstant.CARREFOUR_DATE_PATTERN;

public class CalculationQuantityPerWeek implements Processor {
    private static final Logger logger = LogManager.getLogger(CalculationQuantityPerWeek.class);
    private final List<Transaction> transaction;
    private final Map<String, List<Product>> productMap;

    public CalculationQuantityPerWeek(Map<String, List<Transaction>> transactionMap, Map<String, List<Product>> productMap) throws ParseException {
        this.transaction = mergeLastWeek(transactionMap);
        this.productMap = productMap;
    }

    @Override
    public Map<String, String> process() {
        Map<String, String> response = new HashMap<>();
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
                        var result = new ResultQuantity();
                        result.setId(t.getProduct());
                        result.setQuantity(t.getQuantity());
                        return result;
                    }
                    return null;
                })
                .filter(x -> x != null)
                .collect(Collectors.groupingBy(
                        resultCa -> resultCa.getId(),
                        Collectors.summingDouble(resultCa -> resultCa.getQuantity())
                ));
        var product_per_days = results.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(x -> String.valueOf(x.getKey())).limit(100)
                .collect(Collectors.joining("\n"));

        var now = new Date();
        var filename = "top_100_ventes_GLOBAL_"
                .concat(new SimpleDateFormat(CARREFOUR_DATE_PATTERN).format(now))
                .concat("_J7.data");
        response.put(filename, product_per_days);

        return response;
    }
}
