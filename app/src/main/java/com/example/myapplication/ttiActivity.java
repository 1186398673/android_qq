package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iflytek.sparkchain.core.LLM;
import com.iflytek.sparkchain.core.LLMCallbacks;
import com.iflytek.sparkchain.core.LLMConfig;
import com.iflytek.sparkchain.core.LLMError;
import com.iflytek.sparkchain.core.LLMEvent;
import com.iflytek.sparkchain.core.LLMFactory;
import com.iflytek.sparkchain.core.LLMOutput;
import com.iflytek.sparkchain.core.LLMResult;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;

public class ttiActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AEELog";
    private LLM llm;
    private ImageView imageView;
    private TextView tv_result;
    private Button btn_tti_run_start, btn_tti_arun_start, btn_tti_stop;
    private EditText ed_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tti);

        // 初始化UI组件
        initUI();

        // 配置应用信息
        initSDK();

        // 配置LLM
        setLLMConfig();

        // 设置窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initUI() {
        ed_input = findViewById(R.id.online_llm_tti_input_text);
        imageView = findViewById(R.id.online_llm_tti_output_iv);
        tv_result = findViewById(R.id.online_llm_tti_Notification);
        btn_tti_run_start = findViewById(R.id.online_llm_tti_run_start_btn);
        btn_tti_arun_start = findViewById(R.id.online_llm_tti_arun_start_btn);
        btn_tti_stop = findViewById(R.id.online_llm_tti_stop_btn);

        // 设置按钮点击监听
        btn_tti_run_start.setOnClickListener(this);
        btn_tti_arun_start.setOnClickListener(this);
        btn_tti_stop.setOnClickListener(this);
    }

    private void initSDK() {
        Log.d(TAG, "initSDK");
        // 初始化SDK，Appid等信息在清单中配置
        SparkChainConfig config = SparkChainConfig.builder()
                .appID("c2ad1264")
                .apiKey("03810edd906dc6f81bbed97cb8db5c32")
                .apiSecret("YWU3ZjY1OGVkYzI1ZjFiYTBhNTUzNTVh");
        int ret = SparkChain.getInst().init(this, config);
        String result;
        if (ret == 0) {
            result = "SDK初始化成功,请选择相应的功能点击体验。";
        } else {
            result = "SDK初始化失败,错误码:" + ret;
        }
        Log.d(TAG, result);
        showInfo(result);
    }

    private LLMCallbacks mLLMCallbacksListener = new LLMCallbacks() {
        @Override
        public void onLLMResult(LLMResult result, Object o) {
            byte[] bytes = result.getImage();
            showImage(bytes);
            showInfo("图片生成结束。");
        }

        @Override
        public void onLLMEvent(LLMEvent event, Object o) {
            // 处理事件
        }

        @Override
        public void onLLMError(LLMError error, Object o) {
            int errCode = error.getErrCode();
            String errMsg = error.getErrMsg();
            String sid = error.getSid();
            String errInfo = "出错了，错误码：" + errCode + ",错误信息：" + errMsg;
            showInfo(errInfo);
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.online_llm_tti_arun_start_btn) {
            if (llm != null) {
                clearImage();
                showInfo("图片生成中，请稍后.....");
                tti_arun_start();
            }
        } else if (view.getId() == R.id.online_llm_tti_run_start_btn) {
            if (llm != null) {
                clearImage();
                showInfo("图片生成中，请稍后.....");
                new Thread(() -> {
                    tti_run_start();
                }).start();
            }
        } else if (view.getId() == R.id.online_llm_tti_stop_btn) {
            if (llm != null) {
                tti_stop();
                showInfo("已取消图片生成。");
            }
        }
    }

    private void tti_stop() {
        llm.stop();
    }

    private void tti_arun_start() {
        String content = ed_input.getText().toString();
        Log.d("SparkChain", "content: " + content);
        llm.arun(content);
    }

    private void tti_run_start() {
        String content = ed_input.getText().toString();
        Log.d("SparkChain", "content: " + content);
        LLMOutput syncOutput = llm.run(content);
        if (syncOutput.getErrCode() == 0) {
            byte[] bytes = syncOutput.getImage();
            if (bytes != null) {
                Log.d(TAG, "同步调用：" + bytes.length);
                showImage(bytes);
                showInfo("图片生成结束。");
            } else {
                Log.d(TAG, "同步调用：获取结果失败");
            }
        } else {
            Log.d(TAG, "同步调用：errCode " + syncOutput.getErrCode() + " errMsg:" + syncOutput.getErrMsg());
        }
    }

    private void showImage(byte[] bytes) {
        runOnUiThread(() -> {
            if (bytes != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false));
            }
        });
    }

    private void clearImage() {
        runOnUiThread(() -> {
            imageView.setImageDrawable(null);
        });
    }

    private void showInfo(String text) {
        runOnUiThread(() -> {
            tv_result.setText(text);
        });
    }

    private void setLLMConfig() {
        LLMConfig llmConfig = LLMConfig.builder()
                .maxToken(2048)
                .topK(2);

        llm = LLMFactory.imageGeneration(512, 512, llmConfig);
        llm.registerLLMCallbacks(mLLMCallbacksListener);
    }
}