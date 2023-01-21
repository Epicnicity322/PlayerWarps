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

package com.epicnicity322.playerwarps.listener;

import com.epicnicity322.playerwarps.PlayerWarps;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public final class PlayerMove implements Listener {
    private static final @NotNull HashMap<UUID, BukkitTask> teleports = new HashMap<>(4);
    private final @NotNull PlayerWarps plugin;

    public PlayerMove(@NotNull PlayerWarps plugin) {
        this.plugin = plugin;
    }

    public void scheduleTeleport(@NotNull UUID player, @NotNull Runnable task, long delay) {
        BukkitTask previous = teleports.put(player, plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            teleports.remove(player);
            task.run();
        }, delay));
        if (previous != null) previous.cancel();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            Player player = event.getPlayer();
            BukkitTask task = teleports.remove(player.getUniqueId());
            if (task != null) {
                task.cancel();
                PlayerWarps.lang().send(player, PlayerWarps.lang().get("Moved"));
            }
        }
    }
}
