public void serialize(PacketDataSerializer serializer) throws IOException {
    serializer.b(this.a);
    serializer.writeInt(this.b.size());
    Iterator var2 = this.b.iterator();

    for (PacketPlayOutUpdateAttributes.AttributeSnapshot attribute : this.b) {
        String attributeName = attribute.a();
        serializer.a(attributeName);
        // Attack Damage
        serializer.writeDouble(GenericAttributes.ATTACK_DAMAGE.getName().equals(attributeName) ? 0.0 : attribute.b());
        serializer.b(attribute.c().size());
        Iterator var4 = attribute.c().iterator();

        for (AttributeModifier modifier : attribute.c()) {
            serializer.a(modifier.a());
            serializer.writeDouble(modifier.d());
            serializer.writeByte(modifier.c());
        }
    }
}
