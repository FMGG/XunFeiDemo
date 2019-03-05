package my.project.com;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ResourceUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import my.project.com.utils.DialogUtil;
import my.project.com.utils.LogUtil;
import my.project.com.utils.XPermissionUtils;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    @BindView(R.id.bt_noui)
    Button btNoui;
    @BindView(R.id.bt_ui)
    Button btUi;
    @BindView(R.id.bt_uinonetwork)
    Button btUinonetwork;
    @BindView(R.id.tv_voicetotext)
    TextView tvVoicetotext;
    @BindView(R.id.bt_speechSynthesizer)
    Button btSpeechSynthesizer;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.bt_selectperson)
    Button btSelectperson;


    private SpeechRecognizer mIat;
    private RecognizerDialog mIatDialog;
    private SpeechSynthesizer mTts;

    private final String CLOUD = "cloud";
    private final String LOCAL = "local";
    private int MORE = 0x10;
    private String VOICENAME = "xiaoyan";
    private int selectedNumLocal = 0;
    // 本地发音人列表
    private String[] localVoicersEntries;
    private String[] localVoicersValue ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        doMorePermission();
        // 本地发音人名称列表
        localVoicersEntries = getResources().getStringArray(R.array.voicer_local_entries);
        localVoicersValue = getResources().getStringArray(R.array.voicer_local_values);
    }

    /**
     * 获取所有权限
     */
    private void doMorePermission() {
        XPermissionUtils.requestPermissions(this, MORE,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                },
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                    }

                    @Override
                    public void onPermissionDenied(String[] deniedPermissions, boolean alwaysDenied) {
                        StringBuilder sBuilder = new StringBuilder();
                        for (String deniedPermission : deniedPermissions) {
                            if (deniedPermission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                sBuilder.append("存储");
                                sBuilder.append(",");
                            }
                            if (deniedPermission.equals(Manifest.permission.READ_PHONE_STATE)) {
                                sBuilder.append("手机状态");
                                sBuilder.append(",");
                            }
                            if (deniedPermission.equals(Manifest.permission.RECORD_AUDIO)) {
                                sBuilder.append("麦克风");
                                sBuilder.append(",");
                            }
                        }
                        if (sBuilder.length() > 0) {
                            sBuilder.deleteCharAt(sBuilder.length() - 1);
                        }
                        if (true) {
                            Toast.makeText(MainActivity.this, "获取" + sBuilder.toString() + "权限失败", Toast.LENGTH_SHORT).show();
                            DialogUtil.showPermissionManagerDialog(MainActivity.this, sBuilder.toString());
                        }
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        XPermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 无UI语音识别
     */
    public void noUiStartVoice() {
        //初始化识别无UI识别对象
        //使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(MainActivity.this, new InitListener() {
            @Override
            public void onInit(int i) {
                if (i != ErrorCode.SUCCESS) {
                    Toast.makeText(MainActivity.this, "初始化失败，错误码：" + i, Toast.LENGTH_LONG).show();
                }
            }
        });

        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIat.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
        mIat.setParameter(SpeechConstant.SUBJECT, null);
        //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //此处engineType为“cloud”
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
        //设置语音输入语言，zh_cn为简体中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //设置结果返回语言
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        //自动停止录音，范围{0~10000}
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        //开始识别，并设置监听器
        mIat.startListening(new RecognizerListener() {
            @Override
            public void onVolumeChanged(int i, byte[] bytes) {

            }

            @Override
            public void onBeginOfSpeech() {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                analysisResult(recognizerResult.getResultString());
            }

            @Override
            public void onError(SpeechError speechError) {

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }


    public void uiStartVoice(String mEngineType) {
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(MainActivity.this, new InitListener() {
            @Override
            public void onInit(int i) {
                if (i != ErrorCode.SUCCESS) {
                    Toast.makeText(MainActivity.this, "初始化失败，错误码：" + i, Toast.LENGTH_LONG).show();
                }
            }
        });

        if (!TextUtils.isEmpty(mEngineType)) {
            mIatDialog.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
                // 设置本地识别资源
                mIatDialog.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
            }
        }

        //以下为dialog设置听写参数
        mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//语种，这里可以有zh_cn和en_us
        mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//设置口音，这里设置的是汉语普通话 具体支持口音请查看讯飞文档，
        mIatDialog.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");//设置编码类型


        //开始识别并设置监听器
        mIatDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                analysisResult(recognizerResult.getResultString());
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
//显示听写对话框
        mIatDialog.show();
    }

    //解析result
    private void analysisResult(String resultString) {
        Gson gson = new Gson();
        XunFeiResult xunFeiResult = gson.fromJson(resultString, XunFeiResult.class);
        XunFeiResult.WsBean ws = xunFeiResult.getWs().get(0);
        List<XunFeiResult.WsBean.CwBean> cw = ws.getCw();
        for (XunFeiResult.WsBean.CwBean cwBean : cw) {
            LogUtil.e(cwBean.getW());
            tvVoicetotext.setText(cwBean.getW());
        }

        LogUtil.e(resultString);
        Log.i(TAG, "onResult: " + resultString);
    }

    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "iat/common.jet"));
        tempBuffer.append(";");
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "iat/sms_16k.jet"));
        //识别8k资源-使用8k的时候请解开注释
        return tempBuffer.toString();
    }


    @OnClick({R.id.bt_noui, R.id.bt_ui, R.id.bt_uinonetwork, R.id.bt_speechSynthesizer,R.id.bt_selectperson})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_noui:
                setEmptyText();
                noUiStartVoice();
                break;
            case R.id.bt_ui:
                setEmptyText();
                uiStartVoice(CLOUD);
                break;
            case R.id.bt_uinonetwork:
                setEmptyText();
                uiStartVoice(LOCAL);
                break;
            case R.id.bt_speechSynthesizer:
                speechSynthesizer();
                break;
            case R.id.bt_selectperson:
                selectPerson();
                break;
        }
    }

    //选择发音人
    private void selectPerson() {
        new AlertDialog.Builder(this).setTitle("本地合成发音人选项")
                .setSingleChoiceItems(localVoicersEntries, // 单选框有几项,各是什么名字
                        selectedNumLocal, // 默认的选项
                        new DialogInterface.OnClickListener() { // 点击单选框后的处理
                            public void onClick(DialogInterface dialog,
                                                int which) { // 点击了哪一项
                                VOICENAME = localVoicersValue[which];
                                selectedNumLocal = which;
                                dialog.dismiss();
                            }
                        }).show();
    }
    
    public void setEmptyText() {
        tvVoicetotext.setText("");
    }

    /**
     * 离线语音合成
     */
    public void speechSynthesizer() {
        mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this, new InitListener() {
            @Override
            public void onInit(int i) {
                if (i != ErrorCode.SUCCESS) {
                    Toast.makeText(MainActivity.this, "初始化失败，错误码：" + i, Toast.LENGTH_LONG).show();
                }
            }
        });
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, LOCAL);

        if (SpeechConstant.TYPE_LOCAL.equals(LOCAL)) {
            // 需下载使用对应的离线合成SDK
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH, getSpeechSynthesizerResourcePath(VOICENAME));
        }

        mTts.setParameter(SpeechConstant.VOICE_NAME, VOICENAME);
        String strTextToSpeech = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(strTextToSpeech)) {
            strTextToSpeech = "科大讯飞，让世界聆听我们的声音";
        }
        mTts.startSpeaking(strTextToSpeech, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }

    public String getSpeechSynthesizerResourcePath(String name) {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + name + ".jet"));
        return tempBuffer.toString();
    }


    @Override
    protected void onDestroy() {
        mTts.destroy();
        mIat.destroy();
        super.onDestroy();
    }
}
