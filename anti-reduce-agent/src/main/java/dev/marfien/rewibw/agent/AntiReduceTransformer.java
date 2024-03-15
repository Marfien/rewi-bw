package dev.marfien.rewibw.agent;

import javassist.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AntiReduceTransformer implements ClassFileTransformer {

    private static final String ENTITY_HUMAN_CLASS_NAME = "net/minecraft/server/v1_8_R3/EntityHuman";
    private static final String ENTITY_CLASS_NAME = "net/minecraft/server/v1_8_R3/Entity";
    private static final String ATTACK_METHOD_NAME = "attack";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.equals(ENTITY_HUMAN_CLASS_NAME)) return null;

        try {
            return replaceMethod(className);
        } catch (Exception e) {
            throw new IllegalClassFormatException(e.getMessage());
        }
    }

    private byte[] replaceMethod(String className) throws NotFoundException, CannotCompileException, IOException {
        String fqn = className.replace('/', '.');
        ClassPool pool = ClassPool.getDefault();
        CtClass baseClass = pool.get(fqn);
        CtClass entityClass = pool.get(ENTITY_CLASS_NAME);

        CtMethod oldAttackMethod = baseClass.getDeclaredMethod(ATTACK_METHOD_NAME, new CtClass[] { entityClass });
        try (BufferedReader attackMethodStream = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("CustomAttackMethod.java")))) {
            String methodSource = attackMethodStream.lines().reduce((a, b) -> a + b).get();
            String methodBody = methodSource.substring(methodSource.indexOf('{'));
            oldAttackMethod.setBody(methodBody);
        }

        return baseClass.toBytecode();
    }

}
