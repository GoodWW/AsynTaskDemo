package com.xunman.asyntaskdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 异步加载的 目的是  为了提高用户体验
 * 进行耗时操作  避免堵塞UI线程
 */
public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private static String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.lv);
        //10，打开异步任务
        new newsAsyncTask().execute(URL);
    }

    /**
     * 实现网络的异步访问
     * 请求类型  参数
     * 中间过程   参数
     * 返回类型   参数
     * 1,
     */
    class newsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {

        //2，  后台访问数据
        @Override
        protected List<NewsBean> doInBackground(String... strings) {
            return getJsonData(strings[0]);//3,将请求地址传过去  这里只有一个地址  就用   0
        }

        // 获取到网络数据以后把生成的 newsBeen 设置到   listView  中
        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            NewsAapter adapter = new NewsAapter(MainActivity.this, newsBeen);
            //对吼setAdapter
            listView.setAdapter(adapter);
        }
    }

    /**
     * 4，获得JSON数据
     * 将URL对应的JSON格式数据转化为我们所需要的  newsBean   这个对象
     * @param url
     * @return
     */
    private List<NewsBean> getJsonData(String url) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        //8，读取JSON字符串
        try {
            //9，获取到JSon  格式的字符串
            String jsonString = readString(new URL(url).openStream());//括号里面的可以得到一个InputStream  根据网络链接直接获取数据  简单粗暴
//            Log.d("返回jsonString",jsonString);
            //创建一个JSONObject
            JSONObject jsonObject;
            //创建一个NewsBean 用于封装属性
            NewsBean newsBean;
            //将JSON格式的字符串转化成JSONObject
            try {
                jsonObject = new JSONObject(jsonString);
                //用JSONObject中获取想要的东西  这里是JsonArray
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                //取出jsonArray以后我们就可以通过遍历jsonArray  来取出每一个jsonObject  并取出里面对应的值
                //用一个for循环完成
                for (int i = 0; i < jsonArray.length(); i++) {
                    //jsonArray 中每一个元素都是一个jsonObject
                    jsonObject = jsonArray.getJSONObject(i);
                    //可以将上面得到的之传递到  newsBean  中
                    newsBean = new NewsBean();
                    newsBean.newsIconUrl = jsonObject.getString("picSmall");
                    newsBean.newsTitle = jsonObject.getString("name");
                    newsBean.newsContent = jsonObject.getString("description");
                    //属性设置玩以后，就把  newsBean 设置到  listView中去
                    newsBeanList.add(newsBean);
                    //当循环全部结束以后  我们就将所有的数据塞到   newsBeanList  里面   最后将他返回
                    //就完成了整个JSON格式数据的转化工作  有了这样一个  newsBeanList  对象  我们就能在Adapter中区域使用数据
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Toast.makeText(this, "URL有问题", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return newsBeanList;//成功得到返回的数据以后   我们就要在设置一个Adapter  监管数据设置上去
    }

    /**
     * 5, 创建一个方法   通过URL   inputStream  所返回的字符串信息
     * 从 inputStream  中获取 我们所需要的信息    也就是网页所返回的数据
     * 这里与读取网页信息  所使用的方法是一致的
     */
    private String readString(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            //6,我们需要一行一行的去读
            String line = "";
            isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            //7,通过while循环读取BuferReader里面的数据
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "InputStreamReader有问题", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "BufferedReader有问题", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return result;
    }
}
