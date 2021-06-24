package org.menudocs.warps.util

import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

abstract class Command {

  abstract val name: String

  open val subCommands: List<Command> = emptyList()
  open val tabCompleter: TabCompleter? = null

  /**
   * Executes this command.
   */
  abstract fun execute(sender: CommandSender, label: String, args: MutableList<String>): Boolean

  /**
   * Executes a sub-command
   */
  fun executeSubCommand(
    sender: CommandSender,
    args: MutableList<String>,
    default: String? = null,
    noArgs: String? = null,
    elseBlock: (CommandSender, MutableList<String>) -> Boolean = { _, _ -> false }
  ): Boolean {
    if (args.isEmpty()) {
      val subCommand = subCommands.find { it.name == noArgs } ?: return elseBlock(sender, args)

      return subCommand.execute(sender, noArgs ?: subCommand.name, args)
    }

    val subCommand = subCommands.find { it.name == args.first() } ?: subCommands.find { it.name == default }
    ?: return elseBlock(sender, args)

    return subCommand.execute(
      sender,
      subCommand.name,
      (if (subCommand.name == default) args else args.drop(1)).toMutableList()
    )
  }

}

