package org.enteras.lostar

import org.bukkit.entity.Entity
import org.bukkit.entity.Zombie
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.entity.Player
import me.libraryaddict.disguise.DisguiseAPI
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder

class SamuraiEntityListener : Listener {

    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        val entity = event.entity

        if (entity.type == EntityType.ZOMBIE) {
            val zombie = entity as Zombie

            if (zombie.customName == "[Lv.20] Samurai") {
                applyPlayerSkinToZombie(zombie, "zoupee")
                setZombieCustomName(zombie)
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

            // 이름이 "[Lv.20] Samurai"인 경우에만 8 경험치 추가
            if (zombie.customName == "[Lv.20] Samurai") {
                LevelManager.addExperience(killer.uniqueId, 8)
            }
        }
    }

    private fun applyPlayerSkinToZombie(zombie: Zombie, playerName: String) {
        // PlayerDisguise 객체 생성
        val disguise = PlayerDisguise(playerName)

        // 몹을 플레이어로 변장시키고, 해당 스킨을 적용
        DisguiseAPI.disguiseToAll(zombie, disguise)
    }

    private fun setZombieCustomName(zombie: Zombie) {
        val customName = ComponentBuilder("[Lv.20]")
            .color(ChatColor.RED)
            .append(" Samurai")
            .color(ChatColor.GRAY)
            .create()

        zombie.customName = net.md_5.bungee.api.chat.TextComponent(*customName).toLegacyText()
        zombie.isCustomNameVisible = true
    }
}
