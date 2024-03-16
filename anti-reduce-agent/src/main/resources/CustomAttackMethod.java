public void attack(Entity $1) {
    System.out.println("Attack method called");
    if ($1.aD()) {
        if (!$1.l($0)) {
            float attackDamage = (float) $0.getAttributeInstance(net.minecraft.server.v1_8_R3.GenericAttributes.ATTACK_DAMAGE).getValue();
            float sharpnessModifier = $1 instanceof net.minecraft.server.v1_8_R3.EntityLiving
                    ? net.minecraft.server.v1_8_R3.EnchantmentManager.a($0.bA(), ((net.minecraft.server.v1_8_R3.EntityLiving) $1).getMonsterType())
                    : net.minecraft.server.v1_8_R3.EnchantmentManager.a($0.bA(), net.minecraft.server.v1_8_R3.EnumMonsterType.UNDEFINED);

            int knockbackModifire = net.minecraft.server.v1_8_R3.EnchantmentManager.a($0);

            if ($0.isSprinting()) {
                ++knockbackModifire;
            }

            if (attackDamage > 0.0F || sharpnessModifier > 0.0F) {
                boolean isJumpAttack = $0.fallDistance > 0.0F && !$0.onGround && !$0.k_() && !$0.V() && !$0.hasEffect(net.minecraft.server.v1_8_R3.MobEffectList.BLINDNESS) && $0.vehicle == null && $1 instanceof net.minecraft.server.v1_8_R3.EntityLiving;

                if (isJumpAttack && attackDamage > 0.0F) {
                    attackDamage *= 1.5F;
                }

                attackDamage += sharpnessModifier;
                boolean shouldExtinguish = false;
                int fireAspectEnchantmentLevel = net.minecraft.server.v1_8_R3.EnchantmentManager.getFireAspectEnchantmentLevel($0);

                if ($1 instanceof net.minecraft.server.v1_8_R3.EntityLiving && fireAspectEnchantmentLevel > 0 && !$1.isBurning()) {
                    // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                    org.bukkit.event.entity.EntityCombustByEntityEvent combustEvent = new org.bukkit.event.entity.EntityCombustByEntityEvent($0.getBukkitEntity(),
                            $1.getBukkitEntity(), 1);
                    org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

                    if (!combustEvent.isCancelled()) {
                        shouldExtinguish = true;
                        $1.setOnFire(combustEvent.getDuration());
                    }
                    // CraftBukkit end
                }

                double victimMotX = $1.motX;
                double victimMotY = $1.motY;
                double victimMotZ = $1.motZ;
                boolean entityAttackResult = $1.damageEntity(net.minecraft.server.v1_8_R3.DamageSource.playerAttack($0), attackDamage);

                if (entityAttackResult) {
                    if (knockbackModifire > 0) {
                        $1.g((double) (-net.minecraft.server.v1_8_R3.MathHelper.sin($0.yaw * 3.1415927F / 180.0F) * (float) knockbackModifire * 0.5F), 0.1D,
                                (double) (net.minecraft.server.v1_8_R3.MathHelper.cos($0.yaw * 3.1415927F / 180.0F) * (float) knockbackModifire * 0.5F));
                        // This is responsible for reducing knockback
                        // $0.motX *= 0.6D;
                        // $0.motZ *= 0.6D;
                        // $0.setSprinting(false);
                    }

                    if ($1 instanceof net.minecraft.server.v1_8_R3.EntityPlayer && $1.velocityChanged) {
                        // CraftBukkit start - Add Velocity Event
                        boolean cancelled = false;
                        org.bukkit.entity.Player player = (org.bukkit.entity.Player) $1.getBukkitEntity();
                        org.bukkit.util.Vector velocity = new org.bukkit.util.Vector(victimMotX, victimMotY, victimMotZ);

                        org.bukkit.event.player.PlayerVelocityEvent event = new org.bukkit.event.player.PlayerVelocityEvent(player, velocity.clone());
                        org.bukkit.Bukkit.getPluginManager().callEvent(event);

                        if (event.isCancelled()) {
                            cancelled = true;
                        } else if (!velocity.equals(event.getVelocity())) {
                            player.setVelocity(velocity);
                        }

                        if (!cancelled) {
                            ((net.minecraft.server.v1_8_R3.EntityPlayer) $1).playerConnection.sendPacket(new net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity($1));
                            $1.velocityChanged = false;
                            $1.motX = victimMotX;
                            $1.motY = victimMotY;
                            $1.motZ = victimMotZ;
                        }
                        // CraftBukkit end
                    }

                    if (isJumpAttack) {
                        $0.b($1);
                    }

                    if (sharpnessModifier > 0.0F) {
                        $0.c($1);
                    }

                    if (attackDamage >= 18.0F) {
                        $0.b(net.minecraft.server.v1_8_R3.AchievementList.F);
                    }

                    $0.p($1);
                    if ($1 instanceof net.minecraft.server.v1_8_R3.EntityLiving) {
                        net.minecraft.server.v1_8_R3.EnchantmentManager.a((net.minecraft.server.v1_8_R3.EntityLiving) $1, $0);
                    }

                    net.minecraft.server.v1_8_R3.EnchantmentManager.b($0, $1);
                    net.minecraft.server.v1_8_R3.ItemStack itemstack = $0.bZ();
                    net.minecraft.server.v1_8_R3.Entity actualEntity = $1;

                    if ($1 instanceof net.minecraft.server.v1_8_R3.EntityComplexPart) {
                        net.minecraft.server.v1_8_R3.IComplex baseEntity = ((net.minecraft.server.v1_8_R3.EntityComplexPart) $1).owner;

                        if (baseEntity instanceof net.minecraft.server.v1_8_R3.EntityLiving) {
                            actualEntity = (net.minecraft.server.v1_8_R3.EntityLiving) baseEntity;
                        }
                    }

                    if (itemstack != null && actualEntity instanceof net.minecraft.server.v1_8_R3.EntityLiving) {
                        itemstack.a((net.minecraft.server.v1_8_R3.EntityLiving) actualEntity, $0);
                        // CraftBukkit - bypass infinite items; <= 0 -> == 0
                        if (itemstack.count == 0) {
                            $0.ca();
                        }
                    }

                    if ($1 instanceof net.minecraft.server.v1_8_R3.EntityLiving) {
                        $0.a(net.minecraft.server.v1_8_R3.StatisticList.w, Math.round(attackDamage * 10.0F));
                        if (fireAspectEnchantmentLevel > 0) {
                            // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                            org.bukkit.event.entity.EntityCombustByEntityEvent combustEvent = new org.bukkit.event.entity.EntityCombustByEntityEvent($0.getBukkitEntity(), $1.getBukkitEntity(),
                                    fireAspectEnchantmentLevel * 4);
                            org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

                            if (!combustEvent.isCancelled()) {
                                $1.setOnFire(combustEvent.getDuration());
                            }
                            // CraftBukkit end
                        }
                    }

                    $0.applyExhaustion(world.spigotConfig.combatExhaustion); // Spigot - Change to use configurable value
                } else if (shouldExtinguish) {
                    $1.extinguish();
                }
            }

        }
    }
}