package org.menudocs.warps;

import org.bukkit.plugin.java.JavaPlugin;
import org.menudocs.warps.commands.WarpsCommand

class MenuWarps : JavaPlugin() {

  companion object {
    lateinit var INSTANCE: MenuWarps

    val warpManager = WarpManager()
    val commandManager = CommandManager()
  }

  init {
    INSTANCE = this
  }

  override fun onEnable() {
    if (!dataFolder.exists()) {
      dataFolder.mkdirs()
    }

    /* add the commands */
    commandManager.add(WarpsCommand)

    /* load the managers */
    warpManager.load(this)
    commandManager.load(this)
  }

  override fun onDisable() {
  }

}
