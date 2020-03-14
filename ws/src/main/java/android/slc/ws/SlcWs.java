package android.slc.ws;

import android.content.Context;
import android.slc.ws.core.WsConfigManager;

import android.slc.ws.core.WsConfig;

public class SlcWs {
    /**
     * 唯一实例
     */
    private static SlcWs slcWs = new SlcWs();
    /**
     * application环境
     */
    private Context context;


    private SlcWs() {
        WsConfigManager.getInstance().setWsConfig(new WsConfig.Builder().build());
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static SlcWs getInstance() {
        return slcWs;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init( Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 获取application环境
     *
     * @return
     */
    public Context getApp() {
        return this.context;
    }

    /**
     * 获取配置文件
     *
     * @param wsConfig
     */
    public void setConfig( WsConfig wsConfig) {
        WsConfigManager.getInstance().setWsConfig(wsConfig);
    }


}
