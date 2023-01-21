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
import com.epicnicity322.epicpluginlib.core.util.ObjectUtils;
import com.epicnicity322.playerwarps.PlayerWarps;
import com.epicnicity322.playerwarps.Warp;
import com.epicnicity322.playerwarps.config.Configurations;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PlayerWarpsCommand implements CommandExecutor {
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessageSender lang = PlayerWarps.lang();

        boolean sortAlphabetical = (args.length == 2 && args[1].equals("-a"));
        int page = 1;
        if (args.length > 0) {
            if (args[0].equals("-a")) {
                sortAlphabetical = true;
            } else {
                try {
                    page = Integer.parseInt(args[0]);
                    if (page < 1) page = 1;
                } catch (NumberFormatException ignored) {
                    lang.send(sender, lang.get("General.Invalid Arguments").replace("<label>", label).replace("<args>", "[" + lang.get("List.Page") + "] [-a]"));
                    return true;
                }
            }
        }

        Set<Map.Entry<String, Warp>> warps = PlayerWarps.playerWarps(sortAlphabetical ? PlayerWarps.Sort.ALPHABETICAL : PlayerWarps.Sort.MOST_VISITED).entrySet();
        if (warps.isEmpty()) {
            lang.send(sender, lang.get("List.No Warps"));
            return true;
        }

        int maxPerPage = Configurations.CONFIG.getConfiguration().getNumber("List Max Per Page").orElse(10).intValue();
        if (maxPerPage < 1) maxPerPage = 1;
        HashMap<Integer, ArrayList<Map.Entry<String, Warp>>> list = ObjectUtils.splitIntoPages(warps, maxPerPage);
        int listSize = list.size();
        if (page > listSize) {
            lang.send(sender, lang.get("List.Page Not Found." + (listSize == 1 ? "Singular" : "Plural")).replace("<max>", Integer.toString(listSize)));
            return true;
        }

        lang.send(sender, lang.get("List.Header").replace("<page>", Integer.toString(page)).replace("<total>", Integer.toString(list.size())));

        var sorting = new TextComponent(lang.getColored("List.Sorting.Sorting By"));
        var alphabetical = new TextComponent(lang.getColored("List.Sorting." + (sortAlphabetical ? "" : "Non ") + "Selected Color")
                + lang.getColored("List.Sorting.Alphabetical"));
        var mostVisited = new TextComponent(lang.getColored("List.Sorting." + (sortAlphabetical ? "Non " : "") + "Selected Color")
                + lang.getColored("List.Sorting.Most Visited"));
        if (sortAlphabetical) {
            mostVisited.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " " + page));
        } else {
            alphabetical.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " " + page + " -a"));
        }

        sorting.addExtra(alphabetical);
        sorting.addExtra(lang.getColored("List.Sorting.Separator"));
        sorting.addExtra(mostVisited);
        sender.spigot().sendMessage(sorting);

        ArrayList<Map.Entry<String, Warp>> entries = list.get(page);
        var text = new TextComponent();
        for (Map.Entry<String, Warp> entry : entries) {
            int visitedAmount = entry.getValue().visitedAmount();
            var entryText = new TextComponent(lang.getColored("List.Entry." + (visitedAmount == 1 ? "Singular" : "Plural")).replace("<name>", entry.getKey()).replace("<visits>", Integer.toString(visitedAmount)));
            entryText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pwarp " + entry.getKey()));
            entryText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(lang.getColored("List.Entry Tooltip"))));
            text.addExtra(entryText);
        }
        sender.spigot().sendMessage(text);

        String next = (page + 1) + (sortAlphabetical ? " -a" : "");
        if (page != list.size()) {
            var more = new TextComponent(lang.getColored("List.More").replace("<label>", label).replace("<next>", next));
            more.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " " + next));
            more.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(lang.getColored("List.More Tooltip"))));
            sender.spigot().sendMessage(more);
        }

        var footer = new TextComponent(lang.getColored("List.Footer"));
        footer.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pwarp "));
        sender.spigot().sendMessage(footer);
        return true;
    }
}
