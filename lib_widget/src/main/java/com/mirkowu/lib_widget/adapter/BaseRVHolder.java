package com.mirkowu.lib_widget.adapter;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.mirkowu.lib_widget.R;

import org.jetbrains.annotations.NotNull;

public class BaseRVHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews = new SparseArray<>();
    private BaseRVAdapter mAdapter;

    public BaseRVHolder(@NonNull @NotNull View itemView) {
        super(itemView);
    }

    public <T extends View> T getView(@IdRes int viewId) {
        View view = (View) this.mViews.get(viewId);
        if (view == null) {
            view = this.itemView.findViewById(viewId);
            this.mViews.put(viewId, view);
        }

        return (T) view;
    }


    public BaseRVHolder setVisible(@IdRes int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

//    public BaseRVHolder setGone(@IdRes int viewId, boolean visible) {
//        View view = this.getView(viewId);
//        view.setVisibility(visible ? View.GONE : View.VISIBLE);
//        return this;
//    }


    public BaseRVHolder setSelected(@IdRes int viewId, boolean selected) {
        View view = getView(viewId);
        view.setSelected(selected);
        return this;
    }

    public BaseRVHolder setText(@IdRes int viewId, CharSequence value) {
        TextView view = (TextView) this.getView(viewId);
        view.setText(value);
        return this;
    }

    public BaseRVHolder setText(@IdRes int viewId, @StringRes int strId) {
        TextView view = (TextView) this.getView(viewId);
        view.setText(strId);
        return this;
    }

    public BaseRVHolder setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
        ImageView view = (ImageView) this.getView(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    public BaseRVHolder setBackgroundColor(@IdRes int viewId, @ColorInt int color) {
        View view = this.getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public BaseRVHolder setBackgroundRes(@IdRes int viewId, @DrawableRes int backgroundRes) {
        View view = this.getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BaseRVHolder setTextColor(@IdRes int viewId, @ColorInt int textColor) {
        TextView view = (TextView) this.getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public BaseRVHolder setImageDrawable(@IdRes int viewId, Drawable drawable) {
        ImageView view = (ImageView) this.getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    public BaseRVHolder setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
        ImageView view = (ImageView) this.getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public BaseRVHolder setAlpha(@IdRes int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.getView(viewId).setAlpha(value);
        } else {
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0L);
            alpha.setFillAfter(true);
            this.getView(viewId).startAnimation(alpha);
        }

        return this;
    }


    public BaseRVHolder linkify(@IdRes int viewId) {
        TextView view = (TextView) this.getView(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    public BaseRVHolder setTypeface(@IdRes int viewId, Typeface typeface) {
        TextView view = (TextView) this.getView(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | 128);
        return this;
    }

    public BaseRVHolder setTypeface(Typeface typeface, int... viewIds) {
        int[] var3 = viewIds;
        int var4 = viewIds.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            int viewId = var3[var5];
            TextView view = (TextView) this.getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | 128);
        }

        return this;
    }

    public BaseRVHolder setProgress(@IdRes int viewId, int progress) {
        ProgressBar view = (ProgressBar) this.getView(viewId);
        view.setProgress(progress);
        return this;
    }

    public BaseRVHolder setProgress(@IdRes int viewId, int progress, int max) {
        ProgressBar view = (ProgressBar) this.getView(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    public BaseRVHolder setMax(@IdRes int viewId, int max) {
        ProgressBar view = (ProgressBar) this.getView(viewId);
        view.setMax(max);
        return this;
    }

    public BaseRVHolder setRating(@IdRes int viewId, float rating) {
        RatingBar view = (RatingBar) this.getView(viewId);
        view.setRating(rating);
        return this;
    }

    public BaseRVHolder setRating(@IdRes int viewId, float rating, int max) {
        RatingBar view = (RatingBar) this.getView(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }

    public BaseRVHolder setTag(@IdRes int viewId, Object tag) {
        View view = this.getView(viewId);
        view.setTag(tag);
        return this;
    }

    public BaseRVHolder setTag(@IdRes int viewId, int key, Object tag) {
        View view = this.getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    public BaseRVHolder setChecked(@IdRes int viewId, boolean checked) {
        View view = this.getView(viewId);
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(checked);
        }
        return this;
    }

    public BaseRVHolder setEnabled(@IdRes int viewId, boolean enable) {
        View view = this.getView(viewId);
        view.setEnabled(enable);
        return this;
    }

    public BaseRVHolder setAdapter(BaseRVAdapter adapter) {
        mAdapter = adapter;
        return this;
    }


    /**
     * Child添加点击事件
     *
     * @param resIds
     * @return
     */
    public BaseRVHolder addOnClickListener(@IdRes int... resIds) {
        if (mAdapter != null && mAdapter.getOnItemChildClickListener() != null) {
            if (resIds != null && resIds.length > 0) {
                for (int id : resIds) {
                    View view = getView(id);
                    if (view != null) {
                        mAdapter.addOnClickListener(this, view);
                    }
                }
            }
        }
        return this;
    }

    public BaseRVHolder addOnClickListener(@IdRes int resId) {
        if (mAdapter != null && mAdapter.getOnItemChildClickListener() != null) {
            View view = getView(resId);
            if (view != null) {
                mAdapter.addOnClickListener(this, view);
            }
        }
        return this;
    }

    public void addOnClickListener(View... views) {
        if (mAdapter != null && mAdapter.getOnItemChildClickListener() != null) {
            if (views != null && views.length > 0) {
                for (View view : views) {
                    mAdapter.addOnClickListener(this, view);
                }
            }
        }
    }

    public void addOnClickListener(View view) {
        if (mAdapter != null && mAdapter.getOnItemChildClickListener() != null) {
            mAdapter.addOnClickListener(this, view);
        }
    }

    /**
     * Child添加点击事件
     *
     * @param resIds
     * @return
     */
    public BaseRVHolder addOnLongClickListener(@IdRes int... resIds) {
        if (mAdapter != null && mAdapter.getOnItemChildLongClickListener() != null) {
            if (resIds != null && resIds.length > 0) {
                for (int id : resIds) {
                    View view = getView(id);
                    if (view != null) {
                        mAdapter.addOnLongClickListener(this, view);
                    }
                }
            }
        }
        return this;
    }

    public BaseRVHolder addOnLongClickListener(@IdRes int resId) {
        if (mAdapter != null && mAdapter.getOnItemChildLongClickListener() != null) {
            View view = getView(resId);
            if (view != null) {
                mAdapter.addOnLongClickListener(this, view);
            }
        }
        return this;
    }


    public void addOnLongClickListener(View... views) {
        if (mAdapter != null && mAdapter.getOnItemChildLongClickListener() != null) {
            if (views != null && views.length > 0) {
                for (View view : views) {
                    mAdapter.addOnLongClickListener(this, view);
                }
            }
        }
    }

    public void addOnLongClickListener(View view) {
        if (mAdapter != null && mAdapter.getOnItemChildLongClickListener() != null) {
            mAdapter.addOnLongClickListener(this, view);
        }
    }
}
