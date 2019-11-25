package com.lab5com.processor;

import com.lab5com.model.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.lab5com.util.PhenixConstant.CARREFOUR_DATE_PATTERN;

public interface Processor {
    public Map<String,String> process();
    public default List<Transaction> mergeLastWeek(Map<String, List<Transaction>> transactionMap) throws ParseException {
        List<Transaction> map = new ArrayList<>();
        for(var transaction: transactionMap.entrySet() ) {
            var sDate = transaction.getKey();
            var date = new SimpleDateFormat(CARREFOUR_DATE_PATTERN).parse(sDate);
            var now = new Date().toInstant();
            var last = now.minus(7, ChronoUnit.DAYS);
            var days = date.toInstant()
                            .plus(1,ChronoUnit.DAYS)
                            .minus(1,ChronoUnit.SECONDS);
            if(days.compareTo(last) > 0 )
                map.addAll(transaction.getValue());
        }
        return map;
    }
}
