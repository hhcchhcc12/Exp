package lk.cn.com.exp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import lk.cn.com.exp.clip.ClipActivity;
import lk.cn.com.exp.clip.PagerActivity;
import lk.cn.com.exp.design.MDActivity;
import lk.cn.com.exp.http.OkHttpActivity;
import lk.cn.com.exp.http.RetrofitActivity;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;

    private ArrayList<ActivityItem> list = new ArrayList<>();

    {
        list.add(new ActivityItem("ClipPadding与ClipChildren", ClipActivity.class));
        list.add(new ActivityItem("ViewPager自定义动画", PagerActivity.class));
        list.add(new ActivityItem("OkHttp简易例子", OkHttpActivity.class));
        list.add(new ActivityItem("Retrofit简易例子", RetrofitActivity.class));
        list.add(new ActivityItem("Metal Design例子", MDActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv);

        rv.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        ItemAdapter adapter = new ItemAdapter();
        adapter.bindToRecyclerView(rv);

        adapter.replaceData(list);
    }


}