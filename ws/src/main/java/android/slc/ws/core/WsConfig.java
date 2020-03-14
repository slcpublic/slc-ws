package android.slc.ws.core;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class WsConfig {
    /**
     * 重连次数
     */
    private int reconnectCount;
    /**
     * 重连时间间隔
     */
    private long reconnectTimeInterval;
    /**
     * 网络状态发生变化后重新连接
     */
    private boolean reconnectWithNetworkChanged;
    /**
     * 客户端
     */
    private OkHttpClient client;
    /**
     * 公共请求头
     */
    private ArrayMap<String, String> publicHeaders;
    /**
     * 是否显示日志的
     */
    private boolean isShowLog;
    /**
     * 基础的监听器
     */
    private List<SlcWebSocket.OnSocketListener> baseSocketListenerList = new ArrayList<>();

    private WsConfig(int reconnectCount, long reconnectTimeInterval, boolean reconnectWithNetworkChanged,
                     OkHttpClient client, ArrayMap<String, String> publicHeaders, boolean isShowLog, List<SlcWebSocket.OnSocketListener> baseSocketListenerList) {
        this.reconnectCount = reconnectCount;
        this.reconnectTimeInterval = reconnectTimeInterval;
        this.reconnectWithNetworkChanged = reconnectWithNetworkChanged;
        this.client = client;
        this.publicHeaders = publicHeaders;
        this.isShowLog = isShowLog;
        this.baseSocketListenerList = baseSocketListenerList;
    }

    protected int getReconnectCount() {
        if (reconnectCount < 0) {
            reconnectCount = SlcWsConstant.DEF_RECONNECT_COUNT;
        }
        return reconnectCount;
    }

    protected void setReconnectCount(int reconnectCount) {
        this.reconnectCount = reconnectCount;
    }

    protected long getReconnectTimeInterval() {
        if (reconnectTimeInterval <= 0) {
            reconnectTimeInterval = SlcWsConstant.DEF_RECONNECT_TIME;
        }
        return reconnectTimeInterval;
    }

    protected void setReconnectTimeInterval(long reconnectTimeInterval) {
        this.reconnectTimeInterval = reconnectTimeInterval;
    }

    protected boolean isReconnectWithNetworkChanged() {
        return reconnectWithNetworkChanged;
    }

    protected void setReconnectWithNetworkChanged(boolean reconnectWithNetworkChanged) {
        this.reconnectWithNetworkChanged = reconnectWithNetworkChanged;
    }

    protected OkHttpClient getClient() {
        return client;
    }

    protected void setClient(OkHttpClient client) {
        this.client = client;
    }

    protected ArrayMap<String, String> getPublicHeaders() {
        return publicHeaders;
    }

    protected void setPublicHeaders(ArrayMap<String, String> publicHeaders) {
        this.publicHeaders = publicHeaders;
    }

    protected void setShowLog(boolean showLog) {
        isShowLog = showLog;
    }

    protected boolean isShowLog() {
        return isShowLog;
    }

    protected void setBaseSocketListenerList(List<SlcWebSocket.OnSocketListener> baseSocketListenerList) {
        this.baseSocketListenerList = baseSocketListenerList;
    }

    protected List<SlcWebSocket.OnSocketListener> getBaseSocketListenerList() {
        return baseSocketListenerList;
    }
    public static Builder newBuilder(){
        return new Builder();
    }
    public static class Builder {
        /**
         * 重连次数
         */
        private int reconnectCount=-1;
        /**
         * 重连时间间隔
         */
        private long reconnectTimeInterval;
        /**
         * 网络状态发生变化后重新连接
         */
        private boolean reconnectWithNetworkChanged = false;
        /**
         * 客户端
         */
        private OkHttpClient client = new OkHttpClient();
        /**
         * 公共请求头
         */
        private ArrayMap<String, String> publicHeaders = new ArrayMap<>();
        /**
         * 是否显示日志的
         */
        private boolean isShowLog;
        /**
         * 基础的监听器
         */
        private List<SlcWebSocket.OnSocketListener> baseSocketListenerList = new ArrayList<>();

        /**
         * 设置连接次数
         *
         * @param reconnectCount
         * @return
         */
        public Builder setReconnectCount(int reconnectCount) {
            this.reconnectCount = reconnectCount;
            return this;
        }

        /**
         * 设置连接时间间隔
         *
         * @param reconnectTimeInterval 单位毫秒
         */
        public Builder setReconnectTimeInterval(long reconnectTimeInterval) {
            this.reconnectTimeInterval = reconnectTimeInterval;
            return this;
        }

        /**
         * 设置网络状态改变后自动重连
         *
         * @param reconnectWithNetworkChanged
         * @return
         */
        @Deprecated
        /**
         * 此处目前调用无效，相关功能未实现
         */
        public Builder setReconnectWithNetworkChanged(boolean reconnectWithNetworkChanged) {
            this.reconnectWithNetworkChanged = reconnectWithNetworkChanged;
            return this;
        }

        /**
         * 设置okhttp客户端
         *
         * @param client
         * @return
         */
        public Builder setClient(OkHttpClient client) {
            this.client = client;
            return this;
        }

        /**
         * 公共请求头
         *
         * @param publicHeaders
         */
        public Builder setPublicHeaders(ArrayMap<String, String> publicHeaders) {
            this.publicHeaders.putAll(publicHeaders);
            return this;
        }

        /**
         * 是否显示日志
         *
         * @param showLog
         */
        public Builder setShowLog(boolean showLog) {
            this.isShowLog = showLog;
            return this;
        }

        public Builder addOnSocketListener(SlcWebSocket.OnSocketListener onSocketListener) {
            if (!baseSocketListenerList.contains(onSocketListener)) {
                baseSocketListenerList.add(onSocketListener);
            }
            return this;
        }

        /**
         * 构建
         *
         * @return
         */
        public WsConfig build() {
            return new WsConfig(reconnectCount, reconnectTimeInterval, reconnectWithNetworkChanged, client, this.publicHeaders, this.isShowLog, baseSocketListenerList);
        }

    }
}
