package me.darkolythe.shulkerpacks;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.InventoryView;

/** 실제 바닐라 메뉴를 사용하여 조합법, 경험치와 재료 소모를 유지합니다. */
enum PortableWorkstation {
    CRAFTING(Material.CRAFTING_TABLE),
    ENDER_CHEST(Material.ENDER_CHEST),
    STONECUTTER(Material.STONECUTTER),
    LOOM(Material.LOOM),
    GRINDSTONE(Material.GRINDSTONE),
    ANVIL(Material.ANVIL),
    CHIPPED_ANVIL(Material.CHIPPED_ANVIL),
    DAMAGED_ANVIL(Material.DAMAGED_ANVIL);

    final Material material;

    PortableWorkstation(Material material) {
        this.material = material;
    }

    static PortableWorkstation from(Material material) {
        for (PortableWorkstation workstation : values()) {
            if (workstation.material == material) return workstation;
        }
        return null;
    }

    boolean isAnvil() {
        return this == ANVIL || this == CHIPPED_ANVIL || this == DAMAGED_ANVIL;
    }

    boolean enabled(PluginSettings settings) {
        switch (this) {
            case CRAFTING: return settings.portableCrafting;
            case ENDER_CHEST: return settings.portableEnderChest;
            case STONECUTTER: return settings.portableStonecutter;
            case LOOM: return settings.portableLoom;
            case GRINDSTONE: return settings.portableGrindstone;
            default: return settings.portableAnvil;
        }
    }

    InventoryView createView(Player player) {
        switch (this) {
            case CRAFTING: return buildView(MenuType.CRAFTING.builder(), player, "제작대");
            case STONECUTTER: return buildView(MenuType.STONECUTTER.builder(), player, "돌 절단기");
            case LOOM: return buildView(MenuType.LOOM.builder(), player, "베틀");
            case GRINDSTONE: return buildView(MenuType.GRINDSTONE.builder(), player, "숫돌");
            default: return buildView(MenuType.ANVIL.builder(), player, "모루");
        }
    }

    // Paper/Purpur의 메뉴 제목 API는 String이 아닌 Adventure Component를 받습니다.
    static <V extends InventoryView> V buildView(LocationInventoryViewBuilder<V> builder,
                                                Player player, String title) {
        return builder.checkReachable(false).title(Component.text(title)).build(player);
    }

    Material nextDamageStage() {
        switch (this) {
            case ANVIL: return Material.CHIPPED_ANVIL;
            case CHIPPED_ANVIL: return Material.DAMAGED_ANVIL;
            case DAMAGED_ANVIL: return Material.AIR;
            default: throw new IllegalStateException("모루가 아닙니다.");
        }
    }
}
