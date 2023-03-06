package me.rowyourboat.limitedlife;

import me.rowyourboat.limitedlife.commands.MainCommandExecutor;
import me.rowyourboat.limitedlife.commands.MainTabCompleter;
import me.rowyourboat.limitedlife.data.SaveHandler;
import me.rowyourboat.limitedlife.listeners.PlayerDeathEvents;
import me.rowyourboat.limitedlife.listeners.PlayerJoinEvents;
import me.rowyourboat.limitedlife.scoreboard.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class LimitedLife extends JavaPlugin {

    public static JavaPlugin plugin;

    public static SaveHandler SaveHandler;
    public static TeamHandler TeamHandler;

    public static boolean globalTimerActive;

    @Override
    public void onEnable() {
        plugin = this;
        globalTimerActive = false;

        SaveHandler = new SaveHandler();
        TeamHandler = new TeamHandler();

        PluginCommand limitedlifeCommand = plugin.getCommand("limitedlife");
        if (limitedlifeCommand != null) {
            limitedlifeCommand.setExecutor(new MainCommandExecutor());
            limitedlifeCommand.setTabCompleter(new MainTabCompleter());
        }

        Bukkit.getPluginManager().registerEvents(new PlayerDeathEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvents(), plugin);

        plugin.saveDefaultConfig();
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[LimitedLife] The plugin has been loaded!"
                + "\nPlease run the command '/lf timer start' to get started, or to resume everyone's timer!"
                + "\nRun '/lf boogeyman roll' to roll the boogeyman!"
                + "\nRun '/lf help' for a list of all commands!"
        );
    }

    @Override
    public void onDisable() {
        globalTimerActive = false;
        SaveHandler.save();
    }

    // Sound.ENTITY_ENDER_DRAGON_DEATH = Chosen as Boogey and Failed Sound
    // BLOCK_NOTE_BLOCK_DIDGERIDO = Not chosen as boogey and Cured Sound
    // Sound.BLOCK_NOTE_BLOCK_CHIME = Countdown Sound

}
