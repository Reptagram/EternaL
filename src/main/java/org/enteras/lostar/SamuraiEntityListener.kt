package org.enteras.lostar

import me.libraryaddict.disguise.DisguiseAPI
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Zombie
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SamuraiEntityListener(private val plugin: JavaPlugin) : Listener { ///summon zombie ~ ~ ~ {CustomName:'[{"text":"[Lv.20] 불의 사무라이"}]'}

    private val zombieArmorStandMap = mutableMapOf<Zombie, ArmorStand>()

    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        val entity = event.entity

        if (entity.type == EntityType.ZOMBIE) {
            val zombie = entity as Zombie

            if (zombie.customName == "[Lv.20] 불의 사무라이") {
                applyPlayerSkinToZombie(zombie, "GAMA_official")
                setZombieCustomName(zombie)
                summonArmorStand(zombie)
                equipZombieWithIronSword(zombie)
                applyInfiniteFireResistance(zombie)
                setZombieHealth(zombie) // Set the health of the zombie
            }
        }
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity

        if (entity.type == EntityType.ZOMBIE) {
            val zombie = entity as Zombie

            // 경험치를 추가할 플레이어 확인
            val killer = entity.killer ?: return // 엔티티를 죽인 플레이어가 없으면 종료

            val customName = "${ChatColor.RED}[Lv.20] ${ChatColor.WHITE}불의 사무라이"

            // 이름이 "[Lv.20] Samurai"인 경우에만 8 경험치 추가
            if (zombie.customName == customName) {
                LevelManager.addExperience(killer.uniqueId, 8)
            }

            // Remove the associated armor stand
            val armorStand = zombieArmorStandMap.remove(zombie)
            armorStand?.remove()

            // Prevent all items from dropping
            event.drops.clear()
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val entity = event.entity

        if (entity is Zombie && entity.customName == "[Lv.20] 불의 사무라이") {
            // Cancel fire damage to prevent burning in sunlight
            if (event.cause == EntityDamageEvent.DamageCause.FIRE || event.cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                event.isCancelled = true
            }
        }
    }

    private fun applyPlayerSkinToZombie(zombie: Zombie, playerName: String) {
        // Create PlayerDisguise object
        val disguise = PlayerDisguise(playerName)

        // Disguise the zombie with the player skin
        DisguiseAPI.disguiseToAll(zombie, disguise)
    }

    private fun setZombieCustomName(zombie: Zombie) {
        // Set custom name
        val customName = "${ChatColor.RED}[Lv.20] ${ChatColor.WHITE}불의 사무라이"
        zombie.customName = customName
        zombie.isCustomNameVisible = true
    }

    private fun summonArmorStand(zombie: Zombie) {
        val location = zombie.location.add(0.0, 1.0, 0.0) // Position the armor stand above the zombie
        val armorStand = zombie.world.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand

        armorStand.customName = "${ChatColor.RED}[Lv.20] ${ChatColor.WHITE}불의 사무라이"
        armorStand.isCustomNameVisible = true
        armorStand.isInvisible = true
        armorStand.isInvulnerable = true
        armorStand.isMarker = true // Non-interactable and removes hitbox

        // Store the armor stand in the map
        zombieArmorStandMap[zombie] = armorStand

        // Schedule a task to update the armor stand's position to follow the zombie
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
        }.runTaskTimer(plugin, 0L, 1L) // Run the task every tick
    }

    private fun equipZombieWithIronSword(zombie: Zombie) {
        val ironSword = ItemStack(Material.IRON_SWORD)
        val meta: ItemMeta? = ironSword.itemMeta // Retrieve ItemMeta

        if (meta != null) {
            ironSword.itemMeta = meta // Apply updated meta back to ItemStack
        }

        zombie.equipment?.setItemInMainHand(ironSword)
    }

    private fun applyInfiniteFireResistance(zombie: Zombie) {
        // Apply infinite Fire Resistance
        val fireResistanceEffect = PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false)
        zombie.addPotionEffect(fireResistanceEffect)
    }

    private fun setZombieHealth(zombie: Zombie) {
        // Set maximum health and current health to 40
        val maxHealth = 40.0
        zombie.maxHealth = maxHealth
        zombie.health = maxHealth
    }
}
