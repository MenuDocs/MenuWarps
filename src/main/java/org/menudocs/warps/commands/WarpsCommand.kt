package org.menudocs.warps.commands

import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.menudocs.warps.MenuWarps
import org.menudocs.warps.data.Warp
import org.menudocs.warps.util.*
import xyz.nkomarn.Honeycomb.menu.Menu
import xyz.nkomarn.Honeycomb.menu.MenuButton

object WarpsCommand : Command() {

  override val name: String = "warps"
  override val subCommands = listOf(Save, Create, List)
  override val tabCompleter: TabCompleter = TabCompleter { _, _, _, args ->
    val subCommand = args.firstOrNull()
    if (subCommand == null) {
      subCommands.map { it.name }
    } else {
      val matches = subCommands.any { it.name == subCommand.lowercase() }
      if (matches) {
        emptyList()
      } else {
        MenuWarps.warpManager.warps.map { it.name }
      }
    }
  }

  override fun execute(sender: CommandSender, label: String, args: MutableList<String>): Boolean {
    if (sender !is Player) {
      return true
    }

    return executeSubCommand(sender, args, default = "list", noArgs = "list")
  }

  object List : Command() {

    override val name: String = "list"
    override fun execute(sender: CommandSender, label: String, args: MutableList<String>): Boolean {
      if (sender !is Player) {
        return true
      }

      fun open() {
        val menu = Menu(sender, "Available Warps", 45) // 45
          .fillMenuDocs()

        val warps = MenuWarps.warpManager.warps.filter { it.acceptance != null || sender.hasPermission("warps.accept") }
        if (warps.isEmpty()) {
          val paperStack = ItemStack(Material.PAPER)
          paperStack.itemMeta?.apply {
            setDisplayName("&cOh no!".normalized)
            lore = listOf("It looks like there are no warps", "for this server you should", "try creating some.").map {
              "&f$it".normalized
            }

            paperStack.itemMeta = this
          }

          menu.add {
            item = paperStack
            slot = 22
          }
        } else {
          var increase = 0
          for ((idx, warp) in warps.take(21).withIndex()) {
            if (idx > 0 && idx.mod(7) == 0) {
              increase += 2
            }

            val accepted = warp.acceptance != null
            val pearlStack = ItemStack(if (accepted) Material.ENDER_EYE else Material.ENDER_PEARL)

            /* action message */
            var actionMessage = ""
            if (accepted) {
              actionMessage = "&bClick to warp&r"
              if (sender.hasPermission("warps.delete-warp")) {
                actionMessage += "&f,&r &cRight click to delete!&r"
              }
            } else {
              actionMessage = "&9Click to Accept&r&f,&r Right Click to Deny."
            }

            /* item meta */
            pearlStack.itemMeta?.apply {
              setDisplayName(warp.name)
              lore = listOf("&f${warp.description}".normalized, actionMessage.normalized)
              pearlStack.itemMeta = this
            }

            /* button stuff */
            menu.add {
              item = pearlStack
              slot = idx + 10 + increase
              onClick = MenuButton.GuiButtonCallback { _, clickType ->
                when (clickType) {
                  ClickType.RIGHT -> if (sender.hasPermission("warps.delete")) {
                    val confirmMenu = ConfirmMenu(sender, "${if (accepted) "Remove" else "Deny"} Warp") { confirmed ->
                      if (confirmed) MenuWarps.warpManager.remove(warp)
                      open()
                    }

                    confirmMenu.open()
                  }

                  ClickType.SHIFT_RIGHT -> {
                    MenuWarps.warpManager.remove(warp)
                    open()
                  }

                  ClickType.LEFT -> if (accepted) {
                    warp.take(sender)
                  } else {
                    warp.acceptance = Warp.Acceptance(by = sender.uniqueId, date = System.currentTimeMillis())
                    open()
                  }

                  else -> {
                    if (!accepted && clickType == ClickType.SHIFT_LEFT) warp.take(sender)
                  }
                }
              }
            }
          }


        }

        menu.open()
      }

      open()
      return true
    }
  }

  object Save : Command() {
    override val name: String = "save"

    override fun execute(sender: CommandSender, label: String, args: MutableList<String>): Boolean {
      if (sender !is Player) {
        return true
      }

      try {
        MenuWarps.warpManager.save(true)
        sender.sendMessage("&a&lSuccessfully&r&a saved all warps.&r".normalized)
      } catch (ex: Exception) {
        sender.sendMessage("&c&lError:&r Uh oh, something went wrong while saving the warps, check console.".normalized)
        ex.printStackTrace()
      }

      return true
    }
  }

  object Create : Command() {
    override val name: String = "create"
    override fun execute(sender: CommandSender, label: String, args: MutableList<String>): Boolean {
      if (sender !is Player) {
        return true
      }

      val name = args.elementAtOrNull(0)
      if (name == null) {
        sender.sendMessage("&c&lWarning:&r make sure to give this warp a name!".normalized)
        return false
      }

      val description = args.drop(1).joinToString(" ")
      if (description.isBlank()) {
        sender.sendMessage("&c&lWarning:&r make sure to give this warp a description")
        return false
      }

      MenuWarps.warpManager.add {
        this.name = name
        this.location = sender.location
        this.creator = sender.uniqueId
        this.description = description
      }

      sender.sendMessage("Created warp &l$name&r".normalized)
      return true
    }
  }

}