package dev.marfien.rewibw.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AntiReduceTransformer implements ClassFileTransformer {

  private static final String NMS_CLASS_TEMPLATE = "net.minecraft.server.v1_8_R3.%s";

  private static final MethodReplacementData ENTITY_HUMAN_ATTACK = new MethodReplacementData(
      String.format(NMS_CLASS_TEMPLATE, "EntityHuman"),
      "attack",
      "CustomAttackMethod.java",
      new String[] { String.format(NMS_CLASS_TEMPLATE, "Entity") });
  private static final MethodReplacementData PACKET_UPDATE_ATTRIBUTES_SERIALIZE = new MethodReplacementData(
      String.format(NMS_CLASS_TEMPLATE, "PacketPlayOutUpdateAttributes"),
      "b",
      "CustomUpdateAttributesSerializeMethod.java",
      new String[] { String.format(NMS_CLASS_TEMPLATE, "PacketDataSerializer") });

  private final Logger logger;

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    try {
      MethodReplacementData methodReplacementData;
      switch (className) {
        case "net/minecraft/server/v1_8_R3/EntityHuman":
          methodReplacementData = ENTITY_HUMAN_ATTACK;
          break;
        case "net/minecraft/server/v1_8_R3/PacketPlayOutUpdateAttributes":
          methodReplacementData = PACKET_UPDATE_ATTRIBUTES_SERIALIZE;
          break;
        default:
          return null;
      }

      logger.info("Replacing method " + methodReplacementData.getMethodName() + " in class " + className);
      return methodReplacementData.getByteCode();
    } catch (Exception e) {
      logger.severe(String.format("Error transforming class %s: %s", className, e.getMessage()));
      throw new IllegalClassFormatException(e.getMessage());
    }
  }

}
