package com.supersoft.internusa.helper.exoplayer;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewParent;

import com.supersoft.internusa.helper.exoplayer.widget.Container;

/**
 * Created by Centaury on 19/04/2018.
 */
public final class ToroUtil {

    private static final String TAG = "ToroLib:Util";

    private ToroUtil() {
        throw new RuntimeException("Meh!");
    }

    public static final String LIB_NAME = "ToroUtil";

    /**
     * Get the ratio in range of 0.0 ~ 1.0 the visible area of a {@link ToroPlayer}'s playerView.
     *
     * @param player the {@link ToroPlayer} need to investigate.
     * @param parent the {@link ViewParent} that holds the {@link ToroPlayer}. If {@code null} or
     * not a {@link Container} then this method must returns 0.0f;
     * @return the value in range of 0.0 ~ 1.0 of the visible area.
     */
    @FloatRange(from = 0.0, to = 1.0) //
    public static float visibleAreaOffset(@NonNull ToroPlayer player, @Nullable ViewParent parent) {
        if (parent == null) return 0.0f;

        View playerView = player.getPlayerView();
        Rect drawRect = new Rect();
        playerView.getDrawingRect(drawRect);
        int drawArea = drawRect.width() * drawRect.height();

        Rect playerRect = new Rect();
        boolean visible = playerView.getGlobalVisibleRect(playerRect, new Point());

        float offset = 0.f;
        if (visible && drawArea > 0) {
            int visibleArea = playerRect.height() * playerRect.width();
            offset = visibleArea / (float) drawArea;
        }
        return offset;
    }
}
