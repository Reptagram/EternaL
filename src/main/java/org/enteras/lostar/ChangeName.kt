package org.enteras.lostar

import net.md_5.bungee.api.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.entity.Player

class ChangeNameListener(private val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        // 플레이어의 이름이 "Reptagram"인지 확인
        if (player.name == "Reptagram") {
            // 플레이어의 표시 이름과 플레이어 리스트 이름을 변경
            player.setDisplayName("${ChatColor.of("#90d2f8")}${ChatColor.BOLD}DEV ${ChatColor.of("#FFFFFF")}${ChatColor.BOLD}百鬼れだる${ChatColor.YELLOW}⚝")
            player.setPlayerListName("${ChatColor.of("#90d2f8")}${ChatColor.BOLD}DEV ${ChatColor.of("#FFFFFF")}${ChatColor.BOLD}百鬼れだる${ChatColor.YELLOW}⚝")
        }

        if (player.name == "Xx_KOIJI_xX") {
            // 플레이어의 표시 이름과 플레이어 리스트 이름을 변경
            player.setDisplayName("${ChatColor.of("#90d2f8")}${ChatColor.BOLD}DEV ${ChatColor.of("#FFFFFF")}Xx_KOIJI_xX")
            player.setPlayerListName("${ChatColor.of("#90d2f8")}${ChatColor.BOLD}DEV ${ChatColor.of("#FFFFFF")}Xx_KOIJI_xX")
        }
    }
}
