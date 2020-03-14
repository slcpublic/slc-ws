package android.slc.ws.core;

import android.os.Handler;
import android.util.ArrayMap;

import android.slc.ws.SlcWs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class SlcWebSocket<Send extends SlcWebSocket.SendDelegate, Call extends SlcWebSocket.OnSocketListener> {
    private WebSocket webSocket;
    private Request request;
    private Send onSendListener;
    private List<SlcWebSocket.OnSocketListener> callList = new ArrayList<>();
    private int currentReconnectCount;
    private int currentConnectState = SlcWsConstant.CONNECT_STATE_CUT;//当前的连接状态
    private Handler handler;
    private SocketListenerComparable socketListenerComparable = new SocketListenerComparable();

    public SlcWebSocket(String url) {
        this(new Request.Builder()
                .url(url));
    }

    public SlcWebSocket(Request.Builder requestBuilder) {
        if (WsConfigManager.getInstance().getWsConfig() != null) {
            ArrayMap<String, String> publicHeaders = WsConfigManager.getInstance().getWsConfig().getPublicHeaders();
            for (int i = 0; i < publicHeaders.size(); i++) {
                requestBuilder.header(publicHeaders.keyAt(i), publicHeaders.valueAt(i));
            }
            callList.addAll(WsConfigManager.getInstance().getWsConfig().getBaseSocketListenerList());
            Collections.sort(callList, socketListenerComparable);
        }
        this.request = requestBuilder.build();
        handler = new Handler(SlcWs.getInstance().getApp().getMainLooper());
    }

    /**
     * 设置发送监听
     *
     * @param onSendListener
     */
    public void setOnSendListener(Send onSendListener) {
        this.onSendListener = onSendListener;
    }

    public Send getOnSendListener() {
        return onSendListener;
    }

    /**
     * 设置接收监听
     *
     * @param onSocketListener
     */
    public void addOnSocketListener(Call onSocketListener) {
        if (!callList.contains(onSocketListener)) {
            callList.add(onSocketListener);
            Collections.sort(callList, socketListenerComparable);
        }
    }

    /**
     * 移除
     *
     * @param onSocketListener
     */
    public void removeSocketListener(Call onSocketListener) {
        callList.remove(onSocketListener);
    }

    /**
     * 移除
     *
     * @param index
     */
    public void removeSocketListener(int index) {
        if (callList.size() > index) {
            callList.remove(index);
        }
    }

    public int getCurrentConnectState() {
        return currentConnectState;
    }

    /**
     * 启动
     */
    public void startConnect() {
        if (currentConnectState == SlcWsConstant.CONNECT_STATE_OPEN || currentConnectState == SlcWsConstant.CONNECT_STATE_CONNECTING) {
            return;
        }
        currentConnectState = SlcWsConstant.CONNECT_STATE_CONNECTING;
        this.webSocket = WsConfigManager.getInstance().getWsConfig().getClient().newWebSocket(this.request, new WebSocketListener() {
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                int code = response.code();
                String message = response.message();
                SlcWsLog.d(SlcWsLog.TAG, "code = " + code + " message = " + message);
                //正常连接后将连接次数重置
                currentReconnectCount = WsConfigManager.getInstance().getWsConfig().getReconnectCount();
                //设置连接状态
                currentConnectState = SlcWsConstant.CONNECT_STATE_OPEN;
                //通知发送消息代理器
                if (onSendListener != null) {
                    onSendListener.setWebSocket(webSocket);
                    onSendListener.notifyConnectState(currentConnectState);
                }
                //通知监听器
                Iterator<OnSocketListener> listenerIterator = callList.iterator();
                while (listenerIterator.hasNext()) {
                    OnSocketListener onSocketListenerTemp = listenerIterator.next();
                    if (onSocketListenerTemp != null) {
                        if (onSocketListenerTemp.onOpen(webSocket, response)) break;
                    }
                }
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                SlcWsLog.d(SlcWsLog.TAG, "Receive String msg from web socket.And msg is " + text);
                //通知监听器
                Iterator<OnSocketListener> listenerIterator = callList.iterator();
                while (listenerIterator.hasNext()) {
                    OnSocketListener onSocketListenerTemp = listenerIterator.next();
                    if (onSocketListenerTemp != null) {
                        if (onSocketListenerTemp.onMessage(webSocket, text)) break;
                    }
                }
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
                SlcWsLog.d(SlcWsLog.TAG, "Receive byte String msg from web socket.And msg is " + bytes);
                //通知监听器
                Iterator<OnSocketListener> listenerIterator = callList.iterator();
                while (listenerIterator.hasNext()) {
                    OnSocketListener onSocketListenerTemp = listenerIterator.next();
                    if (onSocketListenerTemp != null) {
                        if (onSocketListenerTemp.onMessage(webSocket, bytes)) break;
                    }
                }
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
                SlcWsLog.d(SlcWsLog.TAG, "Web socket is closing");
                //TODO 此处设置为正常关闭 更改连接状态为正常关闭
                currentConnectState = SlcWsConstant.CONNECT_STATE_CLOSED;
                if (onSendListener != null) {
                    onSendListener.notifyConnectState(currentConnectState);
                }
                Iterator<OnSocketListener> listenerIterator = callList.iterator();
                while (listenerIterator.hasNext()) {
                    OnSocketListener onSocketListenerTemp = listenerIterator.next();
                    if (onSocketListenerTemp != null) {
                        if (onSocketListenerTemp.onClosing(webSocket, code, reason)) break;
                    }
                }
            }

            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                SlcWsLog.d(SlcWsLog.TAG, "Web socket has closed");
                //TODO 此处设置为正常关闭，可能需要另外处理  更改连接状态为正常关闭
                currentConnectState = SlcWsConstant.CONNECT_STATE_CLOSED;
                if (onSendListener != null) {
                    onSendListener.notifyConnectState(currentConnectState);
                }
                Iterator<OnSocketListener> listenerIterator = callList.iterator();
                while (listenerIterator.hasNext()) {
                    OnSocketListener onSocketListenerTemp = listenerIterator.next();
                    if (onSocketListenerTemp != null) {
                        if (onSocketListenerTemp.onClosed(webSocket, code, reason)) break;
                    }
                }
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                SlcWsLog.d(SlcWsLog.TAG, "Try to connect web socket.But got Failure");
                //更改连接状态为断开
                currentConnectState = SlcWsConstant.CONNECT_STATE_CUT;
                //通知发送消息代理器
                if (onSendListener != null) {
                    onSendListener.notifyConnectState(currentConnectState);
                }
                //通知监听器
                Iterator<OnSocketListener> listenerIterator = callList.iterator();
                while (listenerIterator.hasNext()) {
                    OnSocketListener onSocketListenerTemp = listenerIterator.next();
                    if (onSocketListenerTemp != null) {
                        if (onSocketListenerTemp.onFailure(webSocket, t, response)) break;
                    }
                }
                //开始重连
                if (currentReconnectCount != 0) {
                    //网络断开后过5秒重新连接一次
                    Iterator<OnSocketListener> listenerIteratorReconnect = callList.iterator();
                    while (listenerIterator.hasNext()) {
                        OnSocketListener onSocketListenerTemp = listenerIteratorReconnect.next();
                        if (onSocketListenerTemp != null) {
                            if (onSocketListenerTemp.onReconnect(webSocket)) break;
                        }
                    }

                    handler.postDelayed(() -> {
                        //当连接状态不是为连接上时 此处重新连接
                        if (currentConnectState != SlcWsConstant.CONNECT_STATE_OPEN) {
                            currentReconnectCount--;
                            startConnect();
                        }
                    }, WsConfigManager.getInstance().getWsConfig().getReconnectTimeInterval());
                }
            }
        });
    }

    /**
     * 关闭
     */
    public void stopConnect() {
        stopConnect(3000,"active disconnect");
    }
    public void stopConnect(int code,String reason) {
        handler.removeCallbacksAndMessages(null);
        if (webSocket != null) {
            webSocket.close(code,reason);
        }
    }
    /**
     * 消息发送监听
     */
    public interface SendDelegate {

        void setWebSocket(WebSocket webSocket);

        void notifyConnectState(int state);

        /**
         * 消息体
         *
         * @param msg
         */
        void send(String msg);

        /**
         * 消息体
         *
         * @param bytes
         */
        void send(ByteString bytes);
    }

    public interface OnSocketListener {
        /**
         * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
         * messages.
         *
         * @return 返回false 则继续通知其他的监听 否则将中断 默认为false 通知顺序根据priority来判断
         */
        boolean onOpen(WebSocket webSocket, Response response);

        /**
         * 正在重新连接
         *
         * @param webSocket
         * @return 返回false 则继续通知其他的监听 否则将中断 默认为false 通知顺序根据priority来判断
         */
        boolean onReconnect(WebSocket webSocket);

        /**
         * Invoked when a text (type {@code 0x1}) message has been received.
         *
         * @return 返回false 则继续通知其他的监听 否则将中断 默认为false 通知顺序根据priority来判断
         */
        boolean onMessage(WebSocket webSocket, String text);

        /**
         * Invoked when a binary (type {@code 0x2}) message has been received.
         *
         * @return 返回false 则继续通知其他的监听 否则将中断 默认为false 通知顺序根据priority来判断
         */
        boolean onMessage(WebSocket webSocket, ByteString bytes);

        /**
         * Invoked when the peer has indicated that no more incoming messages will be transmitted.
         *
         * @return 返回false 则继续通知其他的监听 否则将中断 默认为false 通知顺序根据priority来判断
         */
        boolean onClosing(WebSocket webSocket, int code, String reason);

        /**
         * Invoked when both peers have indicated that no more messages will be transmitted and the
         * connection has been successfully released. No further calls to this listener will be made.
         *
         * @return 返回false 则继续通知其他的监听 否则将中断 默认为false 通知顺序根据priority来判断
         */
        boolean onClosed(WebSocket webSocket, int code, String reason);

        /**
         * Invoked when a web socket has been closed due to an error reading from or writing to the
         * network. Both outgoing and incoming messages may have been lost. No further calls to this
         * listener will be made.
         *
         * @return 返回false 则继续通知其他的监听 否则将中断 默认为false 通知顺序根据priority来判断
         */
        boolean onFailure(WebSocket webSocket, Throwable t, Response response);

        /**
         * 获取优先级
         *
         * @return
         */
        int getPriority();
    }
}
