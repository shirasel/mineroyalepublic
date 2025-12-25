package me.shirasemaru.mineroyale1218

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.potion.PotionEffectType

class DeathHandler(
    private val gameManager: GameManager
) : Listener {

    private var updateScheduled = false

    init {
        Bukkit.getPluginManager().registerEvents(this, gameManager.plugin)
    }

    fun handlePlayerDeath(player: Player) {
        gameManager.handleDeath(player)
        player.removePotionEffect(PotionEffectType.GLOWING)
        player.sendMessage("§cあなたは死亡しました。観戦モードです。")
        scheduleInventoryUpdate()
    }

    private fun scheduleInventoryUpdate() {
        if (updateScheduled) return
        updateScheduled = true

        val delay = gameManager.config.deathInventoryUpdateDelayTicks
        Bukkit.getScheduler().runTaskLater(gameManager.plugin, Runnable {
            updateDeadInventories()
            updateScheduled = false
        }, delay)
    }

    private fun updateDeadInventories() {
        val deadPlayers = gameManager.getDeadPlayers()
        val alivePlayers = gameManager.getAlivePlayers()

        deadPlayers.forEach { dead ->
            dead.inventory.clear()
            alivePlayers.forEach { surviver ->
                val skull = ItemStack(Material.PLAYER_HEAD, 1)
                val meta: ItemMeta? = skull.itemMeta
                meta?.setDisplayName("${surviver.name} の頭")
                skull.itemMeta = meta
                dead.inventory.addItem(skull)
            }
        }
    }

    @EventHandler
    fun onPlayerUseHead(event: PlayerInteractEvent) {
        val player = event.player
        val item: ItemStack = player.inventory.itemInMainHand
        if (item.type != Material.PLAYER_HEAD) return
        val meta = item.itemMeta ?: return
        val targetName = meta.displayName.removeSuffix(" の頭")
        if (event.action != Action.LEFT_CLICK_AIR && event.action != Action.LEFT_CLICK_BLOCK) return
        val target = Bukkit.getPlayerExact(targetName)
        if (target != null && gameManager.getAlivePlayers().contains(target)) {
            player.teleport(target.location)
            player.sendMessage("§a${target.name} にテレポートしました！")
        } else {
            player.sendMessage("§c${targetName} は現在生存していません。")
        }
    }
}