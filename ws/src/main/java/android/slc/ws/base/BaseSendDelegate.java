package android.slc.ws.base;

import android.slc.ws.core.SlcWebSocket;
import android.slc.ws.core.SlcWsConstant;

import com.google.gson.Gson;

import android.slc.ws.core.SlcWsLog;

import okhttp3.WebSocket;
import okio.ByteString;

public class BaseSendDelegate<T> implements SlcWebSocket.SendDelegate {
    protected WebSocket webSocket;
    protected int currentConnectState;


    @Override
    public synchronized void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public void notifyConnectState(int currentConnectState) {
        this.currentConnectState = currentConnectState;
    }

    /**
     * 发送字符串消息
     *
     * @param msg
     */
    @Override
    public synchronized void send(String msg) {
        if (webSocket != null && this.currentConnectState == SlcWsConstant.CONNECT_STATE_OPEN) {
            SlcWsLog.d(SlcWsLog.TAG, msg);
            webSocket.send(msg);
        }
    }

    /**
     * 发送字节消息
     *
     * @param bytes
     */
    @Override
    public synchronized void send(ByteString bytes) {
        if (webSocket != null && this.currentConnectState == SlcWsConstant.CONNECT_STATE_OPEN) {
            webSocket.send(bytes);
        }
    }

    /**
     * 发送聊天类型的消息
     * 如果发送失败就存到发送失败队列
     *
     * @param msg
     */
    public synchronized void send(T msg) {
        send(new Gson().toJson(msg));
    }
}
