package com.supersoft.internusa.helper.exoplayer;

/**
 * Created by Centaury on 19/04/2018.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import java.util.LinkedHashMap;

import com.supersoft.internusa.helper.exoplayer.media.PlaybackInfo;
import com.supersoft.internusa.helper.exoplayer.widget.Container;

/**
 * @author eneim (7/5/17).
 *
 *         {@link CacheManager} is a helper interface used by {@link Container} to manage the
 *         {@link PlaybackInfo} of {@link ToroPlayer}s. For each {@link ToroPlayer}, {@link
 *         CacheManager} will ask for a unique key for its {@link PlaybackInfo} cache. {@link
 *         Container} uses a {@link LinkedHashMap} to implement the caching mechanism, so {@link
 *         CacheManager} must provide keys which are uniquely distint by
 *         {@link Object#equals(Object)}.
 */
public interface CacheManager {

    /**
     * Get the unique key for the {@link ToroPlayer} of a specific order. Note that this key must
     * also be managed by {@link RecyclerView.Adapter} so that it prevents the uniqueness at data
     * change events.
     *
     * @param order order of the {@link ToroPlayer}.
     * @return the unique key of the {@link ToroPlayer}.
     */
    @Nullable
    Object getKeyForOrder(int order);

    /**
     * Get the order of a specific key value. Returning a {@code null} order value here will tell
     * {@link Container} to ignore this key's cache order.
     *
     * @param key the key value to lookup.
     * @return the order of the {@link ToroPlayer} whose unique key value is key.
     */
    @Nullable Integer getOrderForKey(@NonNull Object key);

    /**
     * A built-in {@link CacheManager} that use the order as the unique key. Note that this may not
     * survive data changes. Which means that after data change events, the map may need to be
     * updated.
     */
    CacheManager DEFAULT = new CacheManager() {
        @Override public Object getKeyForOrder(int order) {
            return order;
        }

        @Override public Integer getOrderForKey(@NonNull Object key) {
            return key instanceof Integer ? (Integer) key : null;
        }
    };
}
