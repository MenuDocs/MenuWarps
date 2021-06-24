package org.menudocs.warps

import org.menudocs.warps.util.Command

class CommandManager {

  private val _commands: MutableList<Command> = mutableListOf()

  fun add(vararg commands: Command) {
    _commands.addAll(commands)
  }

  fun load(mod: MenuWarps) {
    for (command in _commands) {
      val pluginCommand = mod.getCommand(command.name)
        ?: continue

      pluginCommand.tabCompleter = command.tabCompleter
      pluginCommand.setExecutor { s, _, l, a -> command.execute(s, l, a.toMutableList()) }
    }
  }

}