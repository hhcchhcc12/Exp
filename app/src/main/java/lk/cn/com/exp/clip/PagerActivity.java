package lk.cn.com.exp.clip;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import lk.cn.com.exp.R;

public class PagerActivity extends AppCompatActivity {

    ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        vp = (ViewPager) findViewById(R.id.vp);

        findViewById(R.id.fl).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vp.onTouchEvent(event);
                return false;
            }
        });

        vp.setPageMargin(30);
        vp.setOffscreenPageLimit(3);
        vp.setPageTransformer(false, new PageAnimation());
        vp.setAdapter(new Adapter());
    }

    class Adapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            TextView tv = new TextView(PagerActivity.this);
            tv.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            tv.setText(position + "");
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(30);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(Color.argb(255, 50 * position, 50, 50));

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PagerActivity.this, "点击文本" + position, Toast.LENGTH_SHORT)
                            .show();
                }
            });

            container.addView(tv);
            return tv;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /**
     * 切换动画
     */
    class PageAnimation implements ViewPager.PageTransformer {
        final float SCALE_MAX = 0.75f;
        final float ALPHA_MAX = 0.5f;
        final float MAX_Z = 12;

        @Override
        public void transformPage(View page, float position) {
            float scale = (position < 0)
                    ? ((1 - SCALE_MAX) * position + 1)
                    : ((SCALE_MAX - 1) * position + 1);
            float alpha = (position < 0)
                    ? ((1 - ALPHA_MAX) * position + 1)
                    : ((ALPHA_MAX - 1) * position + 1);
            //为了滑动过程中，page间距不变，这里做了处理
            if (position < 0) {
                ViewCompat.setPivotX(page, page.getWidth());
                ViewCompat.setPivotY(page, page.getHeight() / 2);
            } else {
                ViewCompat.setPivotX(page, 0);
                ViewCompat.setPivotY(page, page.getHeight() / 2);
            }
            ViewCompat.setScaleX(page, scale);
            ViewCompat.setScaleY(page, scale);
            ViewCompat.setAlpha(page, Math.abs(alpha));

            // 阴影
            ViewCompat.setElevation(page, MAX_Z * Math.abs(alpha));
        }
    }

}
