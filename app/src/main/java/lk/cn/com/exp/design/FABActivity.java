package lk.cn.com.exp.design;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import lk.cn.com.exp.R;

/**
 * 浮动按钮
 */
public class FABActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,
                        // 最多两行文字
                        "最多两行文字最多两行文字最多两行文字最多两行文字最多两行文字最多两行文字最多两行文字", Snackbar.LENGTH_SHORT)
                        // 文本内容右侧的可点击区域，会挤占左侧文本
                        .setAction("可点击区域，会挤占左侧文本", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(FABActivity.this, "点击了可点击区域", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }).show();
            }
        });
    }
}
