package net.okocraft.nonetherportaltrap;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private final TranslationManager translationManager = new TranslationManager(this);

    private BukkitAudiences adventure;

    @Override
    public void onLoad() {
        translationManager.load();
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        translationManager.unload();
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("adventure is not initialized yet.");
        }
        return this.adventure;
    }
}
