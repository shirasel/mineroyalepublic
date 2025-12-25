package me.shirasemaru.mineroyale1218

import org.bukkit.plugin.java.JavaPlugin

class Mineroyale1218 : JavaPlugin() {
    companion object {
        lateinit var instance: Mineroyale1218
            private set
    }

    lateinit var configManager: ConfigManager
    lateinit var teleportManager: TeleportManager
    lateinit var gameManager: GameManager
    lateinit var deathHandler: DeathHandler

    override fun onEnable() {
        instance = this

        // Config 読み込み
        saveDefaultConfig()
        configManager = ConfigManager(this)

        // Manager 初期化
        teleportManager = TeleportManager(this, configManager)
        gameManager = GameManager(this, configManager, teleportManager)
        deathHandler = DeathHandler(gameManager)

        // listener 登録
        server.pluginManager.registerEvents(PlayerListener(gameManager, deathHandler), this)

        // commands 登録
        getCommand("royalestart")?.setExecutor(RoyaleStartCommand(gameManager))
        getCommand("royaleend")?.setExecutor(RoyaleEndCommand(gameManager))
        getCommand("royalereload")?.setExecutor { sender, _, _, _ ->
            configManager.reload()
            sender.sendMessage("§a[Royal] config.yml を再読み込みしました！")
            true
        }


        logger.info("mineroyale プラグインが有効になりました。")
    }

    override fun onDisable() {
        logger.info("mineroyale プラグインが無効になりました。")
    }
}
