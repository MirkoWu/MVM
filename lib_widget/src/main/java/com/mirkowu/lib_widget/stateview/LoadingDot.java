package com.mirkowu.lib_widget.stateview;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.github.ybq.android.spinkit.animation.SpriteAnimatorBuilder;
import com.github.ybq.android.spinkit.sprite.ShapeSprite;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.sprite.SpriteContainer;

/**
 * Created by ybq.
 */
public class LoadingDot extends SpriteContainer {

    @Override
    public Sprite[] onCreateChild() {
        return new Sprite[]{
                new Dot(),
                new Dot(),
                new Dot()
        };
    }

    @Override
    public void onChildCreated(Sprite... sprites) {
        super.onChildCreated(sprites);
        sprites[1].setAnimationDelay(160);
        sprites[2].setAnimationDelay(320);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        bounds = clipSquare(bounds);
        int radius = bounds.width() / 8;
        int top = bounds.centerY() - radius;
        int bottom = bounds.centerY() + radius;

        for (int i = 0; i < getChildCount(); i++) {
            int left = bounds.width() * i / 3
                    + bounds.left;
            getChildAt(i).setDrawBounds(
                    left, top, left + radius * 2, bottom
            );
        }
    }

    private class Dot extends ShapeSprite {

        Dot() {
            setScale(0f);
        }

        @Override
        public ValueAnimator onCreateAnimation() {
            float fractions[] = new float[]{0f, 0.4f, 0.8f, 1f};
            return new SpriteAnimatorBuilder(this).scale(fractions, 0f, 1f, 0f, 0f).
                    duration(1000).
                    easeInOut(fractions)
                    .build();
        }

        @Override
        public void drawShape(Canvas canvas, Paint paint) {
            if (getDrawBounds() != null) {
                int radius = Math.min(getDrawBounds().width(), getDrawBounds().height()) / 2;
                canvas.drawCircle(getDrawBounds().centerX(),
                        getDrawBounds().centerY(),
                        radius, paint);
            }
        }
    }
}
