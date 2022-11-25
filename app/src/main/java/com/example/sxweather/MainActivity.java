package com.example.sxweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
//设计流程
//1.
//2.
//3.手动刷新天气和切换城市（在第二阶段，我们会发现进入一个城市的天气后，查找其他的城市天气，必须退出程序）---------------------------------------------------------------------------------------------
    //(1).手动刷新天气
    //(2).切换城市----------------------------------------------------------------------->
        /*对于搜索全球城市数据，我们特意在前面选择了Fragment来实现，所以我们只要在天气界面的布局引入这个Fragment，我们就可以解决无法搜索城市的问题
        * 这个时候滑动菜单的功能对于我们解决问题就是非常方便的，将Fragment放入滑动菜单中实在时再合适不过了，正常情况下，它不占据主界面的任何控件，想要切换城市的时候，只要通过滑动的方式将菜单显示出来就可以了
        * */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}