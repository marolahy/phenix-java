package com.lab5com.util;

public class PhenixConstant {
    public static final String FILE_NAME = "C:\\lab5com\\projet\\phenix-challenge\\data\\transactions_20170514.data";


    public static final int WORKERS = 2;

    public static final String CARREFOUR_DATE_PATTERN = "yyyyMMdd";

    public static final int THREAD_COUNT = 20;
    public static final String TEMP_DIR = "c:\\temp\\";
    public static final long MAX_TEMP_FILE_SIZE = 1024 * 1024;
    public final static String TRANSACTION_FILENAME_PATTERN = "^transactions\\_[0-9]{8}\\.data$";
    public final static String PRODUCT_FILENAME_PATTERN = "^reference\\_prod\\-(.*)\\_[0-9]{8}\\.data$";
}
