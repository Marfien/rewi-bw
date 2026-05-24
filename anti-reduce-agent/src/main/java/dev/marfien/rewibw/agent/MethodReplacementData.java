package dev.marfien.rewibw.agent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MethodReplacementData {

  private static final ClassPool CLASS_POOL = ClassPool.getDefault();

  private final String className;

  @Getter
  private final String methodName;
  private final String customMethodBodySource;
  private final String[] parameterTypes;

  @Getter(AccessLevel.NONE)
  private byte[] bytecode = null;

  public byte[] getByteCode() throws NotFoundException, CannotCompileException, IOException {
    if (this.bytecode == null) {
      this.makeBytecode();
    }

    return this.bytecode;
  }

  private void makeBytecode() throws IOException, CannotCompileException, NotFoundException {
    CtClass baseClass = CLASS_POOL.get(this.className);
    try {
      CtMethod method = baseClass.getDeclaredMethod(this.methodName, this.getParametersClasses(CLASS_POOL));

      method.setBody(this.getMethodBody());

      this.bytecode = baseClass.toBytecode();
    } finally {
      baseClass.detach();
    }
  }

  private CtClass[] getParametersClasses(ClassPool pool) throws NotFoundException {
    CtClass[] classes = new CtClass[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      classes[i] = pool.get(parameterTypes[i]);
    }
    return classes;
  }

  private String getMethodBody() throws IOException {
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
