package dev.marfien.rewibw.agent;

import java.lang.instrument.Instrumentation;

public class AntiReduceAgent {

    public static void premain(String args, Instrumentation instrumentation) throws Exception {
        System.out.println("Loading AntiReduceAgent");
        try {
            instrumentation.addTransformer(new AntiReduceTransformer(), true);
        } catch (Exception e) {
            System.err.println("Error loading AntiReduceAgent");
            throw e;
        }
        System.out.println("AntiReduceAgent loaded");
    }

}
