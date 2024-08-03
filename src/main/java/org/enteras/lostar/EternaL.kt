package org.enteras.lostar

import org.bukkit.plugin.java.JavaPlugin

class EternaL : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(SamuraiEntityListener(this), this)
        server.pluginManager.registerEvents(VanillaEntityExperience(), this)
        server.pluginManager.registerEvents(ChangeNameListener(this), this)
        server.pluginManager.registerEvents(NakiriAyameListener(this), this)
        server.pluginManager.registerEvents(NakiriSwordListener(this), this)
        LevelManager.init(this)

        // ActionBarTask 주기적으로 실행
        ActionBarTask(this).runTaskTimer(this, 0L, 20L)
    }

    override fun onDisable() {
        LevelManager.saveData()
    }
}
