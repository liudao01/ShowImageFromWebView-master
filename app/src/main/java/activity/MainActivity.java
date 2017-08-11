package activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.peng.zhang.activity.R;

import view.ShowImageWebView;

/**
 * description: 加载 WebView 主界面
 * author：pz
 * 时间：2016/10/18 :23:11
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private ShowImageWebView mWebView;
    private String url = "http://a.mp.uc.cn/article.html?uc_param_str=frdnsnpfvecpntnwprdssskt&from=media#!wm_aid=1b221845d3894dce9ebb434ddce862d1!!wm_id=482efebe15ed4922a1f24dc42ab654e6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (ShowImageWebView) findViewById(R.id.web_view);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.setWebViewClient(new WebViewClient() {
            // 网页跳转
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                try {
                    if (url.startsWith("weixin://") //微信
                            || url.startsWith("alipays://") //支付宝
                            || url.startsWith("mailto://") //邮件
                            || url.startsWith("tel://")//电话
                            || url.startsWith("dianping://")//大众点评
                            //其他自定义的scheme
                            || url.startsWith("intent://")//自定义的
                            || url.startsWith("ucweb://")//UC
                            ) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }

                //处理http和https开头的url
                mWebView.loadUrl(url);
                return true;

            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // web 页面加载完成，添加监听图片的点击 js 函数
                mWebView.setImageClickListner();
                //解析 HTML
                mWebView.parseHTML(view);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, "请检查您的网络设置 errorCode", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onReceivedError: errorCode =" + errorCode);
                Log.d(TAG, "onReceivedError: description =" + description);
                Log.d(TAG, "onReceivedError: failingUrl =" + failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 接受所有网站的证书
                super.onReceivedSslError(view, handler, error);
            }
        });

        mWebView.loadUrl(url);
    }

}
