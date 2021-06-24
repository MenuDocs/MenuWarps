package org.menudocs.warps.util

import org.bukkit.ChatColor

val String.normalized
  get() = ChatColor.translateAlternateColorCodes('&', this)
