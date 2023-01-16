package net.okocraft.nonetherportaltrap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

class PlayerListener implements Listener {

    private final Map<UUID, Long> portalStayExpire = new HashMap<>();
    private final Set<UUID> stayMessageSentPlayers = new HashSet<>();

    private final Main plugin;

    PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onEntityPortalEnter(EntityPortalEnterEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        UUID uid = player.getUniqueId();
        checkRunningMapRemoveTask(uid, 2);

        long time = System.currentTimeMillis();

        if (!portalStayExpire.containsKey(uid)) {
            long waitTime = 20000;
            if (player.getPortalCooldown() == 0) {
                // new portal entry (not teleported from another portal.)
                waitTime += 10000;
            }
            portalStayExpire.put(uid, time + waitTime);
            return;
        }

        long timeLeft = portalStayExpire.get(uid) - time;
        if (timeLeft <= 0) {
            plugin.adventure().player(player).sendMessage(Component.translatable("teleport-to-spawn-point"));
            player.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());

        } else if (timeLeft <= 20000 && !stayMessageSentPlayers.contains(uid)) {
            plugin.adventure().player(player).sendMessage(Component.translatable("stay-if-trapped").args(Component.text(20)));
            stayMessageSentPlayers.add(uid);
        }
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        removeMapEntry(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        removeMapEntry(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        removeMapEntry(event.getPlayer().getUniqueId());
    }

    private void removeMapEntry(UUID target) {
        portalStayExpire.remove(target);
        stayMessageSentPlayers.remove(target);
    }

    private final Map<UUID, Integer> mapRemoveTaskDelays = new HashMap<>();
    private void checkRunningMapRemoveTask(UUID target, int delay) {
        if (!mapRemoveTaskDelays.containsKey(target)) {
            plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
                Integer storedDelay = mapRemoveTaskDelays.get(target);
                if (storedDelay != null && storedDelay >= 1) {
                    mapRemoveTaskDelays.put(target, storedDelay - 1);
                } else {
                    mapRemoveTaskDelays.remove(target);
                    portalStayExpire.remove(target);
                    stayMessageSentPlayers.remove(target);
                    task.cancel();
                }
            }, 0, 1);
        }

        mapRemoveTaskDelays.put(target, delay);
    }
}
