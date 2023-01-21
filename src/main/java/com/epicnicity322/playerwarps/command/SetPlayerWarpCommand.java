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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SetPlayerWarpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessageSender lang = PlayerWarps.lang();
        if (!(sender instanceof Player player)) {
            lang.send(sender, lang.get("General.Not A Player"));
            return true;
        }
        String cost = Configurations.CONFIG.getConfiguration().getString("Set Cost").orElse("50000.0");
        switch (PlayerWarps.setPlayerWarp(player, new Warp(player.getLocation()))) {
            case NO_MONEY -> lang.send(player, lang.get("Set.No Money").replace("<amount>", cost));
            case ERROR -> lang.send(player, lang.get("Set.Error"));
            case REPLACED -> lang.send(player, lang.get("Set.Replaced").replace("<amount>", cost));
            case CREATED -> lang.send(player, lang.get("Set.Created").replace("<amount>", cost));
        }
        return true;
    }
}
