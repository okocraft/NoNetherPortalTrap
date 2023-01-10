package net.okocraft.nonetherportaltrap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private final TranslationManager translationManager = new TranslationManager(this);

    private final Set<UUID> waitingTeleport = new HashSet<>();

    private BukkitAudiences adventure;

    @Override
    public void onLoad() {
        translationManager.load();
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);

        getServer().getScheduler().runTaskTimer(this, () -> {
            Collection<? extends Player> players = getServer().getOnlinePlayers();
            waitingTeleport.retainAll(players.stream().map(Entity::getUniqueId).collect(Collectors.toSet()));
            players.forEach(player -> {
                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                    return;
                }

                UUID uid = player.getUniqueId();

                Block bottom = player.getLocation().getBlock();
                Block top = bottom.getRelative(BlockFace.UP);
                if (bottom.getType() != Material.NETHER_PORTAL || top.getType() != Material.NETHER_PORTAL) {
                    waitingTeleport.remove(uid);
                    return;
                }

                if (!waitingTeleport.contains(uid)) {
                    waitingTeleport.add(uid);
                    adventure.player(player).sendMessage(Component.translatable("stay-if-trapped"));
                } else {
                    waitingTeleport.remove(uid);
                    adventure.player(player).sendMessage(Component.translatable("teleport-to-spawn-point"));
                    player.teleport(getServer().getWorlds().get(0).getSpawnLocation());
                }
            });
        }, 0L, 10 * 20L);
    }

    @Override
    public void onDisable() {
        translationManager.unload();
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}
