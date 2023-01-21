/*
 * PlayerWarps - Player warp owning management plugin
 * Copyright (C) 2023  Christiano Rangel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.epicnicity322.playerwarps;

import com.epicnicity322.epicpluginlib.bukkit.lang.MessageSender;
import com.epicnicity322.epicpluginlib.bukkit.logger.Logger;
import com.epicnicity322.epicpluginlib.core.config.ConfigurationHolder;
import com.epicnicity322.epicpluginlib.core.logger.ConsoleLogger;
import com.epicnicity322.epicpluginlib.core.util.PathUtils;
import com.epicnicity322.playerwarps.command.*;
import com.epicnicity322.playerwarps.config.Configurations;
import com.epicnicity322.playerwarps.listener.PlayerJoin;
import com.epicnicity322.playerwarps.listener.PlayerMove;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SuppressWarnings("unchecked")
public final class PlayerWarps extends JavaPlugin {
    private static final @NotNull HashMap<UUID, Warp> playerWarps;
    private static final @NotNull Path playerWarpsData = Configurations.dataFolder().resolve("playerwarps");
    private static final @NotNull HashMap<UUID, Double[]> pendingMail;
    private static final @NotNull Path pendingMailData = Configurations.dataFolder().resolve("pendingmail");
    private static final @NotNull MessageSender lang = new MessageSender(() -> Configurations.CONFIG.getConfiguration().getString("Locale").orElse("EN-US"), Configurations.LANGUAGE_EN_US.getDefaultConfiguration());
    private static final @NotNull Logger logger = new Logger("&1[&9PlayerWarps&1]&7 ");
    private static @Nullable Economy econ = null;
    private static boolean success = true;

    static {
        lang.addLanguage("EN-US", Configurations.LANGUAGE_EN_US);
        lang.addLanguage("PT-BR", Configurations.LANGUAGE_PT_BR);

        HashMap<UUID, Warp> playerWarps2 = null;
        if (Files.exists(playerWarpsData)) {
            logger.log("Loading player warps...");
            if (verifyPlayerWarpsPath()) {
                try (FileInputStream fin = new FileInputStream(playerWarpsData.toFile()); ObjectInputStream in = new ObjectInputStream(fin)) {
                    playerWarps2 = (HashMap<UUID, Warp>) in.readObject();
                    logger.log(playerWarps2.size() + " player warp" + (playerWarps2.size() == 1 ? "" : "s") + " loaded.");
                } catch (Exception e) {
                    logger.log("Something went terribly wrong while loading player warps:", ConsoleLogger.Level.ERROR);
                    e.printStackTrace();
                    try {
                        Path backup = PathUtils.getUniquePath(playerWarpsData.getParent().resolve("BUGGED playerwarps"));
                        Files.move(playerWarpsData, backup);
                        logger.log("Since your playerwarps file failed to load, it was backed up to '" + backup.getFileName() + "' and all previous warps were reset.", ConsoleLogger.Level.WARN);
                    } catch (Exception ignored) {
                        logger.log("Failed to back up the failing playerwarps file, please back it up manually or the plugin might replace it with an empty file.", ConsoleLogger.Level.ERROR);
                    }
                    success = false;
                }
            }
        }
        playerWarps = playerWarps2 == null ? new HashMap<>(4) : playerWarps2;

        HashMap<UUID, Double[]> pendingMail2 = null;
        if (Files.exists(pendingMailData)) {
            logger.log("Loading pending mail...");
            try (FileInputStream fin = new FileInputStream(pendingMailData.toFile()); ObjectInputStream in = new ObjectInputStream(fin)) {
                pendingMail2 = (HashMap<UUID, Double[]>) in.readObject();
                logger.log("Pending mail loaded.");
            } catch (Exception e) {
                logger.log("Something went wrong while loading pending mail:", ConsoleLogger.Level.WARN);
                e.printStackTrace();
                success = false;
            }
        }
        pendingMail = pendingMail2 == null ? new HashMap<>(2) : pendingMail2;
    }

    public PlayerWarps() {
        logger.setLogger(getLogger());
    }

    private static boolean verifyPlayerWarpsPath() {
        if (!Files.isDirectory(playerWarpsData)) return true;
        try {
            Path renamed = PathUtils.getUniquePath(playerWarpsData.getParent().resolve("playerwarps folder"));
            Files.move(playerWarpsData, renamed);
            logger.log("A folder exists in the path playerwarps file should be, the folder was renamed to '" + renamed.getFileName() + "' to avoid errors.", ConsoleLogger.Level.WARN);
        } catch (Exception e) {
            logger.log("A folder exists in the path playerwarps file should be, the plugin was not able to move it to another location. Please move it manually or new player warps will not be saved.", ConsoleLogger.Level.ERROR);
            return false;
        }
        return false;
    }

    public static @NotNull MessageSender lang() {
        return lang;
    }

    /**
     * Gets the warp of a player.
     *
     * @param player The player to get the warp from.
     * @return A warp created by this player, if they have created one.
     */
    public static @Nullable Warp playerWarp(@NotNull Player player) {
        return playerWarp(player.getUniqueId());
    }

    /**
     * Gets the warp of a player.
     *
     * @param player The ID of the player to get the warp from.
     * @return A warp created by this player, if they have created one.
     */
    public static @Nullable Warp playerWarp(@NotNull UUID player) {
        Warp warp = playerWarps.get(player);

        if (warp != null && isInactive(warp, player, ZonedDateTime.now())) {
            playerWarps.remove(player);
            return null;
        }
        return warp;
    }

    /**
     * Gets the warp of a player.
     *
     * @param player The name of the player to get the warp from.
     * @return A warp created by this player, if they have created one.
     */
    public static @Nullable Warp playerWarp(@NotNull String player) {
        for (Map.Entry<String, Warp> entry : playerWarps(Sort.RANDOM).entrySet()) {
            if (entry.getKey().equalsIgnoreCase(player)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Creates a new warp that will be associated with this player.
     * <p>
     * If the warp is not visited for a long time, it might be removed automatically.
     * <p>
     * The player is charged a certain amount for creating the warp.
     *
     * @param player The player that owns the warp.
     * @param warp   The warp to set as this player's.
     * @return The result of the warp creation.
     */
    public static @NotNull Response setPlayerWarp(@NotNull Player player, @NotNull Warp warp) {
        if (econ == null) return Response.ERROR;

        boolean hasPrevious = playerWarps.put(player.getUniqueId(), warp) != null;

        try {
            saveWarps();
        } catch (Exception e) {
            logger.log("Something went wrong while saving warps:", ConsoleLogger.Level.ERROR);
            e.printStackTrace();
            return Response.ERROR;
        }

        EconomyResponse response = econ.withdrawPlayer(player, Configurations.CONFIG.getConfiguration().getNumber("Set Cost").orElse(50000.0).doubleValue());
        if (!response.transactionSuccess()) return Response.NO_MONEY;
        return hasPrevious ? Response.REPLACED : Response.CREATED;
    }

    /**
     * Deletes a player's warp if they have one.
     *
     * @param player The player that owns the warp.
     */
    public static void delPlayerWarp(@NotNull Player player) {
        delPlayerWarp(player.getUniqueId());
    }

    /**
     * Deletes a player's warp if they have one.
     *
     * @param player The ID of the player that owns the warp.
     * @return Whether the player had a warp.
     */
    public static boolean delPlayerWarp(@NotNull UUID player) {
        if (playerWarps.remove(player) != null) {
            try {
                saveWarps();
            } catch (IOException e) {
                logger.log("Failed to save playerwarps upon deletion. The warp of " + player + " might come back the next time the plugin loads.", ConsoleLogger.Level.ERROR);
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * Lists all warps. They key as the owning player's name, value as the {@link Warp} containing the details of the warp.
     *
     * @param sort How the map should be sorted.
     * @return An immutable map of every player created warp.
     */
    public static @NotNull Map<String, Warp> playerWarps(@NotNull Sort sort) {
        ZonedDateTime now = ZonedDateTime.now();

        if (sort == Sort.MOST_VISITED) {
            ArrayList<Map.Entry<UUID, Warp>> entries = new ArrayList<>(playerWarps.entrySet());
            entries.sort((o1, o2) -> Integer.compare(o2.getValue().visitedAmount(), o1.getValue().visitedAmount()));

            LinkedHashMap<String, Warp> sortedMap = new LinkedHashMap<>((int) ((entries.size() / 0.75) + 1));
            entries.forEach(entry -> {
                UUID player = entry.getKey();
                Warp warp = entry.getValue();
                if (isInactive(warp, player, now)) {
                    playerWarps.remove(player);
                    return;
                }
                String name = Bukkit.getOfflinePlayer(player).getName();
                if (name != null) sortedMap.put(name, warp);
            });
            return sortedMap;
        } else {
            HashMap<String, Warp> warps = new HashMap<>((int) ((playerWarps.size() / 0.75) + 1));
            playerWarps.entrySet().removeIf(entry -> {
                UUID player = entry.getKey();
                Warp warp = entry.getValue();
                if (isInactive(warp, player, now)) {
                    return true;
                }
                String name = Bukkit.getOfflinePlayer(player).getName();
                if (name != null) warps.put(name, warp);
                return false;
            });
            return sort == Sort.ALPHABETICAL ? new TreeMap<>(warps) : warps;
        }
    }

    /**
     * @return A map with key as the warp owner's ID and value as the warp.
     */
    public static @NotNull HashMap<UUID, Warp> playerWarps() {
        return new HashMap<>(playerWarps);
    }

    /**
     * Saves all warps to the data folder.
     *
     * @throws IOException If warps could not be saved.
     */
    public static void saveWarps() throws IOException {
        if (!verifyPlayerWarpsPath() && Files.isDirectory(playerWarpsData)) return;
        Files.deleteIfExists(playerWarpsData);
        if (Files.notExists(playerWarpsData.getParent())) {
            Files.createDirectories(playerWarpsData.getParent());
        }
        if (!playerWarps.isEmpty()) {
            try (FileOutputStream fout = new FileOutputStream(playerWarpsData.toFile()); ObjectOutputStream out = new ObjectOutputStream(fout)) {
                out.writeObject(playerWarps);
            }
        }
    }

    /**
     * Whether a warp is inactive enough to be removed. This method does not remove the warp to avoid {@link ConcurrentModificationException},
     * but it will let the owner know if the warp is removed because of inactivity.
     *
     * @param warp  The warp to check for inactivity.
     * @param owner The owner of the warp to warn the removal.
     * @param now   What time is now.
     * @return Whether the warp was not visited for a certain amount of days.
     */
    private static boolean isInactive(@NotNull Warp warp, @NotNull UUID owner, @NotNull ZonedDateTime now) {
        int maxInactiveDays = Configurations.CONFIG.getConfiguration().getNumber("Max Inactive Warp Days").orElse(30).intValue();
        if (ChronoUnit.DAYS.between(warp.lastVisited(), now) < maxInactiveDays) return false;

        Player player = Bukkit.getPlayer(owner);
        Location loc = warp.location();
        if (player == null) {
            pendingMail.put(owner, new Double[]{loc.x(), loc.y(), loc.z()});
        } else {
            lang.send(player, lang.get("Inactive Warp").replace("<x>", Double.toString(loc.x())).replace("<y>", Double.toString(loc.y())).replace("<z>", Double.toString(loc.z())).replace("<inactiveDays>", Integer.toString(maxInactiveDays)));
        }
        return true;
    }

    public static void reload() {
        HashMap<ConfigurationHolder, Exception> errors = Configurations.loader().loadConfigurations();
        errors.forEach((config, error) -> logger.log("Something went wrong while loading " + config.getPath() + ". Using default values.", ConsoleLogger.Level.WARN));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            logger.log("Could not find a Vault economy plugin!", ConsoleLogger.Level.ERROR);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        reload();

        var playerMove = new PlayerMove(this);
        getServer().getPluginManager().registerEvents(playerMove, this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(pendingMail), this);

        PluginCommand delplayerwarp = getCommand("delplayerwarp");
        if (delplayerwarp != null) delplayerwarp.setExecutor(new DelPlayerWarpCommand());
        PluginCommand playerwarp = getCommand("playerwarp");
        if (playerwarp != null) playerwarp.setExecutor(new PlayerWarpCommand(playerMove));
        PluginCommand playerwarpinfo = getCommand("playerwarpinfo");
        if (playerwarpinfo != null) playerwarpinfo.setExecutor(new PlayerWarpInfoCommand());
        PluginCommand playerwarps = getCommand("playerwarps");
        if (playerwarps != null) playerwarps.setExecutor(new PlayerWarpsCommand());
        PluginCommand playerwarpsreload = getCommand("playerwarpsreload");
        if (playerwarpsreload != null) playerwarpsreload.setExecutor(new PlayerWarpsReloadCommand());
        PluginCommand setplayerwarp = getCommand("setplayerwarp");
        if (setplayerwarp != null) setplayerwarp.setExecutor(new SetPlayerWarpCommand());

        if (success) {
            logger.log("PlayerWarps enabled successfully!");
        } else {
            logger.log("Some issues happened while loading PlayerWarps. Please go back in the log to check for errors.", ConsoleLogger.Level.WARN);
        }
    }

    @Override
    public void onDisable() {
        logger.log("Saving player warps...");
        try {
            saveWarps();
            logger.log("Player warps were saved successfully!");
        } catch (IOException e) {
            logger.log("The plugin was unable to save player warps to '" + playerWarpsData + "'.", ConsoleLogger.Level.ERROR);
            e.printStackTrace();
            logger.log("The current player warps are: " + playerWarps + ". They will not exist the next time the plugin loads.", ConsoleLogger.Level.WARN);
        }

        try {
            Files.deleteIfExists(pendingMailData);
            if (!pendingMail.isEmpty()) {
                logger.log("Saving pending mail...");
                try (FileOutputStream fout = new FileOutputStream(pendingMailData.toFile()); ObjectOutputStream out = new ObjectOutputStream(fout)) {
                    out.writeObject(pendingMail);
                    logger.log("Mail saved successfully.");
                }
            }
        } catch (Exception e) {
            logger.log("Unable to save pending mail.", ConsoleLogger.Level.WARN);
            e.printStackTrace();
        }

    }

    public enum Sort {
        /**
         * Sort warps by the player's name.
         */
        ALPHABETICAL,
        /**
         * Sort warps by the most visited ones.
         */
        MOST_VISITED,
        /**
         * Don't sort warps to save performance.
         */
        RANDOM
    }

    public enum Response {
        /**
         * The warp was created successfully.
         */
        CREATED,
        /**
         * Something went wrong while creating this warp.
         */
        ERROR,
        /**
         * The player didn't have enough funds to set the warp.
         */
        NO_MONEY,
        /**
         * The warp was created successfully and the previous warp was replaced.
         */
        REPLACED
    }
}
