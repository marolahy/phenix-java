package com.lab5com.file;

import com.lab5com.Launcher;
import com.lab5com.PhenixException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AlphabeticalOrderValidator {
    private static final Logger logger = LogManager.getLogger(Launcher.class);

    public boolean validate(String file) {
        logger.debug("Running AlphabeticalOrderValidator!");
        Path path = Paths.get(file);
        Sorter sorter = new Sorter();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String left = br.readLine();
            String right;
            while ((right = br.readLine()) != null ) {
                if (sorter.isLeftPrecedent(left, right)) {
                    left = right;
                } else {
                    System.out.println(" LEFT == "+left);
                    System.out.println(" RIGHT == "+right);
                    throw new PhenixException("The file " + path.toFile().getAbsolutePath() + " isn't sorted!", null );
                }
            }
        } catch (NoSuchFileException e) {
            throw new PhenixException("File not Found!", e);
        } catch (Exception e) {
            throw new PhenixException("Unexpected error occured!", e);
        }
        logger.debug("The file {] is sorted!", path.toFile().getAbsolutePath() );
        return Boolean.TRUE;
    }
}
