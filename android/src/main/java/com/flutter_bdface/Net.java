package com.flutter_bdface;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LL on 2018/6/21/021.
 */

public class Net {

    private static RxJava2CallAdapterFactory callAdapter;
    private static GsonConverterFactory converterFactory;
    private static OkHttpClient httpClient;
    private static HttpLoggingInterceptor httpLoggingInterceptor;

    private static UpLoadImageService upLoadImageService;    //上传图片服务

    /**
     * 日志拦截器
     */
    private  static Interceptor mLoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            Response response = chain.proceed(request);
            return response;
        }
    };

    static {
        converterFactory = GsonConverterFactory.create();
        callAdapter = RxJava2CallAdapterFactory.create();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        if (BuildConfig.DEBUG) {
//            httpLoggingInterceptor = new HttpLoggingInterceptor();
//            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.addInterceptor(httpLoggingInterceptor);
//        }
        httpClient = builder.connectTimeout(20, TimeUnit.SECONDS).
                readTimeout(20, TimeUnit.SECONDS).
                writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new AddCookiesInterceptor())
                .addInterceptor(mLoggingInterceptor)
                .build();
    }


    /**
     * 上传图片接口
     */
    public static UpLoadImageService getUpLoadImageService() {
        if (upLoadImageService == null) {
            upLoadImageService = getService(UpLoadImageService.class);
        }
        return upLoadImageService;
    }

    /**
     * 创造service
     *
     * @param c
     * @param <T>
     * @return
     */
    public static <T> T getService(Class<T> c) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapter)
                .baseUrl(Constant.BASEURL)
                .client(httpClient)
                .build();
        return retrofit.create(c);
    }

    /**
     * 添加cookies拦截器
     */
    public static class AddCookiesInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            String token = PreferenceUtil.getString(Constant.TOKEN);
            if (StringUtil.isEmpty(token)) {
                token = "1";
            }
            builder.addHeader(Constant.TOKEN, token);
            return chain.proceed(builder.build());
        }
    }

    private static String logForRequest(Request request)
    {
        try
        {
            String url = request.url().toString();
            Headers headers = request.headers();

            StringBuffer buffer = new StringBuffer();
            buffer.append("========request'log=======");
            buffer.append("\n");
            buffer.append("method : " + request.method());
            buffer.append("\n");
            buffer.append("url : " + url);
            buffer.append("\n");

            if (headers != null && headers.size() > 0) {

                buffer.append("headers : \n" + headers.toString());

            }
            RequestBody requestBody = request.body();
            if (requestBody != null)
            {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {

                    buffer.append("\n");
                    buffer.append("requestBody's contentType : " + mediaType.toString());

                    if (isText(mediaType)) {

                        buffer.append("\n");
                        buffer.append("requestBody's content : " + bodyToString(request));

                    } else {

                        buffer.append("\n");
                        buffer.append("requestBody's content : " + " maybe [file part] , too large too print , ignored!");

                    }
                }
            }
            buffer.append("\n");
            buffer.append("========request'log=======end");

            return buffer.toString();

        } catch (Exception e) {}

        return null;
    }

    private static boolean isText(MediaType mediaType)
    {
        if (mediaType.type() != null && mediaType.type().equals("text"))
        {
            return true;
        }
        if (mediaType.subtype() != null)
        {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    private static String bodyToString(final Request request) {
        try
        {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e)
        {
            return "something error when show requestBody.";
        }
    }



}