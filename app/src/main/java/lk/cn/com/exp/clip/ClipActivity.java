package lk.cn.com.exp.clip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import lk.cn.com.exp.R;

public class ClipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);

        findViewById(R.id.iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClipActivity.this, "点击中间图标", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClipActivity.this, "点击文本", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ClipActivity.this, PagerActivity.class));
            }
        });
    }
}
