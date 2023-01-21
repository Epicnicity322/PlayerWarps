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

package com.epicnicity322.playerwarps.config;

import com.epicnicity322.epicpluginlib.core.config.ConfigurationHolder;
import com.epicnicity322.epicpluginlib.core.config.ConfigurationLoader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class Configurations {
    private static final @NotNull Path dataFolder = Path.of("plugins", "PlayerWarps");
    public static final @NotNull ConfigurationHolder CONFIG = new ConfigurationHolder(dataFolder.resolve("config.yml"), """
            # The max of entries per page of the command "/pwarps".
            List Max Per Page: 10
                        
            # The language of messages. Available locales: [EN-US, PT-BR]
            Locale: EN-US
                        
            # If a warp is not visited for this amount of days, it will be automatically removed.
            Max Inactive Warp Days: 30
                        
            # The price of setting a warp.
            Set Cost: 50000.0
                        
            # The delay in ticks for teleporting to warps.
            # Players with permission 'playerwarps.delay.bypass' are not affected.
            # If the player moves the teleportation is cancelled.
            # Set to 0 to disable.
            Teleport Delay: 60
            """);

    public static final @NotNull ConfigurationHolder LANGUAGE_EN_US = new ConfigurationHolder(dataFolder.resolve("Language").resolve("Language EN-US.yml"), """
            General:
              Invalid Arguments: '&cIncorrect command syntax! Use: "&7&n/<label> <args>&c".'
              No Permission: '&4You don''t have permission to do this.'
              Not A Player: '&cYou must be a player to use this command.'
              Player: 'player'
              Prefix: '&1[&9PlayerWarps&1] '
                        
            Inactive Warp: '&c&lYour warp set at X: &7&l<x>&c&l Y: &7&l<y>&c&l Z: &7&l<z>&c&l was not visited for more than <inactiveDays> days so it was removed.'
                        
            Info: '&f<name>&7 warp has a total of &f<visits>&7 visit(s) and was last visited &f<lastVisit>&7 day(s) ago.'
                        
            List:
              Header: '&8All player warps (Page &7<page>&8 of &7<total>&8):'
              Sorting:
                Sorting By: '&8Sorting by: '
                Selected Color: '&a&l'
                Alphabetical: 'ALPHABETICAL'
                Separator: '&8 | '
                Non Selected Color: '&7&l'
                Most Visited: "MOST VISITED\\n"
              Entry:
                Singular: "&7- &f<name> visited <visits> time\\n"
                Plural: "&7- &f<name> visited <visits> times\\n"
              Entry Tooltip: '&7Click to teleport'
              More: '&8View more warps with &7/<label> <next>&8.'
              More Tooltip: '&7Click to go to next page'
              Footer: '&8Teleport to a warp with &7/pwarp <name>&8.'
              No Warps: '&cNo one created a warp yet.'
              Page: 'page'
              Page Not Found:
                Singular: '&cPage not found, there is only &4<max>&c page.'
                Plural: '&cPage not found, there are only &4<max>&c pages.'
                        
            Moved: '&cYou moved so the teleportation was cancelled.'
                        
            Not Found: '&cA warp for the player "&7<value>&c" was not found.'
                        
            Del:
              Deleted: '&aWarp &7<warp>&a deleted!'
              No Permission Others: '&cYou don''t have permission to delete other player''s warps!'
              No Warp: '&cYou don''t have a warp.'
                        
            Reload: '&7Configurations reloaded!'
                        
            Set:
              Created: '&aWarp created successfully for &6<amount>$&a.'
              Error: '&cSomething went wrong while creating this warp!'
              No Money: '&cYou need at least &6<amount>$&c to create a warp!'
              Replaced: '&aWarp moved successfully for &6<amount>$&a.'
                        
            Warp:
              Teleported: '&aTeleported to &7<player>''s&a warp.'
              Teleporting: '&aYou will be teleported to &7<player>''s&a warp in <delay> seconds, don''t move!'
            """);
    public static final @NotNull ConfigurationHolder LANGUAGE_PT_BR = new ConfigurationHolder(dataFolder.resolve("Language").resolve("Language EN-US.yml"), """
            General:
              Invalid Arguments: '&cSintaxe de comando incorreta! Use: "&7&n/<label> <args>&c".'
              No Permission: '&4Você não tem permissão para fazer isso.'
              Not A Player: '&cVocê precisa ser um jogador para usar esse comando.'
              Player: 'jogador'
              Prefix: '&1[&9PlayerWarps&1] '
                        
            Inactive Warp: '&c&lSua warp marcada em X: &7&l<x>&c&l Y: &7&l<y>&c&l Z: &7&l<z>&c&l não foi visitada por mais de <inactiveDays> dias, portanto foi removida.'
                        
            Info: '&7A warp &f<name>&7 tem um total de &f<visits>&7 visita(s) e a última visita foi &f<lastVisit>&7 dia(s) atrás.'
                        
            List:
              Header: '&8Todas as warps de jogadores (Página &7<page>&8 de &7<total>&8):'
              Sorting:
                Sorting By: '&8Listando em ordem: '
                Selected Color: '&a&l'
                Alphabetical: 'ALFABÉTICA'
                Separator: '&8 | '
                Non Selected Color: '&7&l'
                Most Visited: "MAIS VISITADA\\n"
              Entry:
                Singular: "&7- &f<name> <visits> visita\\n"
                Plural: "&7- &f<name> <visits> visitas\\n"
              Entry Tooltip: '&7Clique para teleportar'
              More: '&8Veja mais warps com &7/<label> <next>&8.'
              More Tooltip: '&7Clique para ir a próxima página'
              Footer: '&8Teletransporte-se para uma warp com &7/pwarp <nome>&8.'
              No Warps: '&cNinguém criou uma warp ainda.'
              Page: 'página'
              Page Not Found:
                Singular: '&cPágina não encontrada, há somente &4<max>&c página.'
                Plural: '&cPágina não encontrada, há somente &4<max>&c páginas.'
                        
            Moved: '&cVocê se moveu, portanto o teletransporte foi cancelado.'
                        
            Not Found: '&cUma warp para o jogador "&7<value>&c" não foi encontrada.'
                        
            Del:
              Deleted: '&aWarp &7<warp>&a removida!'
              No Permission Others: '&cVocê não tem permissão para apagar warps de outros jogadores!'
              No Warp: '&cVocê não tem uma warp.'
                        
            Reload: '&7Configurações recarregadas!'
                        
            Set:
              Created: '&aWarp criada com sucesso por &6<amount>$&a.'
              Error: '&cAlgo de errado ocorreu ao criar essa warp!'
              No Money: '&cVocê precisa de pelo menos &6<amount>$&c para criar uma warp!'
              Replaced: '&aWarp movida com sucesso por &6<amount>$&a.'
                        
            Warp:
              Teleported: '&aTeletransportado para a warp de &7<player>&a.'
              Teleporting: '&aVocê será teletransportado para a warp de &7<player>&a em <delay> segundos, não se mova!'
            """);
    private static final @NotNull ConfigurationLoader loader = new ConfigurationLoader();

    static {
        loader.registerConfiguration(CONFIG);
        loader.registerConfiguration(LANGUAGE_EN_US);
        loader.registerConfiguration(LANGUAGE_PT_BR);
    }

    private Configurations() {
    }

    public static @NotNull Path dataFolder() {
        return dataFolder;
    }

    public static @NotNull ConfigurationLoader loader() {
        return loader;
    }
}
