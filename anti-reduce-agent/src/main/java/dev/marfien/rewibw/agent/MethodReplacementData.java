package dev.marfien.rewibw.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import lombok.Data;

import java.io.*;

@Data
public class MethodReplacementData {

    private final String className;
    private final String methodName;
    private final String customMethodBodySource;
    private final String[] parameterTypes;

    public CtClass[] getParametersClasses(ClassPool pool) throws NotFoundException {
        CtClass[] classes = new CtClass[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            classes[i] = pool.get(parameterTypes[i]);
        }
        return classes;
    }

    public CtClass getClass(ClassPool pool) throws NotFoundException {
        return pool.get(className);
    }

    public String getMethodBody() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(customMethodBodySource);
        if (stream == null) {
            throw new FileNotFoundException("Resource not found: " + customMethodBodySource);
        }

        String body;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            body = reader.lines()
                    .reduce((current, next) -> current + "\n" + next)
                    .orElse("{ System.out.println(\"Method body not found\"); }");
        }

        return body.substring(body.indexOf('{'));
    }

}
