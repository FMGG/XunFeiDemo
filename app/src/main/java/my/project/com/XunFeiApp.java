package my.project.com;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import my.project.com.utils.LogUtil;

public class XunFeiApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init(true);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56911316");
    }


}
