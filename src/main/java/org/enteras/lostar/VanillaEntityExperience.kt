package org.enteras.lostar

import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class VanillaEntityExperience : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity

        // 경험치를 추가할 플레이어 확인
        val killer = entity.killer ?: return // 엔티티를 죽인 플레이어가 없으면 종료

        // 이름이 없는 경우에만 경험치 추가
        if (entity.customName == null) {
            val experienceAmount = when (entity.type) {
                EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER -> 4
                else -> return // 해당 엔티티가 아니면 아무 것도 하지 않음
            }

            // 플레이어의 UUID를 통해 PlayerData를 가져옴
            LevelManager.addExperience(killer.uniqueId, experienceAmount)
        }
    }
}
