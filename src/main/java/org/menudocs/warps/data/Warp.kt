package org.menudocs.warps.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.menudocs.warps.MenuWarps
import org.menudocs.warps.util.normalized
import java.util.*

@Serializable
data class Warp(
  val id: String,
  val name: String,
  var acceptance: Acceptance? = null,
  @Contextual
  val location: Location,
  @Contextual
  val creator: UUID,
  val description: String
) {
  @Serializable
  data class Acceptance(@Contextual val by: UUID, val date: Long)

  fun take(player: Player) {
    Bukkit.getScheduler().runTask(MenuWarps.INSTANCE, Runnable {
      player.sendMessage("&aSuccessfully warped to &l\"$name\"&r".normalized)
      player.teleport(location)
    })
  }
}
