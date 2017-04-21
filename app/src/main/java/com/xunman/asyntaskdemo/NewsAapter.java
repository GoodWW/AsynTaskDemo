package com.xunman.asyntaskdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * 项目名： AsynTaskDemo
 * 创建者： xxxxx
 * 创建时间：  2017/4/21 0021 11:52
 * 包名：com.xunman.asyntaskdemo
 * 文件名： ${name}
 * 描述：  新闻内容的adapter
 */

public class NewsAapter extends BaseAdapter {

    private ImageLoader mImageLoader;
    //1,  定义几个我们需要的变量
    /**
     * 需要映射的  数据
     */
    private List<NewsBean> mList = null;
    /**
     * layout 布局  对应我们每个布局
     */
    private LayoutInflater mInflater;

    //2,构造方法
    public NewsAapter(Context context, List<NewsBean> mList) {
        this.mList = mList;
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader();
    }

    //3,直接返回  mList 的长度
    @Override
    public int getCount() {
        return mList.size();
    }

    //4，getItem  返回   mList.get(i)
    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    //5，   getItemId   直接返回 i
    @Override
    public long getItemId(int i) {
        return i;
    }

    //6，  重点   getView   使用  ViewHOLder  方式
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_layout, null);
            //接下来 对 控件 进行实例化
            viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tv_content = (TextView) view.findViewById(R.id.tv_content);
            viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //有了  viewHolder  这一项  我们就能对 ViewHolder  里面的对象  进行赋值
        viewHolder.iv_icon.setImageResource(R.mipmap.ic_launcher);
        String url = mList.get(i).newsIconUrl;
        viewHolder.iv_icon.setTag(url);
//        new ImageLoader().showImageByThread(viewHolder.iv_icon,url);
        mImageLoader.showImageLoaderByAsyncTask(viewHolder.iv_icon,url);
        viewHolder.tv_title.setText(mList.get(i).newsTitle);
        viewHolder.tv_content.setText(mList.get(i).newsContent);
        return view;
    }

    class ViewHolder {
        public TextView tv_title, tv_content;
        public ImageView iv_icon;
    }

}
