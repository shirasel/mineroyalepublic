package me.shirasemaru.mineroyale1218

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class ConfigManager(private val plugin: JavaPlugin) {
    private var config: FileConfiguration = plugin.config

    // ゲーム開始前のカウントダウン秒数
    val countdownSeconds: Int
        get() = config.getInt("countdown-seconds", 30)

    // ゲーム全体の制限時間（秒）
    val gameDurationSeconds: Long
        get() = config.getLong("game-duration-seconds", 1800L)

    // 初期ボーダーサイズ
    val initialBorderSize: Double
        get() = config.getDouble("initial-border-size", 70.0)

    // テレポート関連
    val teleportCenterX: Double
        get() = config.getDouble("teleport-center-x", 0.0)
    val teleportCenterZ: Double
        get() = config.getDouble("teleport-center-z", 0.0)
    val teleportRadius: Int
        get() = config.getInt("teleport-radius", 1000)
    val teleportRange: Int
        get() = config.getInt("teleportRange", 500000)

    // デス時のインベントリ更新遅延
    val deathInventoryUpdateDelayTicks: Long
        get() = config.getLong("deathInventoryUpdateDelayTicks", 20L)

    // ボーダー縮小スケジュール（分 → サイズ）
    val borderShrinkSchedule: Map<Int, Int>
        get() {
            val section = config.getConfigurationSection("border-shrink-schedule") ?: return emptyMap()
            return section.getKeys(false)
                .mapNotNull { key ->
                key.toIntOrNull()?.let { it to section.getInt(key) }
                }
                .toMap()
        }

    // 設定をリロード
    fun reload() {
        plugin.reloadConfig()
        config = plugin.config
        plugin.logger.info("config.yml をリロードしました。")
    }
}
