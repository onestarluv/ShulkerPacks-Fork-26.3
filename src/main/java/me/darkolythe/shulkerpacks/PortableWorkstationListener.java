package me.darkolythe.shulkerpacks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;

/** 아이템을 소모하지 않고 휴대용 작업대를 엽니다. 모루는 작업 성공 시 손상됩니다. */
public final class PortableWorkstationListener implements Listener {
    private final ShulkerPacks plugin;
    private final Map<UUID, AnvilSession> anvils = new HashMap<>();

    public PortableWorkstationListener(ShulkerPacks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClickAir(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR || event.getHand() != EquipmentSlot.HAND
                || event.useItemInHand() == Event.Result.DENY || workstation(event.getItem()) == null) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        open(player, player.getInventory().getHeldItemSlot());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        AnvilSession session = session(event.getView(), player);
        if (session != null) {
            // 사용 중인 모루를 옮기거나 입력 재료로 소비하여 파손을 회피할 수 없게 합니다.
            if (session.pending || !sourceMatches(player, session)
                    || (event.getClickedInventory() == player.getInventory() && event.getSlot() == session.slot)
                    || (event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() == session.slot)
                    || event.getClick() == ClickType.SWAP_OFFHAND
                    || event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                event.setCancelled(true);
            }
            return;
        }
        if ((event.getClick() != ClickType.RIGHT && event.getClick() != ClickType.SHIFT_RIGHT)
                || event.getClickedInventory() != player.getInventory()
                || event.getSlotType() != InventoryType.SlotType.CONTAINER
                || workstation(event.getCurrentItem()) == null
                || !empty(event.getCursor())) return;
        ItemStack clicked = event.getCurrentItem().clone();
        int slot = event.getSlot();
        event.setCancelled(true);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && player.getOpenInventory().getTopInventory() == event.getView().getTopInventory()
                    && clicked.equals(player.getInventory().getItem(slot)) && empty(player.getItemOnCursor())) {
                open(player, slot);
            }
        });
    }

    private PortableWorkstation workstation(ItemStack item) {
        if (empty(item)) return null;
        PortableWorkstation result = PortableWorkstation.from(item.getType());
        return result != null && result.enabled(plugin.settings) ? result : null;
    }

    private void open(Player player, int slot) {
        ItemStack source = player.getInventory().getItem(slot);
        PortableWorkstation type = workstation(source);
        if (type == null) return;
        if (type.isAnvil() && source.getAmount() != 1) {
            kr.gomsv.kokonutCore.api.util.MessageUtil.send(player, plugin.prefix + "모루를 한 개로 나누어 사용해주세요.");
            return;
        }
        if (type == PortableWorkstation.ENDER_CHEST) {
            player.openInventory(player.getEnderChest());
            return;
        }
        InventoryView view = type.createView(player);
        ItemStack original = source.clone();
        player.openInventory(view);
        if (type.isAnvil() && player.getOpenInventory().getTopInventory() == view.getTopInventory()) {
            anvils.put(player.getUniqueId(), new AnvilSession(slot, original, view.getTopInventory()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnvilResult(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || event.getRawSlot() != 2
                || event.getAction() == InventoryAction.NOTHING || empty(event.getCurrentItem())) return;
        Player player = (Player) event.getWhoClicked();
        AnvilSession session = session(event.getView(), player);
        if (session == null || !(event.getView() instanceof AnvilView) || session.pending) return;
        session.pending = true;
        session.use = new AnvilUse(player.getGameMode() == GameMode.CREATIVE,
                player.getLevel(), ((AnvilView) event.getView()).getRepairCost());
        plugin.getServer().getScheduler().runTask(plugin, () -> finishOperation(player, session));
    }

    private void finishOperation(Player player, AnvilSession session) {
        if (!session.pending) return;
        session.pending = false;
        // 성공한 작업에서는 첫 입력 아이템과 경험치가 소모됩니다.
        if (!session.use.completed(player.getLevel(), empty(session.inventory.getItem(0)))
                || !sourceMatches(player, session)) return;
        if (ThreadLocalRandom.current().nextDouble() >= plugin.settings.anvilDamageChance) return;
        PortableWorkstation current = PortableWorkstation.from(session.source.getType());
        Material next = current.nextDamageStage();
        if (next == Material.AIR) {
            player.getInventory().setItem(session.slot, null);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
            kr.gomsv.kokonutCore.api.util.MessageUtil.send(player, plugin.prefix + "사용하던 모루가 파손되었습니다.");
            if (anvils.get(player.getUniqueId()) == session
                    && player.getOpenInventory().getTopInventory() == session.inventory) player.closeInventory();
        } else {
            ItemStack damaged = session.source.clone();
            damaged.setType(next);
            player.getInventory().setItem(session.slot, damaged);
            session.source = damaged.clone();
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
            kr.gomsv.kokonutCore.api.util.MessageUtil.send(player, plugin.prefix + "사용하던 모루가 한 단계 손상되었습니다.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        AnvilSession session = session(event.getView(), player);
        if (session != null) {
            // 창이 닫혀 입력 아이템이 반환되기 전에 작업 성공 여부를 확인합니다.
            anvils.remove(player.getUniqueId());
            finishOperation(player, session);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        AnvilSession session = session(event.getView(), player);
        if (session == null) return;
        for (int raw : event.getRawSlots()) {
            if (event.getView().getInventory(raw) == player.getInventory()
                    && event.getView().convertSlot(raw) == session.slot) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (anvils.containsKey(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (anvils.containsKey(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    private AnvilSession session(InventoryView view, Player player) {
        AnvilSession session = anvils.get(player.getUniqueId());
        return session != null && session.inventory == view.getTopInventory() ? session : null;
    }

    private boolean sourceMatches(Player player, AnvilSession session) {
        return session.source.equals(player.getInventory().getItem(session.slot));
    }

    private boolean empty(ItemStack item) {
        return item == null || item.getType().isAir();
    }

    private static final class AnvilSession {
        final int slot;
        ItemStack source;
        final Inventory inventory;
        boolean pending;
        AnvilUse use;

        AnvilSession(int slot, ItemStack source, Inventory inventory) {
            this.slot = slot;
            this.source = source;
            this.inventory = inventory;
        }
    }
}
