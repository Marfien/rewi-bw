package dev.marfien.rewibw.agent;

import javassist.*;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class AntiReduceTransformer implements ClassFileTransformer {
    
    private static final String NMS_LOCATION_TEMPLATE = "net/minecraft/server/1_8_R3/%s";
    private static final String NMS_CLASS_TEMPLATE = "net.minecraft.server.1_8_R3.%s";

    private static final Map<String, MethodReplacementData> replacements = new HashMap<>();
    
    private static void addReplacement(String className, String methodName, String customMethodBodySource, String... parameterTypes) {
        replacements.put(String.format(NMS_LOCATION_TEMPLATE, className), new MethodReplacementData(
                String.format(NMS_CLASS_TEMPLATE, className),
                methodName,
                customMethodBodySource,
                parameterTypes
        ));
    }

    static {
        addReplacement("EntityHuman", "attack", "CustomAttackMethod.java", String.format(NMS_CLASS_TEMPLATE, "Entity"));
        addReplacement("PacketPlayOutUpdateAttributes", "b", "CustomUpdateAttributesSerializeMethod.java", String.format(NMS_CLASS_TEMPLATE, "PacketDataSerializer"));
    }
    
    private final Logger logger;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        
        try {
            MethodReplacementData methodReplacementData = replacements.get(className);
            
            if (methodReplacementData == null) return null;
            
            logger.info("Replacing method " + methodReplacementData.getMethodName() + " in class " + className);
            return replaceMethod(methodReplacementData);
            
        } catch (Exception e) {
            logger.severe(String.format("Error transforming class %s: %s", className, e.getMessage()));
            throw new IllegalClassFormatException(e.getMessage());
        }
    }

    private byte[] replaceMethod(MethodReplacementData methodReplacementData)
            throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();
        
        CtClass baseClass = methodReplacementData.getClass(pool);
        CtMethod method = baseClass.getDeclaredMethod(methodReplacementData.getMethodName(), methodReplacementData.getParametersClasses(pool));
        
        method.setBody(methodReplacementData.getMethodBody());

        byte[] byteCode = baseClass.toBytecode();
        baseClass.detach();
        return byteCode;
    }

}
