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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Warp implements Serializable {
    @Serial
    private static final long serialVersionUID = 6332888971859539153L;
    private final @NotNull Double @NotNull [] xyz;
    private final @NotNull Float @NotNull [] py;
    private final @NotNull UUID world;
    private @NotNull ZonedDateTime lastVisited;
    private int visitedAmount;

    public Warp(@NotNull Location location) {
        this(location, ZonedDateTime.now(), 0);
    }

    public Warp(@NotNull Location location, @NotNull ZonedDateTime lastVisited, int visitedAmount) {
        this.xyz = new Double[]{location.x(), location.y(), location.z()};
        this.py = new Float[]{location.getPitch(), location.getYaw()};
        this.world = location.getWorld().getUID();
        this.lastVisited = lastVisited;
        this.visitedAmount = visitedAmount;
    }

    /**
     * @return The location of teleport of this warp.
     */
    public @NotNull Location location() {
        return new Location(Bukkit.getWorld(world), xyz[0], xyz[1], xyz[2], py[1], py[0]);
    }

    /**
     * @return The amount of times this warp was visited.
     */
    public int visitedAmount() {
        return visitedAmount;
    }

    /**
     * @return The date this warp was last visited.
     */
    public @NotNull ZonedDateTime lastVisited() {
        return lastVisited;
    }

    /**
     * Marks the warp as visited. Updates the {@link #lastVisited()} date and increases {@link #visitedAmount()}.
     */
    public void visited() {
        lastVisited = ZonedDateTime.now();
        visitedAmount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Warp warp)) return false;
        return visitedAmount == warp.visitedAmount && Arrays.equals(xyz, warp.xyz) && Arrays.equals(py, warp.py) && world.equals(warp.world) && lastVisited.equals(warp.lastVisited);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(world, lastVisited, visitedAmount);
        result = 31 * result + Arrays.hashCode(xyz);
        result = 31 * result + Arrays.hashCode(py);
        return result;
    }

    @Override
    public String toString() {
        return "Warp{" +
                "xyz=" + Arrays.toString(xyz) +
                ", py=" + Arrays.toString(py) +
                ", world=" + world +
                ", lastVisited=" + lastVisited +
                ", visitedAmount=" + visitedAmount +
                '}';
    }
}
