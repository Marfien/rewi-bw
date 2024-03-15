package dev.marfien.rewibw.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AntiReduceAgent {

    public static void premain(String args, Instrumentation instrumentation) throws UnmodifiableClassException, ClassNotFoundException {
        instrumentation.addTransformer(new AntiReduceTransformer(), true);
        instrumentation.retransformClasses(Class.forName("net.minecraft.server.v1_8_R3.EntityHuman"));
    }

}
