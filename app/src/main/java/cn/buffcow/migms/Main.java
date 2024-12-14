package cn.buffcow.migms;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

/**
 * @author qingyu
 * <p>Create on 2024/12/14 15:04</p>
 */
public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jump();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            finishAndRemoveTask();
            System.exit(0);
            Process.killProcess(Process.myPid());
        }, 100);
    }

    private void jump() {
        var intent = new Intent();
        var cmp = new ComponentName("com.miui.securitycenter", "com.miui.googlebase.ui.GmsCoreSettings");

        intent.setComponent(cmp);
        intent.setAction("miui.intent.action.APP_SETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }
}
