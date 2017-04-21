package com.xunman.asyntaskdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 项目名： AsynTaskDemo
 * 创建者： xxxxx
 * 创建时间：  2017/4/21 0021 14:33
 * 包名：com.xunman.asyntaskdemo
 * 文件名： ${name}
 * 描述：  专门处理图片的加载
 */

public class ImageLoader {

    /**
     * 保存传过来的 imageView
     */
    private ImageView mImageView;
    /**
     * 保存传过来的  图片地址
     */
    private String mUrl;
    /**
     * 用于缓存如片的   catch  第一个  可以  时url   时String  第二个 value  时保存的对象  是Bitmap
     */
    private LruCache<String, Bitmap> mCache;

    public ImageLoader() {
        /**获取当前系统可用的最大内存*/
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        /**  缓存的大小  */
        int cacheSize = maxMemory / 4;
        /**  对 mCache 进行初始化   以下仅仅是  LruCache  初始化
         * 使用  lruCache   需要写两个方法   一个是保存数据到  LruCache  中
         *     另一个就是  从   lruCache  中拿出数据
         * */
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            /**
             *  此方法用户获取每个存进去的对象的大小  必须重写方法  去记载正确的内存大小
             *    默认返回的元素的个数  在这里是不行的
             * @param key
             * @param value
             * @return
             */
            @Override
            protected int sizeOf(String key, Bitmap value) {
//                return super.sizeOf(key, value);//将每次返回改成  bitmap  的大小
                return value.getByteCount();//返回   bitmap  实际的大小  并保存进去  在每次缓存是调用
            }
        };
    }

    /**
     * 增加到  缓存  保存数据   （图片） 到  LruCache  中
     *  缓存前先判断 是否已经存在
     * @param url    用 url当作key
     * @param bitmap 用bitmap  当作值
     */
    public void addBitmapTocache(String url, Bitmap bitmap) {
     if (getBitmapFromUrl(url)==null){
         mCache.put(url,bitmap);
     }
    }

    /**
     *  *   从  LruCache  中取得  bitmap
     * @param url  用 url当作key
     * @return  cache
     */
    public Bitmap getBitMapFromCache(String url) {
        return mCache.get(url);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    /**
     * 用多线程的方式加载图片
     *
     * @param imageView 那一张imageView 需要显示这张图片
     * @param url       网络地址
     */
    public void showImageByThread(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                //在这里增加图片加载方法
                Bitmap bitmap = getBitmapFromUrl(url);
                Message message = Message.obtain();//通过这种方式创建的   message  可以使用现有的 和已经回收掉的 message  提高  message 的使用效率
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 从一个url  去获取一个bitmap
     *
     * @param urlString 图片地址
     * @return
     */
    public Bitmap getBitmapFromUrl(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showImageLoaderByAsyncTask(ImageView imageView, String url) {
        //从缓存中  去除对应的图片
        Bitmap bitmap = getBitMapFromCache(url);
        //如果缓存中没有图片   那么 去网络加载
        if (bitmap==null){
            new NewsAsyncTask(imageView, url).execute(url);
        }else{
            //有的话直接使用
            mImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 异步加载图片的类
     */
    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private String mUrl;
        private ImageView mImageView;

        /**
         * 用构造方法传递  imageView
         *
         * @param imageView
         */
        public NewsAsyncTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            //从网络获取图片
            Bitmap bitmap  =getBitmapFromUrl(url);
            if (bitmap !=null){
                //下载完毕后将不再缓存的图片加入缓存
                addBitmapTocache(url,bitmap);
            }
            return bitmap;
        }

        /**
         * 将  bitmap  设置给  imageView
         * @param bitmap
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }
}
