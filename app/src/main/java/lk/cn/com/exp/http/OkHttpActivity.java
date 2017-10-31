package lk.cn.com.exp.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import lk.cn.com.exp.R;
import okhttp3.OkHttpClient;

public class OkHttpActivity extends AppCompatActivity {
    private TextView tv;

    public OkHttpActivity() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendRequest();
            }
        });
        tv = (TextView) findViewById(R.id.tv);
    }

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)      //设置连接超时
            .readTimeout(60, TimeUnit.SECONDS)         //设置读超时
            .writeTimeout(60, TimeUnit.SECONDS)        //设置写超时
            .retryOnConnectionFailure(true)            //是否自动重连

            // 设置https配置，此处忽略了所有证书
            .sslSocketFactory(createEasySSLContext().getSocketFactory(), new EasyX509TrustManager(null))

            .build();

    private static SSLContext createEasySSLContext() throws IOException {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            return context;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private void sendRequest() {
    }
}
