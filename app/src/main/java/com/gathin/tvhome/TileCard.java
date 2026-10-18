package com.gathin.tvhome;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.FrameLayout;

/**
 * Tile card that also reports the framework's key long-press (FLAG_LONG_PRESS on a held D-pad
 * centre / Enter), not just the View timer: some remotes and ROMs deliver the flag without the
 * timer firing. Wired to the same action as the touch long-press.
 */
public class TileCard extends FrameLayout {

    private Runnable longPress;

    public TileCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    void setOnLongPressAction(Runnable action) {
        longPress = action;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (longPress != null) {
                longPress.run();
                return true;
            }
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
