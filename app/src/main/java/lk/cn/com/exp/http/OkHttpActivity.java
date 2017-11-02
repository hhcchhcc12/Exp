package lk.cn.com.exp.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import lk.cn.com.exp.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * http://blog.csdn.net/lk007007sclk1/article/details/78209375
 */
public class OkHttpActivity extends AppCompatActivity {
    private TextView tv;

    public OkHttpActivity() {
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

        try {
            initOkhttp();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static final String HOST = "tmallapi.bluemoon.com.cn";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /***
     * 用于json请求的client
     */
    private static OkHttpClient okHttpClient;

    public static void initOkhttp() throws IOException, KeyStoreException,
            NoSuchAlgorithmException {

        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)      //设置连接超时
                .readTimeout(60, TimeUnit.SECONDS)         //设置读超时
                .writeTimeout(60, TimeUnit.SECONDS)        //设置写超时
                .retryOnConnectionFailure(true)            //是否自动重连

                // 设置https配置，此处忽略了所有证书
                .sslSocketFactory(createEasySSLContext().getSocketFactory(), new
                        EasyX509TrustManager(null))

                // 验证服务器的证书域名。在https握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
                // 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })

                .addInterceptor(log)         // 日志记录，应用层拦截器，只会调用一次
                .addNetworkInterceptor(new NetworkInterceptor())  // 网络层拦截器，可能在重定向时调用多次

                .followRedirects(true)       // 允许http重定向
                .followSslRedirects(true)    // 允许https的重定向

                //                .pingInterval(30, TimeUnit.SECONDS) // 设置ping检测网络连通性的间隔。默认为0

                .build();
    }

    static class NetworkInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request requestOrigin = chain.request();

            // 添加统一通用header， 不会覆盖前面的header
            Request.Builder rb = requestOrigin.newBuilder()
                    .addHeader("Accept-Language", Locale.getDefault().toString())
                    .addHeader("Host", HOST)
                    .addHeader("Connection", "Keep-Alive");

            Request request = rb.build();

            okhttp3.Response response = chain.proceed(request);

            return response;
        }
    }

    private static SSLContext createEasySSLContext() throws IOException {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            return context;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    // 异步发送post的json请求
    private void sendRequest() {
        RequestBody rb = RequestBody.create(JSON, "{\"pageType\":\"MOONMALL\"}");

        Request request = new Request.Builder()
                .url("https://tmallapi.bluemoon.com.cn/washMall/app/getBannerList?"
                        + "client=android&cuid=a75da689-a198-4488-a656-6a6ec9ce7c95&version=2.0.0"
                        + "&format=json&time=1509506413443&appType=washMall"
                        + "&lng=999.0&lat=999.0&hig=0.0&sign=fa224d9ec30cc3b20cbae7532e398668")
                .header("Content-Type", "application/json")
                .post(rb)
                .tag("OKHTTPTEST")
                .build();

        // 同步执行
        //        try {
        //            Response rp =  okHttpClient.newCall(request).execute();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }

        // 异步执行
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                tv.setText(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 特别注意，400,401等错误也会在此处回调
                final int code = response.code();
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();
                    Log.d("RESPONSE", response.request().tag() + "(" + code + ")-->" +
                            responseStr);
                    tv.post(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(responseStr);
                        }
                    });
                } else {
                    Log.e("RESPONSE", response.request().tag() + "(" + code + ")");
                    tv.post(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(code + "");
                        }
                    });
                }
            }
        });
    }
}
