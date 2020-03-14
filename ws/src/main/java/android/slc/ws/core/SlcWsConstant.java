package android.slc.ws.core;

public class SlcWsConstant {
    /**
     * 默认尝试重新连接次数
     */
    public static final int DEF_RECONNECT_COUNT = 32;
    /**
     * 默认连接时间间隔 5000毫秒
     */
    public static final long DEF_RECONNECT_TIME = 5000;
    /**
     * 未连接/连接断开
     */
    public static final int CONNECT_STATE_CUT = 0;
    /**
     * 正在连接
     */
    public static final int CONNECT_STATE_CONNECTING = 1;
    /**
     * 已连接
     */
    public static final int CONNECT_STATE_OPEN = 2;
    /**
     * 正常关闭
     */
    public static final int CONNECT_STATE_CLOSED = 3;
}
