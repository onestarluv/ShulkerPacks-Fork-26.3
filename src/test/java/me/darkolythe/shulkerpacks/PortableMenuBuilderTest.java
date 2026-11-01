package me.darkolythe.shulkerpacks;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PortableMenuBuilderTest {
    @Test
    @SuppressWarnings("unchecked")
    void usesPaperComponentTitleForEveryPortableMenu() {
        for (String title : List.of("제작대", "돌 절단기", "베틀", "숫돌", "모루")) {
            List<String> calls = new ArrayList<>();
            LocationInventoryViewBuilder<InventoryView> builder =
                    (LocationInventoryViewBuilder<InventoryView>) Proxy.newProxyInstance(
                            getClass().getClassLoader(), new Class<?>[]{LocationInventoryViewBuilder.class},
                            (proxy, method, args) -> {
                                calls.add(method.getName());
                                switch (method.getName()) {
                                    case "checkReachable":
                                        assertEquals(false, args[0]);
                                        return proxy;
                                    case "title":
                                        assertInstanceOf(TextComponent.class, args[0]);
                                        assertEquals(title, ((TextComponent) args[0]).content());
                                        return proxy;
                                    case "build": return null;
                                    default: throw new AssertionError(method);
                                }
                            });
            PortableWorkstation.buildView(builder, null, title);
            assertEquals(List.of("checkReachable", "title", "build"), calls);
        }
    }
}
