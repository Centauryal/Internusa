package com.supersoft.internusa.helper.exoplayer.widget;

import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;

import com.supersoft.internusa.helper.exoplayer.ToroPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Centaury on 19/04/2018.
 */
@SuppressWarnings({ "unused", "UnusedReturnValue" }) //
final class PlayerManager {

    private final Container container;

    PlayerManager(Container container) {
        this.container = container;
    }

    // Make sure each ToroPlayer will present only once in this Manager.
    private final Set<ToroPlayer> players = new ArraySet<>();

    boolean attachPlayer(@NonNull ToroPlayer player) {
        return players.add(player);
    }

    boolean detachPlayer(@NonNull ToroPlayer player) {
        return players.remove(player);
    }

    boolean manages(@NonNull ToroPlayer player) {
        return players.contains(player);
    }

    /**
     * Return a "Copy" of the collection of players this manager is managing.
     *
     * @return a non null collection of Players those a managed.
     */
    @NonNull
    List<ToroPlayer> getPlayers() {
        return new ArrayList<>(this.players);
    }

    void initialize(@NonNull ToroPlayer player) {
        player.initialize(container, container.getPlaybackInfo(player.getPlayerOrder()));
    }

    void play(@NonNull ToroPlayer player) {
        player.play();
    }

    void pause(@NonNull ToroPlayer player) {
        player.pause();
    }

    void release(@NonNull ToroPlayer player) {
        player.release();
    }

    void clear() {
        this.players.clear();
    }
}
