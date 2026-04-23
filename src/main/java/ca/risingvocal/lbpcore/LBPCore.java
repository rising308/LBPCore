package ca.risingvocal.lbpcore;

import ca.risingvocal.lbpcore.commands.LBPCoreCommand;
import ca.risingvocal.lbpcore.commands.ParticleCommand;
import ca.risingvocal.lbpcore.commands.SweepstakesCommand;
import ca.risingvocal.lbpcore.util.ParticleSpawn;
import ca.risingvocal.lbpcore.util.ParticleData;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public final class LBPCore extends JavaPlugin {

    private Economy econ;
    private FileConfiguration langConfig;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<UUID, ParticleData> playerCache = new HashMap<>();
    private File dataFile;

    private ParticleSpawn particleSpawn;

    Logger logger = getLogger();

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        saveResource("lang/en_US.yml", true);
        saveDefaultConfig();

        this.dataFile = new File(getDataFolder(), "player_data.json");
        loadData();

        loadLang();

        loadCommands();

        this.particleSpawn = new ParticleSpawn(this);

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                ParticleData data = playerCache.get(player.getUniqueId());

                if (data != null && data.getName() != "NONE") {
                    particleSpawn.particleTickEvent(player, data);
                }
            }
        }, 0L, 1L);

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (!playerCache.isEmpty()) {
                saveData();
                getLogger().info("Autosaved " + playerCache.size() + " player profiles to JSON.");
            }
        }, 6000L, 6000L);

        if (!setupEconomy()) {
            logger.severe("Failed to load economy.");
        }
    }

    public void saveData() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(playerCache, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        if (!dataFile.exists()) return;

        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<HashMap<UUID, ParticleData>>(){}.getType();
            playerCache = gson.fromJson(reader, type);

            if (playerCache == null) playerCache = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID, ParticleData> getPlayerCache() {
        return playerCache;
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

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new ParticleCommand(this).getTree().build());
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
        saveData();
    }
}
