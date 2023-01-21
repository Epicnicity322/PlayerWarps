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

package com.epicnicity322.playerwarps.command;

import com.epicnicity322.epicpluginlib.bukkit.lang.MessageSender;
import com.epicnicity322.playerwarps.PlayerWarps;
import com.epicnicity322.playerwarps.Warp;
import com.epicnicity322.playerwarps.config.Configurations;
import com.epicnicity322.playerwarps.listener.PlayerMove;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerWarpCommand implements CommandExecutor {
    private final @NotNull PlayerMove playerMove;

    public PlayerWarpCommand(@NotNull PlayerMove playerMove) {
        this.playerMove = playerMove;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessageSender lang = PlayerWarps.lang();
        if (!(sender instanceof Player player)) {
            lang.send(sender, lang.get("General.Not A Player"));
            return true;
        }
        if (args.length != 1) {
            lang.send(sender, lang.get("General.Invalid Arguments").replace("<label>", label).replace("<args>", "<" + lang.get("General.Player") + ">"));
            return true;
        }

        boolean isOwner = player.getName().equalsIgnoreCase(args[0]);
        Warp warp = PlayerWarps.playerWarp(args[0]);
        if (warp == null) {
            lang.send(sender, lang.get("Not Found").replace("<value>", args[0]));
            return true;
        }

        long delay = Configurations.CONFIG.getConfiguration().getNumber("Teleport Delay").orElse(60).longValue();

        if (delay == 0 || player.hasPermission("playerwarps.delay.bypass")) {
            player.teleport(warp.location(), PlayerTeleportEvent.TeleportCause.COMMAND);
            lang.send(sender, lang.get("Warp.Teleported").replace("<player>", args[0]));
            if (!isOwner) warp.visited();
        } else {
            lang.send(sender, lang.get("Warp.Teleporting").replace("<player>", args[0]).replace("<delay>", Integer.toString((int) Math.floor(delay / 20.0))));
            playerMove.scheduleTeleport(player.getUniqueId(), () -> {
                player.teleport(warp.location(), PlayerTeleportEvent.TeleportCause.COMMAND);
                lang.send(sender, lang.get("Warp.Teleported").replace("<player>", args[0]));
                if (!isOwner) warp.visited();
            }, delay);
        }

        return true;
    }
}
