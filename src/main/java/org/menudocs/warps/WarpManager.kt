package org.menudocs.warps

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bukkit.Location
import org.menudocs.warps.data.Warp
import org.menudocs.warps.data.ksx.json
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class WarpManager {

  private var _warps: MutableList<Warp> = mutableListOf()
  private val _saver = Executors.newSingleThreadScheduledExecutor()
  private val _saving = AtomicBoolean(false)
  lateinit var _file: File

  val warps: List<Warp>
    get() = _warps

  /**
   * Whether it's good to save.
   */
  var save: Boolean = false

  init {
    _saver.scheduleAtFixedRate(::save, 15, 15, TimeUnit.MINUTES)
  }

  fun add(build: WarpCreator.() -> Unit) = add(WarpCreator().apply(build).build())

  /**
   * Adds a warp to the warp list.
   */
  fun add(warp: Warp): Boolean {
    if (warps.any { it.id == warp.id }) {
      return false
    }

    save = true
    _warps.add(warp)

    return true
  }

  fun remove(warp: Warp): Boolean {
    val removed = _warps.remove(warp)
    if (removed) {
      save = true
    }

    return removed
  }

  /**
   * Loads all warps for this server.
   */
  fun load(mod: MenuWarps) {
    _file = mod.dataFolder.resolve("warps.json")
    if (!_file.exists()) {
      _file.createNewFile()
    }

    val text = _file.readText(Charset.defaultCharset())
    if (text.isNotBlank()) {
      _warps.addAll(json.decodeFromString<List<Warp>>(text))
    }
  }

  /**
   * Saves the current warps to the warps.json file.
   */
  fun save(force: Boolean = false) {
    if (!force) {
      if (!save || _saving.getAndSet(true)) {
        return
      }
    }

    val json = json.encodeToString(_warps)
    _file.writeText(json)
    _saving.set(false)
  }

  class WarpCreator {
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var acceptance: Warp.Acceptance? = null

    lateinit var location: Location
    lateinit var creator: UUID

    fun build(): Warp {
      require(!name.isNullOrBlank()) {
        "Name must not be null or blank"
      }

      require(!description.isNullOrBlank()) {
        "Description must not be null or blank"
      }

      require(::location.isInitialized) {
        "The location must not be null."
      }

      require(::creator.isInitialized) {
        "The warp creator must be specified "
      }

      return Warp(
        id = id ?: name!!.lowercase().replace(' ', '-'),
        name = name!!,
        creator = creator,
        acceptance = acceptance,
        location = location,
        description = description!!
      )
    }
  }

}