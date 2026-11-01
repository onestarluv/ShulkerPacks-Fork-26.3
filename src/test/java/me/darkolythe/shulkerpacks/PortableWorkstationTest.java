package me.darkolythe.shulkerpacks;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PortableWorkstationTest {
    @Test
    void allSupportedItemsHaveDistinctMenus() {
        assertEquals(PortableWorkstation.CRAFTING, PortableWorkstation.from(Material.CRAFTING_TABLE));
        assertEquals(PortableWorkstation.ENDER_CHEST, PortableWorkstation.from(Material.ENDER_CHEST));
        assertEquals(PortableWorkstation.STONECUTTER, PortableWorkstation.from(Material.STONECUTTER));
        assertEquals(PortableWorkstation.LOOM, PortableWorkstation.from(Material.LOOM));
        assertEquals(PortableWorkstation.GRINDSTONE, PortableWorkstation.from(Material.GRINDSTONE));
        assertNull(PortableWorkstation.from(Material.CHEST));
        assertNull(PortableWorkstation.from(Material.STONE));
    }

    @Test
    void anvilProgressesFromEachExistingDamageStageToDestruction() {
        assertEquals(Material.CHIPPED_ANVIL, PortableWorkstation.from(Material.ANVIL).nextDamageStage());
        assertEquals(Material.DAMAGED_ANVIL, PortableWorkstation.from(Material.CHIPPED_ANVIL).nextDamageStage());
        assertEquals(Material.AIR, PortableWorkstation.from(Material.DAMAGED_ANVIL).nextDamageStage());
        assertFalse(PortableWorkstation.GRINDSTONE.isAnvil());
    }
}
