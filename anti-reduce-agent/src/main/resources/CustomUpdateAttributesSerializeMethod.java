public void serialize(PacketDataSerializer $1) throws IOException {
    $1.b($0.a);
    $1.writeInt($0.b.size());
    java.util.Iterator var2 = $0.b.iterator();

    while(var2.hasNext()) {
        net.minecraft.server.v1_8_R3.PacketPlayOutUpdateAttributes.AttributeSnapshot var3 = (net.minecraft.server.v1_8_R3.PacketPlayOutUpdateAttributes.AttributeSnapshot)var2.next();
        String attributeName = var3.a();
        $1.a(attributeName);
        $1.writeDouble("generic.attackDamage".equals(attributeName) ? 0.0 : var3.b());
        $1.b(var3.c().size());
        java.util.Iterator var4 = var3.c().iterator();

        while(var4.hasNext()) {
            net.minecraft.server.v1_8_R3.AttributeModifier var5 = (net.minecraft.server.v1_8_R3.AttributeModifier)var4.next();
            $1.a(var5.a());
            $1.writeDouble(var5.d());
            $1.writeByte(var5.c());
        }
    }

}
