package android.slc.ws.core;

import android.util.Log;

/**
 * 日志工具
 */
public class SlcWsLog {
    public final static String TAG = "SlcWebSocket";

    /**
     * 调试日志
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (WsConfigManager.getInstance().getWsConfig().isShowLog()) {
            Log.d(tag, msg);
        }
    }
}
