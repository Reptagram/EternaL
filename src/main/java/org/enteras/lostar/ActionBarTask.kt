package org.enteras.lostar

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class ActionBarTask(private val plugin: JavaPlugin) : BukkitRunnable() {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val playerData = LevelManager.getPlayerData(player.uniqueId)
            val level = playerData.level
            val experience = playerData.experience
            val experienceForNextLevel = LevelManager.getExperienceForNextLevel(level) // 항상 100으로 설정되어야 합니다
            val progress = (experience.toFloat() / experienceForNextLevel * 20).toInt() // 20은 진행 바의 총 길이

            val levelDisplay = when {
                level >= 200 -> getSpecialLevelDisplay(level)
                level >= 100 -> getRainbowLevelDisplay(level)
                else -> getLevelDisplay(level)
            }

            val bar = buildString {
                append(ChatColor.RED).append(player.name).append(" [")
                append(levelDisplay)
                append(ChatColor.WHITE).append("] ")
                append(ChatColor.GREEN).append("|".repeat(progress))
                append(ChatColor.DARK_GRAY).append("|".repeat(20 - progress))
                append(ChatColor.WHITE).append(" ").append(experience).append("/").append(experienceForNextLevel)
                append(ChatColor.WHITE).append(" [").append(level + 1).append("]")
            }

            player.sendActionBar(bar)
        }
    }

    private fun getSpecialLevelDisplay(level: Int): String {
        val levelString = level.toString()
        val colors = arrayOf(
            ChatColor.WHITE, ChatColor.GREEN, ChatColor.YELLOW,
            ChatColor.GOLD, ChatColor.RED
        )

        return buildString {
            append('[')
            levelString.forEachIndexed { index, char ->
                append(colors.getOrElse(index) { ChatColor.WHITE })
                append(char)
            }
            append(ChatColor.GOLD).append("✯") // 별 기호
            append(']')
        }
    }

    private fun getLevelColors(level: Int): Pair<ChatColor, ChatColor> {
        return when {
            level in 0..10 -> Pair(ChatColor.WHITE, ChatColor.GRAY)
            level in 11..20 -> Pair(ChatColor.GOLD, ChatColor.WHITE)
            else -> Pair(ChatColor.WHITE, ChatColor.WHITE) // 기본 색상 설정
        }
    }

    private fun getLevelDisplay(level: Int): String {
        val (levelColor, numberColor) = getLevelColors(level)
        return buildString {
            append(levelColor)
            append(level)
        }
    }

    private fun getRainbowLevelDisplay(level: Int): String {
        val levelString = level.toString()
        val colors = arrayOf(
            ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW,
            ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE,
            ChatColor.LIGHT_PURPLE
        )

        return buildString {
            append('[')
            levelString.forEachIndexed { index, char ->
                append(colors[index % colors.size])
                append(char)
            }
            append(ChatColor.WHITE).append("⚝") // 별 기호
            append(']')
        }
    }
}
