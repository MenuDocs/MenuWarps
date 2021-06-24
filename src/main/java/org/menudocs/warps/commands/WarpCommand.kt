package org.menudocs.warps.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.menudocs.warps.MenuWarps
import org.menudocs.warps.util.Command
import org.menudocs.warps.util.normalized

object WarpCommand : Command() {
  override val name = "warp"

  override fun execute(sender: CommandSender, label: String, args: MutableList<String>): Boolean {
    if (sender !is Player) {
      return true
    }

    val warpName = args.joinToString(" ").lowercase()
    val warp = MenuWarps.warpManager.warps.find { it.id == warpName.replace(' ', '-') || it.name.lowercase() == warpName }

    if (warp == null) {
      sender.sendMessage("&c&lWarning:&r unable to find a warp with that &oid&r or &oname&r".normalized)
      return false
    }

    if (warp.acceptance == null) {
      sender.sendMessage("&c&lWarning:&r That warp hasn't been accepted yet, bug an admin!".normalized)
      return false
    }

    warp.take(sender)
    return true
  }
}