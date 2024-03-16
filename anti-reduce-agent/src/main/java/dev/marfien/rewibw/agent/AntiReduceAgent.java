package dev.marfien.rewibw.agent;

import lombok.Getter;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public class AntiReduceAgent {

    @Getter
    private static final Logger logger = Logger.getLogger("AntiReduceAgent");

    public static void premain(String args, Instrumentation instrumentation) throws Exception {
        logger.info("Loading AntiReduceAgent");
        try {
            instrumentation.addTransformer(new AntiReduceTransformer(), true);
        } catch (Exception e) {
            logger.severe("Error loading AntiReduceAgent");
            throw e;
        }
        logger.info("AntiReduceAgent loaded");
    }

}
