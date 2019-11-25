package com.lab5com.processor;

import com.lab5com.model.Product;
import com.lab5com.model.ResultCa;
import com.lab5com.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CalculationCaPerDays  implements Processor{
    private static final Logger logger = LogManager.getLogger(CalculationCaPerDays.class);
    private final Map<String, List<Transaction>> transactionMap;
    private final Map<String, List<Product>> productMap;

    public CalculationCaPerDays(Map<String, List<Transaction>> transactionMap, Map<String, List<Product>> productMap) {
        this.transactionMap = transactionMap;
        this.productMap = productMap;
    }

    @Override
    public Map<String,String> process(){
        Map<String,String> response = new HashMap<>();
        for(var transaction: this.transactionMap.entrySet() ){
            var date = transaction.getKey();
            var results = transaction
                    .getValue()
                    .stream()
                    .map( t ->{
                        var optionalProduct = productMap
                                .get(t.getShop())
                                .stream()
                                .filter(prd -> prd.getId() == t.getProduct())
                                .findFirst();
                        if(optionalProduct.isPresent()){
                            var product = optionalProduct.get();
                            var result = new ResultCa();
                            result.setId(t.getProduct());
                            result.setCa( product.getPrice() * t.getQuantity() );
                            return result;
                        }
                        return null;
                    })
                    .filter(x -> x != null )
                    .collect(Collectors.groupingBy(
                            resultCa -> resultCa.getId(),
                            Collectors.summingDouble(resultCa -> resultCa.getCa())
                    ));
            var product_per_days = results.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(x -> String.valueOf(x.getKey())).limit(100)
                    .collect(Collectors.joining("\n"));
            var filename = "top_100_ca_GLOBAL_"
                    .concat(date)
                    .concat(".data");
            response.put(filename,product_per_days);
        }

        return response;
    }
}
