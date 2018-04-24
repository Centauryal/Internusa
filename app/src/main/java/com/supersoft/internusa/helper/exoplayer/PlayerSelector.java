package com.supersoft.internusa.helper.exoplayer;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static com.supersoft.internusa.helper.exoplayer.ToroUtil.visibleAreaOffset;

import com.supersoft.internusa.helper.exoplayer.widget.Container;

/**
 * Created by Centaury on 19/04/2018.
 */
@SuppressWarnings("unused") //
public interface PlayerSelector {

    String TAG = "ToroLib:Selector";

    /**
     * Select a collection of {@link ToroPlayer}s to start a playback (if there is non-playing) item.
     * Playing item are also selected.
     *
     * @param container current {@link Container} that holds the players.
     * @param items a mutable collection of candidate {@link ToroPlayer}s, which are the players
     * those can start a playback. Items are sorted in order obtained from
     * {@link ToroPlayer#getPlayerOrder()}.
     * @return the collection of {@link ToroPlayer}s to start a playback. An on-going playback can be
     * selected, but it will keep playing.
     */
    @NonNull
    Collection<ToroPlayer> select(@NonNull Container container,
                                  @NonNull List<ToroPlayer> items);

    /**
     * The 'reverse' selector of this selector, which can help to select the reversed collection of
     * that expected by this selector.
     * For example: this selector will select the first playable {@link ToroPlayer} from top, so the
     * 'reverse' selector will select the last playable {@link ToroPlayer} from top.
     *
     * @return The PlayerSelector that has opposite selecting logic. If there is no special one,
     * return "this".
     */
    @NonNull PlayerSelector reverse();

    PlayerSelector DEFAULT = new PlayerSelector() {
        @NonNull @Override public Collection<ToroPlayer> select(@NonNull Container container, //
                                                                @NonNull List<ToroPlayer> items) {
            int count = items.size();
            return count > 0 ? singletonList(items.get(0)) : Collections.<ToroPlayer>emptyList();
        }

        @NonNull @Override public PlayerSelector reverse() {
            return DEFAULT_REVERSE;
        }
    };

    PlayerSelector DEFAULT_REVERSE = new PlayerSelector() {
        @NonNull @Override public Collection<ToroPlayer> select(@NonNull Container container, //
                                                                @NonNull List<ToroPlayer> items) {
            int count = items.size();
            return count > 0 ? singletonList(items.get(count - 1)) : Collections.<ToroPlayer>emptyList();
        }

        @NonNull @Override public PlayerSelector reverse() {
            return DEFAULT;
        }
    };

    @SuppressWarnings("unused") PlayerSelector BY_AREA = new PlayerSelector() {
        @NonNull @Override public Collection<ToroPlayer> select(@NonNull final Container container,
                                                                @NonNull List<ToroPlayer> items) {
            int count = items.size();
            Collections.sort(items, new Comparator<ToroPlayer>() {
                @Override public int compare(ToroPlayer o1, ToroPlayer o2) {
                    return Float.compare(visibleAreaOffset(o1, container), visibleAreaOffset(o2, container));
                }
            });

            return count > 0 ? singletonList(items.get(0)) : Collections.<ToroPlayer>emptyList();
        }

        @NonNull @Override public PlayerSelector reverse() {
            return this;  // FIXME return proper reverse selector.
        }
    };

    @SuppressWarnings("unused") PlayerSelector NONE = new PlayerSelector() {
        @NonNull @Override public Collection<ToroPlayer> select(@NonNull Container container, //
                                                                @NonNull List<ToroPlayer> items) {
            return emptyList();
        }

        @NonNull @Override public PlayerSelector reverse() {
            return this;
        }
    };
}
