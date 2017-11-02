package lk.cn.com.exp.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lk.cn.com.exp.R;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * https://dev.qq.com/topic/591aa71ae315487c53deeca9
 */
public class RetrofitActivity extends AppCompatActivity {
    private TextView tv;

    public RetrofitActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        ((TextView) findViewById(R.id.title)).setText("Retrofit测试");

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
    /**
     * retrofit
     */
    private static Retrofit retrofit;

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

        // retrofit初始化
        retrofit = new Retrofit.Builder()
                .baseUrl("https://tmallapi.bluemoon.com.cn/") // Api域名（API_URL）

                // 将request中的对象转成requestBody，将responseBody转成对象
                .addConverterFactory(GsonConverterFactory.create())

                // 使接口可以返回rxjava的Observable
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                .client(okHttpClient) // 指定client
                .build();
    }

    static class NetworkInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request requestOrigin = chain.request();

            // 添加统一通用header， 不会覆盖前面的header
            Request.Builder rb = requestOrigin.newBuilder()
                    .addHeader("Accept-Language", Locale.getDefault().toString())
                    .addHeader("Host", HOST)
                    .addHeader("Connection", "Keep-Alive");

            Request request = rb.build();

            Response response = chain.proceed(request);

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

    //////////////////////////// retrofit 发送json的post请求
    // 定义返回数据
    public class ResultGetBannerList implements Serializable {
        public int responseCode = -1;
        public String responseMsg;
        public List<Banner> bannerList;
    }

    public class Banner implements Serializable {
        public Icon displayPic;
        public String webUrl;
    }

    public class Icon implements Serializable {
        public int height;
        public String picUrl;
        public int width;
    }

    // 请求体
    public class RequestParam implements Serializable {
        public String pageType;
    }

    // 接口定义

    /**
     * 定义有关banner的一系列http接口业务封装类
     */
    public interface BannerService {

        @POST("washMall/app/getBannerList")
            // 接口路径
        retrofit2.Call<ResultGetBannerList> getBannerList(
                @QueryMap Map<String, Object> commonParam, // 公共参数，加到?后面的
                @Body RequestParam body // 请求参数
        ); // 接口定义

        @POST("washMall/app/getBannerList")
            // 接口路径，rxjava风格
        Observable<ResultGetBannerList> getBannerList2(
                @QueryMap Map<String, Object> commonParam, // 公共参数，加到?后面的
                @Body RequestParam body // 请求参数
        ); // 接口定义
    }

    // 异步发送post的json请求
    private void sendRequest() {
        // 构造请求参数
        //        Map<String, Object> params = new HashMap<>();
        //        params.put("pageType", "MOONMALL");
        //        String jsonString = new Gson().toJson(params);
        //        RequestBody requestBody = RequestBody.create(JSON, jsonString);

        final RequestParam requestBody = new RequestParam();
        requestBody.pageType = "MOONMALL";

        // 添加url公参
        Map<String, Object> commonParam = new HashMap<>();
        commonParam.put("client", "android");
        commonParam.put("cuid", "a75da689-a198-4488-a656-6a6ec9ce7c95");
        commonParam.put("version", "2.0.0");
        commonParam.put("format", "json");
        commonParam.put("time", "1509506413443");
        commonParam.put("appType", "washMall");
        commonParam.put("lng", "999.0");
        commonParam.put("lat", "999.0");
        commonParam.put("hig", "0.0");
        commonParam.put("sign", "fa224d9ec30cc3b20cbae7532e398668");

        // 代理模式新建一个代理对象
        BannerService bs = retrofit.create(BannerService.class);

        // 结合rxjava使用
        bs.getBannerList2(commonParam, requestBody)
                .subscribeOn(Schedulers.io())  // 在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread()) // 回到主线程去处理请求结果
                .subscribe(new Observer<ResultGetBannerList>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ResultGetBannerList resultGetBannerList) {
                        // 处理正常响应
                        Gson g = new Gson();
                        tv.setText(g.toJson(resultGetBannerList));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            tv.setText(((HttpException) e).code() + "-->" + ((HttpException) e)
                                    .response().errorBody());
                        } else {
                            tv.setText(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });


        // retrofit原生风格的http请求对象
        //        Call call = bs.getBannerList(commonParam, requestBody);
        //
        //        call.enqueue(new retrofit2.Callback() {
        //            @Override
        //            public void onResponse(Call call, retrofit2.Response response) {
        //                // 特别注意，400,401等错误也会在此处回调
        //                final int code = response.code();
        //                if (response.isSuccessful()) {
        //                    final ResultGetBannerList responseStr = (ResultGetBannerList)
        // response.body();
        //                    Log.d("RESPONSE", "(" + code + ")-->" +
        //                            responseStr);
        //                    tv.post(new Runnable() {
        //                        @Override
        //                        public void run() {
        //                            tv.setText(responseStr.bannerList.size() + "");
        //                        }
        //                    });
        //                } else {
        //                    Log.e("RESPONSE", "(" + code + ")");
        //                    tv.post(new Runnable() {
        //                        @Override
        //                        public void run() {
        //                            tv.setText(code + "");
        //                        }
        //                    });
        //                }
        //            }
        //
        //            @Override
        //            public void onFailure(Call call, Throwable t) {
        //                t.printStackTrace();
        //                tv.setText(t.getMessage());
        //            }
        //        });
    }

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
