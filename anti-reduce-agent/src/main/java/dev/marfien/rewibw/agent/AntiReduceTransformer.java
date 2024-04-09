package dev.marfien.rewibw.agent;

import javassist.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AntiReduceTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            ClassPool pool = ClassPool.getDefault();

            switch (className) {
                case "net/minecraft/server/v1_8_R3/EntityHuman":
                    return replaceMethod(className, "CustomAttackMethod.java", "attack", pool.get("net.minecraft.server.v1_8_R3.Entity"));
                case "net/minecraft/server/v1_8_R3/PacketPlayOutUpdateAttributes":
                    return replaceMethod(className, "CustomUpdateAttributesSerializeMethod.java", "b", pool.get("net.minecraft.server.v1_8_R3.PacketDataSerializer"));
                default:
                    break;
            }
        } catch (Exception e) {
            throw new IllegalClassFormatException(e.getMessage());
        }

        return null;
    }

    private byte[] replaceMethod(String className, String customMethodDeclarationResource, String methodName, CtClass... parameters) throws NotFoundException,
            CannotCompileException, IOException {
        String javaClassName = className.replace('/', '.');
        ClassPool pool = ClassPool.getDefault();
        CtClass baseClass = pool.get(javaClassName);

        CtMethod method = baseClass.getDeclaredMethod(methodName, parameters);
        try (BufferedReader attackMethodStream = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(customMethodDeclarationResource)))) {
            String methodSource = attackMethodStream.lines().reduce((a, b) -> a + '\n' + b).orElse("");
            String methodBody = methodSource.substring(methodSource.indexOf('{'));
            method.setBody(methodBody);
            System.out.println("Replaced method " + methodName + " in " + className);
        } catch (CannotCompileException e) {
            System.err.println("Error replacing method " + methodName + " in " + className);
            System.err.println("Error: " + e.getMessage());
        }

        byte[] byteCode = baseClass.toBytecode();
        baseClass.detach();
        return byteCode;
    }

}
