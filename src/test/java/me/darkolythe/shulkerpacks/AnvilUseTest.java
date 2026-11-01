package me.darkolythe.shulkerpacks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnvilUseTest {
    @Test
    void successfulRepairAndRenameCountAsUse() {
        assertTrue(new AnvilUse(false, 30, 5).completed(25, true));
        assertTrue(new AnvilUse(false, 1, 1).completed(0, true));
    }

    @Test
    void fullInventoryOrRejectedClickDoesNotDamageAnvil() {
        assertFalse(new AnvilUse(false, 30, 5).completed(30, false));
        assertFalse(new AnvilUse(false, 30, 5).completed(30, true));
        assertFalse(new AnvilUse(false, 30, 5).completed(25, false));
    }

    @Test
    void insufficientLevelsAndInvalidCostsDoNotCount() {
        assertFalse(new AnvilUse(false, 4, 5).completed(0, true));
        assertFalse(new AnvilUse(false, 10, 0).completed(10, true));
        assertFalse(new AnvilUse(false, 10, -1).completed(10, true));
    }

    @Test
    void creativeUseDoesNotDamageAnvil() {
        assertFalse(new AnvilUse(true, 30, 5).completed(25, true));
        assertFalse(new AnvilUse(true, 30, 5).completed(30, true));
    }
}
