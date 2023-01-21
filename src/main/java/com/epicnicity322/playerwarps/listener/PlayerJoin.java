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
import com.epicnicity322.playerwarps.config.Configurations;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public final class PlayerJoin implements Listener {
    private final @NotNull HashMap<UUID, Double[]> pendingMail;

    public PlayerJoin(@NotNull HashMap<UUID, Double[]> pendingMail) {
        this.pendingMail = pendingMail;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Double[] removed = pendingMail.remove(player.getUniqueId());
        if (removed != null) {
            PlayerWarps.lang().send(player, PlayerWarps.lang().get("Inactive Warp").replace("<x>", Double.toString(removed[0]))
                    .replace("<y>", Double.toString(removed[1]))
                    .replace("<z>", Double.toString(removed[2]))
                    .replace("<inactiveDays>", Integer.toString(Configurations.CONFIG.getConfiguration().getNumber("Max Inactive Warp Days").orElse(30).intValue())));
        }
    }
}
