package me.motor.motormoney;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


public final class MotorMoney extends JavaPlugin {

    private static Economy economy = null;
    private JavaPlugin plugin;



    private File balanceFile;

    private FileConfiguration balanceConfig;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault를 찾을 수 없습니다. 플러그인이 종료됩니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        MoneyManager(this);
        saveDefaultConfig();
        loadBalance();
        //getCommand("돈").setExecutor(new MoneyCommand());
        getLogger().info("Vault 연동이 완료되었습니다.");
        getCommand("돈").setExecutor(new MoneyCommand());
        getCommand("돈").setTabCompleter(new CommandTab());
    }

    @Override
    public void onDisable() {
        saveBalances();
        getLogger().info("플러그인이 비활성화되었습니다.");
    }



    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        economy = new VaultHook();
        Bukkit.getServicesManager().register(Economy.class, economy, this, ServicePriority.Highest);

        return true;
    }

    public static Economy getEconomy() {

        return economy;
    }

    public void MoneyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.balanceFile = new File(plugin.getDataFolder(), "playerdata.yml");
        this.balanceConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(this.balanceFile);
    }

    public void saveBalances() {
        for (String playerId : VaultHook.balances.keySet())
            this.balanceConfig.set(playerId.toString(), VaultHook.balances.get(playerId));
        try {
            this.balanceConfig.save(this.balanceFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBalance() {
        for (String key : this.balanceConfig.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            int balance = this.balanceConfig.getInt(key);
            VaultHook.balances.put(String.valueOf(playerId), Integer.valueOf(balance));
        }
    }

}