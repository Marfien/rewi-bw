public void attack(Entity entity) {
    if (entity.aD()) {
        if (!entity.l(this)) {
            float attackDamage = (float) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
            float sharpnessModifier = entity instanceof EntityLiving
                    ? EnchantmentManager.a(this.bA(), ((EntityLiving) entity).getMonsterType())
                    : EnchantmentManager.a(this.bA(), EnumMonsterType.UNDEFINED);

            int knockbackModifire = EnchantmentManager.a(this);

            if (this.isSprinting()) {
                ++knockbackModifire;
            }

            if (attackDamage > 0.0F || sharpnessModifier > 0.0F) {
                boolean isJumpAttack = this.fallDistance > 0.0F && !this.onGround && !this.k_() && !this.V() && !this.hasEffect(MobEffectList.BLINDNESS) && this.vehicle == null && entity instanceof EntityLiving;

                if (isJumpAttack && attackDamage > 0.0F) {
                    attackDamage *= 1.5F;
                }

                attackDamage += sharpnessModifier;
                boolean shouldExtinguish = false;
                int fireAspectEnchantmentLevel = EnchantmentManager.getFireAspectEnchantmentLevel(this);

                if (entity instanceof EntityLiving && fireAspectEnchantmentLevel > 0 && !entity.isBurning()) {
                    // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                    EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 1);
                    Bukkit.getPluginManager().callEvent(combustEvent);

                    if (!combustEvent.isCancelled()) {
                        shouldExtinguish = true;
                        entity.setOnFire(combustEvent.getDuration());
                    }
                    // CraftBukkit end
                }

                double victimMotX = entity.motX;
                double victimMotY = entity.motY;
                double victimMotZ = entity.motZ;
                boolean entityAttackResult = entity.damageEntity(DamageSource.playerAttack(this), attackDamage);

                if (entityAttackResult) {
                    if (knockbackModifire > 0) {
                        entity.g((-MathHelper.sin(this.yaw * 3.1415927F / 180.0F) * (float) knockbackModifire * 0.5F), 0.1D, (double) (MathHelper.cos(this.yaw * 3.1415927F / 180.0F) * (float) knockbackModifire * 0.5F));
                        // This is responsible for reducing knockback
                        // this.motX *= 0.6D;
                        // this.motZ *= 0.6D;
                        // this.setSprinting(false);
                    }

                    if (entity instanceof EntityPlayer && entity.velocityChanged) {
                        // CraftBukkit start - Add Velocity Event
                        boolean cancelled = false;
                        Player player = (Player) entity.getBukkitEntity();
                        Vector velocity = new Vector(victimMotX, victimMotY, victimMotZ);

                        PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                        world.getServer().getPluginManager().callEvent(event);

                        if (event.isCancelled()) {
                            cancelled = true;
                        } else if (!velocity.equals(event.getVelocity())) {
                            player.setVelocity(velocity);
                        }

                        if (!cancelled) {
                            ((EntityPlayer) entity).playerConnection.sendPacket(new PacketPlayOutEntityVelocity(entity));
                            entity.velocityChanged = false;
                            entity.motX = victimMotX;
                            entity.motY = victimMotY;
                            entity.motZ = victimMotZ;
                        }
                        // CraftBukkit end
                    }

                    if (isJumpAttack) {
                        this.b(entity);
                    }

                    if (sharpnessModifier > 0.0F) {
                        this.c(entity);
                    }

                    if (attackDamage >= 18.0F) {
                        this.b(AchievementList.F);
                    }

                    this.p(entity);
                    if (entity instanceof EntityLiving) {
                        EnchantmentManager.a((EntityLiving) entity, this);
                    }

                    EnchantmentManager.b(this, entity);
                    ItemStack itemstack = this.bZ();
                    Entity actualEntity = entity;

                    if (entity instanceof EntityComplexPart) {
                        IComplex baseEntity = ((EntityComplexPart) entity).owner;

                        if (baseEntity instanceof EntityLiving) {
                            actualEntity = (EntityLiving) baseEntity;
                        }
                    }

                    if (itemstack != null && actualEntity instanceof EntityLiving) {
                        itemstack.a((EntityLiving) actualEntity, this);
                        // CraftBukkit - bypass infinite items; <= 0 -> == 0
                        if (itemstack.count == 0) {
                            this.ca();
                        }
                    }

                    if (entity instanceof EntityLiving) {
                        this.a(StatisticList.w, Math.round(attackDamage * 10.0F));
                        if (fireAspectEnchantmentLevel > 0) {
                            // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), fireAspectEnchantmentLevel * 4);
                            Bukkit.getPluginManager().callEvent(combustEvent);

                            if (!combustEvent.isCancelled()) {
                                entity.setOnFire(combustEvent.getDuration());
                            }
                            // CraftBukkit end
                        }
                    }

                    this.applyExhaustion(world.spigotConfig.combatExhaustion); // Spigot - Change to use configurable value
                } else if (shouldExtinguish) {
                    entity.extinguish();
                }
            }

        }
    }
}