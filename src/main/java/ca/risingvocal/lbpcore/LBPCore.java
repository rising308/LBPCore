package ca.risingvocal.lbpcore;

import ca.risingvocal.lbpcore.commands.LBPCoreCommand;
import ca.risingvocal.lbpcore.commands.SweepstakesCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.util.logging.Logger;

public final class LBPCore extends JavaPlugin {

    private Economy econ;
    private FileConfiguration langConfig;

    Logger logger = getLogger();

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        saveResource("lang/en_US.yml", true);
        saveDefaultConfig();

        loadLang();

        loadCommands();

        if (!setupEconomy()) {
            logger.severe("Failed to load economy.");
            return;
        }
    }

    public void loadLang() {
        File langFile = new File(getDataFolder(), "lang/en_US.yml");
        this.langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public void loadCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new LBPCoreCommand(this).getTree().build());
        });

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new SweepstakesCommand(this).getTree().build());
        });
    }

    public FileConfiguration getLang() {
        return this.langConfig;
    }

    public void reloadPlugin() {
        this.reloadConfig();
        this.loadLang();

        this.logger.info("Reloading!");
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    @Override
    public void onDisable() {
    }
}
