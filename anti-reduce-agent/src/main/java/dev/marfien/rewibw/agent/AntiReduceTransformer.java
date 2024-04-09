package dev.marfien.rewibw.agent;

import javassist.*;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class AntiReduceTransformer implements ClassFileTransformer {

    private final Logger logger;

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

    private byte[] replaceMethod(
            String className,
            String customMethodDeclarationResource,
            String methodName,
            CtClass... parameters
    ) throws NotFoundException, CannotCompileException, IOException {
        String javaClassName = className.replace('/', '.');
        ClassPool pool = ClassPool.getDefault();
        CtClass baseClass = pool.get(javaClassName);

        CtMethod method = baseClass.getDeclaredMethod(methodName, parameters);

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(customMethodDeclarationResource);
        if (inputStream == null) {
            this.logger.log(Level.SEVERE, "Error loading method {0} in {1}: Resource not found.", new Object[]{methodName, className});
            return null;
        }

        try (BufferedReader attackMethodStream = new BufferedReader(new InputStreamReader(inputStream))) {
            String methodSource = attackMethodStream.lines().reduce((a, b) -> a + '\n' + b).orElse("");
            String methodBody = methodSource.substring(methodSource.indexOf('{'));
            method.setBody(methodBody);
            this.logger.log(Level.FINE, "Replaced method {0} in {1}.", new Object[]{methodName, className});
        } catch (CannotCompileException e) {
            this.logger.log(Level.SEVERE, "Error replacing method {0} in {1}: {2}", new Object[]{methodName, className, e.getMessage()});
            return null;
        }

        byte[] byteCode = baseClass.toBytecode();
        baseClass.detach();
        return byteCode;
    }

}
