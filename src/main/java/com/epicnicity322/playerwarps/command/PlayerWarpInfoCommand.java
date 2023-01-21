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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public final class PlayerWarpInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessageSender lang = PlayerWarps.lang();
        Warp warp;
        String owner;

        if (args.length == 1) {
            warp = PlayerWarps.playerWarp(args[0]);
            owner = args[0];
        } else if (!(sender instanceof Player)) {
            lang.send(sender, lang.get("General.Invalid Arguments").replace("<label>", label).replace("<args>", "<" + lang.get("General.Player") + ">"));
            return true;
        } else {
            warp = PlayerWarps.playerWarp(sender.getName());
            owner = sender.getName();
        }

        if (warp == null) {
            lang.send(sender, lang.get("Not Found").replace("<value>", owner));
            return true;
        }

        lang.send(sender, lang.get("Info").replace("<name>", owner).replace("<visits>", Integer.toString(warp.visitedAmount()))
                .replace("<lastVisit>", Long.toString(ChronoUnit.DAYS.between(warp.lastVisited(), ZonedDateTime.now()))));
        return true;
    }
}
