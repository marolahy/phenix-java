package com.lab5com.processor;

import com.lab5com.model.Product;
import com.lab5com.model.ResultQuantity;
import com.lab5com.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalculationPerQuantityShopPerDays implements Processor {
    private static final Logger logger = LogManager.getLogger(CalculationPerQuantityPerDays.class);
    private final Map<String, List<Transaction>> transactionMap;
    private final Map<String, List<Product>> productMap;

    public CalculationPerQuantityShopPerDays(Map<String, List<Transaction>> transactionMap, Map<String, List<Product>> productMap) {
        this.transactionMap = transactionMap;
        this.productMap = productMap;
    }

    @Override
    public Map<String, String> process() {
        Map<String, String> response = new HashMap<>();
        for (var productPerShop : this.productMap.entrySet()) {
            var shop = productPerShop.getKey();
            for (var transaction : this.transactionMap.entrySet()) {
                var date = transaction.getKey();
                var results = transaction
                        .getValue()
                        .stream()
                        .filter(t -> t.getShop().equals(shop))
                        .map(t -> {
                            var optionalProduct = productMap
                                    .get(t.getShop())
                                    .stream()
                                    .filter(prd -> prd.getId() == t.getProduct())
                                    .findFirst();
                            if (optionalProduct.isPresent()) {
                                var product = optionalProduct.get();
                                var resultQuantity = new ResultQuantity();
                                resultQuantity.setId(t.getProduct());
                                resultQuantity.setQuantity(t.getQuantity());
                                return resultQuantity;
                            }
                            return null;
                        })
                        .filter(x -> x != null)
                        .collect(Collectors.groupingBy(
                                resultQuantity -> resultQuantity.getId(),
                                Collectors.summingLong(resultQuantity -> resultQuantity.getQuantity())
                        ));
                var product_per_days = results.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(x -> String.valueOf(x.getKey())).limit(100)
                        .collect(Collectors.joining("\n"));
                var filename = "top_100_ventes_"
                        .concat(shop)
                        .concat(date)
                        .concat(".data");
                response.put(filename, product_per_days);
            }
        }

        return response;
    }
}
