package com.mirkowu.lib_widget.stateview;

import android.animation.ValueAnimator;
import android.graphics.Rect;

import com.github.ybq.android.spinkit.animation.SpriteAnimatorBuilder;
import com.github.ybq.android.spinkit.sprite.CircleSprite;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.sprite.SpriteContainer;

/**
 * Created by ybq.
 */
public class LoadingTowDot extends SpriteContainer {

    @Override
    public Sprite[] onCreateChild() {
        return new Sprite[]{
                new Dot(0),
                new Dot(1),
        };
    }

    @Override
    public void onChildCreated(Sprite... sprites) {
        super.onChildCreated(sprites);
//        sprites[1].setAnimationDelay(900);
//        sprites[0].setColor(Color.parseColor("#00ffff"));
//        sprites[1].setColor(Color.parseColor("#00ff00"));
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        bounds = clipSquare(bounds);
        int radius = bounds.width() / 12;
        int top = bounds.centerY() - radius;
        int bottom = bounds.centerY() + radius;

        for (int i = 0; i < getChildCount(); i++) {
            int left = radius;
            if (i != 0) {
                left = bounds.width() - radius * 3;
            }
            getChildAt(i).setDrawBounds(left, top, left + radius * 2, bottom);
        }
    }

    private class Dot extends CircleSprite {
        int index;

        Dot(int index) {
            this.index = index;
        }

        @Override
        public ValueAnimator onCreateAnimation() {
            float fractions[] = new float[]{0f, 0.5f, 1f};
            SpriteAnimatorBuilder builder = new SpriteAnimatorBuilder(this)
                    .duration(1500)
                    .easeInOut(fractions);
            if (index == 0) {
                builder.translateXPercentage(fractions, 0f, 0.70f, 0f);
            } else {
                builder.translateXPercentage(fractions, 0f, -0.70f, 0f);
            }
            return builder.build();
        }
    }
}
