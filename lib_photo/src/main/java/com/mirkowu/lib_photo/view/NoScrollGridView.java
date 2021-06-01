package com.mirkowu.lib_photo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/3/9.
 */
public class NoScrollGridView extends GridView {

    public NoScrollGridView(Context context) {
        super(context);
    }

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //不出现滚动条
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

//    public class GridImageAdapter extends BaseAdapter {
//        private Context context;
//        private List<String> data = new ArrayList<>();
//
//        public GridImageAdapter(Context context) {
//            this.context = context;
//        }
//
//        public void setData(List<String> list) {
//            if (list == null) list = new ArrayList<>();
//            data = list;
//            notifyDataSetChanged();
//        }
//
//        public List<String> getData() {
//            return data;
//        }
//
//        public void addData(List<String> list) {
//            if (list == null) list = new ArrayList<>();
//            data.addAll(list);
//            notifyDataSetChanged();
//        }
//
//
//        @Override
//        public int getCount() {
//            return Math.min(data.size() + 1, Constants.PICKNUM);
//        }
//
//        @Override
//        public String getItem(int position) {
//            return data.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return position == data.size() && data.size() < 9 ? 0 : 1;
//        }
//
//        @Override
//        public int getViewTypeCount() {
//            return 2;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder vh = null;
//            if (convertView != null) {
//                vh = (ViewHolder) convertView.getTag();
//            } else {
//                vh = new ViewHolder(context);
//            }
//
//            ImageView imageView = vh.image;
//
//            if (getItemViewType(position) == 0) {//// 最后一项显示一个＋按钮
//                imageView.setImageResource(R.mipmap.addimage);
//            } else {
//                ImageEngine.load(imageView, data.get(position));
//            }
//            return vh.getConvertView();
//        }
//
//        class ViewHolder {
//            ImageView image;
//
//            public ViewHolder(Context context) {
//                this.convertView = View.inflate(context, layoutResId, null);
//                this.convertView.setTag(this);
//                super(context, R.layout.item_grid_img);
//                image = $(R.id.image);
//            }
//        }
//    }
}
