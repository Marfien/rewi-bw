package dev.marfien.rewibw.agent;

import lombok.experimental.UtilityClass;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

@UtilityClass
public class AntiReduceAgent {

    private static final Logger logger = Logger.getLogger("AntiReduceAgent");

    @SuppressWarnings("unused")
    public static void premain(String args, Instrumentation instrumentation) {
        logger.info("Loading AntiReduceAgent");
        try {
            instrumentation.addTransformer(new AntiReduceTransformer(logger), true);
        } catch (Exception e) {
            logger.severe("Error loading AntiReduceAgent");
            throw e;
        }
        logger.info("AntiReduceAgent loaded");
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        premain(args, instrumentation);
    }

}
