package org.enteras.lostar

import me.libraryaddict.disguise.DisguiseAPI
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Zombie
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class RedaLListener(private val plugin: JavaPlugin) : Listener {

    companion object {
        val BOSS_NAME = "${ChatColor.DARK_RED}${ChatColor.BOLD}[BOSS]${ChatColor.RED}[Lv.500] ${ChatColor.WHITE}Reptagram"
    }

    private val zombieArmorStandMap = mutableMapOf<Zombie, ArmorStand>()
    private val zombieAttackCountMap = mutableMapOf<Zombie, Int>() // 좀비 카운트

    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        val entity = event.entity

        if (entity.type == EntityType.ZOMBIE) {
            val zombie = entity as Zombie

            if (zombie.customName == "[Lv.500] Reptagram") {
                applyPlayerSkinToZombie(zombie, "Reptagram")
                setZombieCustomName(zombie)
                summonArmorStand(zombie)
                equipZombieWithWeapons(zombie)
                applyInfiniteFireResistance(zombie)
                setZombieHealth(zombie)
            }
        }
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity

        if (entity.type == EntityType.ZOMBIE) {
            val zombie = entity as Zombie
            val killer = entity.killer ?: return

            if (zombie.customName == BOSS_NAME) {
                LevelManager.addExperience(killer.uniqueId, 100)
            }

            val armorStand = zombieArmorStandMap.remove(zombie)
            armorStand?.remove()

            event.drops.clear()
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val entity = event.entity

        if (entity is Zombie && entity.customName == BOSS_NAME) {
            if (event.cause == EntityDamageEvent.DamageCause.FIRE || event.cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val entity = event.entity

        if (damager is Zombie && damager.customName == BOSS_NAME && entity is Player) {
            val zombie = damager
            val currentCount = zombieAttackCountMap.getOrDefault(zombie, 0)
            val newCount = currentCount + 1
            zombieAttackCountMap[zombie] = newCount

            if (newCount >= 5) {
                inflictDamageToNearbyPlayers(zombie, 10.0, 5.0)
                zombieAttackCountMap[zombie] = 0
            }
        }
    }

    private fun inflictDamageToNearbyPlayers(zombie: Zombie, radius: Double, damage: Double) {
        val nearbyEntities = zombie.location.getNearbyEntities(radius, radius, radius)
        for (entity in nearbyEntities) {
            if (entity is Player) {
                val player = entity
                player.damage(damage, zombie)
                player.playSound(player.location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 1.0f)
            }
        }
    }

    private fun applyPlayerSkinToZombie(zombie: Zombie, playerName: String) {
        val disguise = PlayerDisguise(playerName)
        DisguiseAPI.disguiseToAll(zombie, disguise)
    }

    private fun setZombieCustomName(zombie: Zombie) {
        zombie.customName = BOSS_NAME
        zombie.isCustomNameVisible = true
    }

    private fun summonArmorStand(zombie: Zombie) {
        val location = zombie.location.add(0.0, 1.0, 0.0)
        val armorStand = zombie.world.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand

        armorStand.customName = BOSS_NAME
        armorStand.isCustomNameVisible = true
        armorStand.isInvisible = true
        armorStand.isInvulnerable = true
        armorStand.isMarker = true

        zombieArmorStandMap[zombie] = armorStand

        object : BukkitRunnable() {
            override fun run() {
                if (!zombie.isValid || !armorStand.isValid) {
                    armorStand.remove()
                    zombieArmorStandMap.remove(zombie)
                    cancel()
                    return
                }
                armorStand.teleport(zombie.location.add(0.0, 2.0, 0.0))
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    private fun equipZombieWithWeapons(zombie: Zombie) {
        val netheriteSword = ItemStack(Material.NETHERITE_SWORD)
        val ironSword = ItemStack(Material.IRON_SWORD)

        val netheriteMeta: ItemMeta? = netheriteSword.itemMeta
        val ironMeta: ItemMeta? = ironSword.itemMeta

        if (netheriteMeta != null) {
            netheriteSword.itemMeta = netheriteMeta
        }

        if (ironMeta != null) {
            ironSword.itemMeta = ironMeta
        }

        zombie.equipment?.setItemInMainHand(netheriteSword)
        zombie.equipment?.setItemInOffHand(ironSword)
    }

    private fun applyInfiniteFireResistance(zombie: Zombie) {
        val fireResistanceEffect = PotionEffect(PotionEffectType.FIRE_RESISTANCE, Int.MAX_VALUE, 0, false, false)
        zombie.addPotionEffect(fireResistanceEffect)
        val speedEffect = PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 3, false, false)
        zombie.addPotionEffect(speedEffect)
    }

    private fun setZombieHealth(zombie: Zombie) {
        val maxHealth = 500.0
        zombie.maxHealth = maxHealth
        zombie.health = maxHealth
    }
}
