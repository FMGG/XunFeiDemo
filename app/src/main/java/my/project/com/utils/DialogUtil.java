package my.project.com.utils;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import my.project.com.R;


/**
 * Created by X on 2018/4/10.
 */
public class DialogUtil {

    public static void showPermissionManagerDialog(final Context context, String str) {
       AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("获取" + str + "权限被禁用")
                .setMessage("请在 设置-应用管理-" + context.getString(R.string.app_name) + "-权限管理 (将" + str + "权限打开)")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                })
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        context.startActivity(intent);
                    }
                })
                .setCancelable(false)
                .show();
        Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button button2 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        button.setTextColor(Color.BLACK);
        button2.setTextColor(Color.BLACK);
    }
}
