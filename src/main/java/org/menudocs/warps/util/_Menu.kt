package org.menudocs.warps.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.nkomarn.Honeycomb.menu.Menu
import xyz.nkomarn.Honeycomb.menu.MenuButton

class MenuButtonBuilder(val menu: Menu) {
  var slot: Int? = null
  var onClick: MenuButton.GuiButtonCallback = MenuButton.GuiButtonCallback { _, _ -> }

  lateinit var item: ItemStack

  fun build(): MenuButton {
    require(slot != null)
    require(::item.isInitialized)

    return MenuButton(menu, item, slot!!, onClick)
  }
}

fun Menu.add(block: MenuButtonBuilder.() -> Unit) {
  val button = MenuButtonBuilder(this).also(block).build()

  addButton(button)
}

fun Menu.fillMenuDocs(): Menu {
  fill(Material.WHITE_STAINED_GLASS_PANE)
  fillBorderAlternating(Material.BLUE_STAINED_GLASS_PANE, Material.LIGHT_BLUE_STAINED_GLASS_PANE)
  return this
}

fun ConfirmMenu(player: Player, title: String, callback: (Boolean) -> Unit): Menu {
  val menu = Menu(player, title, 27)
    .fillMenuDocs()


  /* confirm button */
  val confirmItem = ItemStack(Material.LIME_STAINED_GLASS_PANE)
  confirmItem.itemMeta?.apply {
    setDisplayName("Confirm")
    confirmItem.itemMeta = this
  }

  menu.add {
    item = confirmItem
    slot = 12
    onClick = MenuButton.GuiButtonCallback { _, _ -> callback.invoke(true) }
  }

  /* deny button */
  val denyItem = ItemStack(Material.RED_STAINED_GLASS_PANE)
  denyItem.itemMeta?.apply {
    setDisplayName("Deny")
    denyItem.itemMeta = this
  }

  menu.add {
    item = denyItem
    slot = 14
    onClick = MenuButton.GuiButtonCallback { _, _ -> callback.invoke(false) }
  }

  return menu
}