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
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public final class DelPlayerWarpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessageSender lang = PlayerWarps.lang();
        UUID owner = null;
        String ownerName = "";

        if (args.length > 0) {
            if (!sender.getName().equalsIgnoreCase(args[0]) && !sender.hasPermission("playerwarps.delotherwarps")) {
                lang.send(sender, lang.get("Del.No Permission Others"));
                return true;
            }

            for (Map.Entry<UUID, Warp> entry : PlayerWarps.playerWarps().entrySet()) {
                String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                if (name == null) continue;
                if (name.equalsIgnoreCase(args[0])) {
                    owner = entry.getKey();
                    ownerName = name;
                    break;
                }
            }

            if (owner == null) {
                lang.send(sender, lang.get("Not Found").replace("<value>", args[0]));
                return true;
            }
        } else if (!(sender instanceof Player player)) {
            lang.send(sender, lang.get("General.Invalid Arguments").replace("<label>", label).replace("<args>", "<" + lang.get("General.Player") + ">"));
            return true;
        } else {
            owner = player.getUniqueId();
            ownerName = player.getName();
        }

        if (PlayerWarps.delPlayerWarp(owner)) {
            lang.send(sender, lang.get("Del.Deleted").replace("<warp>", ownerName));
        } else {
            lang.send(sender, lang.get("Del.No Warp"));
        }
        return true;
    }
}
