package android.slc.ws.core;

public class WsConfigManager {
    /**
     * 配置文件
     */
    private WsConfig mWsConfig;

    private WsConfigManager() {
    }

    public static WsConfigManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 静态内部类
     */
    private static class Holder {
        private static final WsConfigManager INSTANCE = new WsConfigManager();
    }

    public void setWsConfig(WsConfig mWsConfig) {
        this.mWsConfig = mWsConfig;
    }

    protected WsConfig getWsConfig() {
        return mWsConfig;
    }
}
