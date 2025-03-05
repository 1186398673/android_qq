package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChannelFragment extends Fragment {

    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_channel, container, false);

        // 获取 WebView 的引用
        webView = view.findViewById(R.id.webview_wechat_reading);

        // 配置 WebView 设置
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 启用 JavaScript
        webSettings.setDomStorageEnabled(true); // 启用 DOM 存储
        webSettings.setMediaPlaybackRequiresUserGesture(false); // 自动播放媒体
        webSettings.setLoadWithOverviewMode(true); // 适应屏幕宽度
        webSettings.setUseWideViewPort(true); // 使用广泛的视图端口
        webSettings.setBuiltInZoomControls(true); // 启用缩放控件
        webSettings.setDisplayZoomControls(false); // 隐藏缩放控件

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 显示加载指示器
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 隐藏加载指示器
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // 处理错误，例如显示错误页面
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // 加载微信阅读的网页 URL
        //String wechatReadingUrl = "https://weread.qq.com/"; // 请根据实际情况替换为微信阅读的 URL
        webView.loadUrl("https://weread.qq.com/");

        return view;
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroyView();
    }
}