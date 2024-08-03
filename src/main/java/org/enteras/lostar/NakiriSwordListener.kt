package org.enteras.lostar

import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

class NakiriSwordListener(private val plugin: EternaL) : Listener {

    // Set to track players currently under Sakura Blend! effect
    private val activePlayers: MutableSet<Player> = HashSet()
    // Set to track players ready for the next Sakura Blend! effect
    private val rechargePlayers: MutableSet<Player> = HashSet()

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (damager is Player) {
            val player = damager
            val weapon = player.inventory.itemInMainHand

            if (isSpecialSword(weapon)) {
                if (!activePlayers.contains(player) && !rechargePlayers.contains(player)) {
                    val entity = event.entity as LivingEntity

                    // Apply additional true damage after a 1-second delay
                    object : BukkitRunnable() {
                        override fun run() {
                            // Calculate additional True Damage
                            val additionalDamage = calculateTrueDamage()

                            // Send message to the player
                            player.sendMessage("${ChatColor.of("#FF69B4")}${ChatColor.BOLD}Sakura Blend! ${ChatColor.RESET}${ChatColor.RED}{additionalDamage}❤${ChatColor.WHITE}의 추가 공격!")

                            // Apply true damage
                            entity.damage(additionalDamage, player)

                            // Mark player as active
                            activePlayers.add(player)

                            // Schedule a task to remove the player from the active set after 1 second
                            object : BukkitRunnable() {
                                override fun run() {
                                    activePlayers.remove(player)
                                }
                            }.runTaskLater(plugin, 15L) // 20 ticks = 1 second
                        }
                    }.runTaskLater(plugin, 20L) // 20 ticks = 1 second

                    // Mark player for recharge
                    rechargePlayers.add(player)

                    // Schedule a task to remove the player from the recharge set after 2 seconds
                    object : BukkitRunnable() {
                        override fun run() {
                            rechargePlayers.remove(player)
                        }
                    }.runTaskLater(plugin, 40L) // 40 ticks = 2 seconds
                }
            }
        }
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity
        val killer = entity.killer
        if (killer is Player) {
            val player = killer
            val mainHandItem = player.inventory.itemInMainHand
            val offHandItem = player.inventory.itemInOffHand

            if (isSpecialSword(mainHandItem) || isSpecialSword(offHandItem)) {
                val droppedItem = ItemStack(Material.WARPED_BUTTON)
                val meta = droppedItem.itemMeta
                meta?.let {
                    it.setDisplayName("§b死者의 핏방울")
                    droppedItem.itemMeta = it
                }
                entity.world.dropItem(entity.location, droppedItem)
            }
        }
    }

    private fun isSpecialSword(item: ItemStack): Boolean {
        val meta = item.itemMeta
        val name = meta?.displayName ?: return false
        return when (item.type) {
            Material.IRON_SWORD -> name == "{\"text\":\"a \",\"color\":\"black\",\"obfuscated\":true}{\"text\":\" \",\"color\":\"black\"}{\"text\":\"鬼神刀阿修羅 \",\"color\":\"aqua\"}{\"text\":\" \",\"color\":\"black\"}{\"text\":\"a\",\"color\":\"black\",\"obfuscated\":true}"
            Material.NETHERITE_SWORD -> name == "妖刀羅刹"
            else -> false
        }
    }

    private fun calculateTrueDamage(): Double {
        // Generate a random value between 1 and 3 (inclusive)
        return 1 + Random.nextInt(3).toDouble() // Convert Int to Double
    }
}
