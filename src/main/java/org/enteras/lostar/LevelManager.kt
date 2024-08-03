package org.enteras.lostar

import net.md_5.bungee.api.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlin.collections.HashMap

data class PlayerData(var experience: Int = 0, var level: Int = 1)

object LevelManager {
    private val playerDataMap: MutableMap<UUID, PlayerData> = HashMap()
    private lateinit var dataFile: File
    private lateinit var config: FileConfiguration

    // 플레이어 데이터를 가져오거나 새로 만듦
    fun getPlayerData(playerId: UUID): PlayerData {
        return playerDataMap.computeIfAbsent(playerId) { PlayerData() }
    }

    // 경험치 추가 및 레벨업 처리
    fun addExperience(playerId: UUID, amount: Int) {
        val data = getPlayerData(playerId)
        data.experience += amount
        checkLevelUp(playerId)
    }

    // 레벨업을 위한 경험치 계산 (각 레벨마다 100 경험치)
    fun getExperienceForNextLevel(level: Int): Int {
        return 100 // 레벨 간에 필요한 경험치는 일정
    }

    // 레벨업 체크 및 레벨업 처리
    private fun checkLevelUp(playerId: UUID) {
        val data = getPlayerData(playerId)
        while (data.experience >= getExperienceForNextLevel(data.level)) {
            data.experience -= getExperienceForNextLevel(data.level)
            data.level++
        }
    }

    // YAML 파일에 데이터 저장
    fun saveData() {
        for ((uuid, data) in playerDataMap) {
            val section = config.createSection(uuid.toString())
            section.set("experience", data.experience)
            section.set("level", data.level)
        }

        try {
            config.save(dataFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // YAML 파일에서 데이터 로드
    fun loadData() {
        if (!dataFile.exists()) return

        for (key in config.getKeys(false)) {
            val uuid = UUID.fromString(key)
            val experience = config.getInt("$key.experience")
            val level = config.getInt("$key.level")
            playerDataMap[uuid] = PlayerData(experience, level)
        }
    }

    // 레벨에 따른 색상 설정
    private fun getLevelColor(level: Int): Array<ChatColor> {
        return when {
            level in 0..10 -> arrayOf(ChatColor.WHITE, ChatColor.GRAY, ChatColor.GRAY, ChatColor.WHITE)
            level in 11..20 -> arrayOf(ChatColor.WHITE, ChatColor.GOLD, ChatColor.GOLD, ChatColor.WHITE)
            level in 21..30 -> arrayOf(ChatColor.WHITE, ChatColor.YELLOW, ChatColor.YELLOW, ChatColor.WHITE)
            level in 31..40 -> arrayOf(ChatColor.WHITE, ChatColor.GREEN, ChatColor.GREEN, ChatColor.WHITE)
            level >= 100 -> {
                val levelString = level.toString()
                Array(levelString.length) { index -> getRainbowColor(level, index) }
            }
            else -> arrayOf(ChatColor.WHITE, ChatColor.RED, ChatColor.RED, ChatColor.WHITE)
        }
    }

    // 레벨을 무지개 색상으로 포맷
    private fun getRainbowColor(level: Int, index: Int): ChatColor {
        val colors = arrayOf(
            ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW,
            ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE,
            ChatColor.DARK_PURPLE
        )
        return colors[(index % colors.size)]
    }

    // 레벨을 포맷하여 반환
    fun getFormattedLevel(level: Int): String {
        val levelString = level.toString()
        val colors = getLevelColor(level)

        return buildString {
            append('[')
            levelString.forEachIndexed { index, char ->
                append(colors.getOrElse(index) { ChatColor.WHITE })
                append(char)
            }
            append(ChatColor.WHITE)
            append('⚝')
            append(ChatColor.WHITE)
            append(']')
        }
    }

    // 초기화 메서드
    fun init(plugin: JavaPlugin) {
        dataFile = File(plugin.dataFolder, "playerData.yml")
        if (!dataFile.exists()) {
            dataFile.parentFile.mkdirs()
            plugin.saveResource("playerData.yml", false)
        }
        config = YamlConfiguration.loadConfiguration(dataFile)
        loadData()
    }
}
