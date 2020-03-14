package android.slc.ws.base;

import android.slc.ws.core.SlcWsConstant;

import com.google.gson.Gson;

import java.util.Iterator;

public class SimpleQueueSendDelegate<T> extends BaseSendDelegate<T> {
    /**
     * 给子类持有
     */
    protected SlcMsgNotSentYetQueue slcMsgNotSentYetQueue = SlcMsgNotSentYetQueue.getInstance();

    @Override
    public void notifyConnectState(int state) {
        super.notifyConnectState(state);
        sendYetQueueMsg();
    }

    /**
     * 发送在失败队列里面的消息
     */
    protected void sendYetQueueMsg() {
        //TODO 此处尝试重新发送失败的消息逻辑需要修改
        if (SlcWsConstant.CONNECT_STATE_OPEN == currentConnectState) {
            Iterator<Object> iterator = slcMsgNotSentYetQueue.getMsgNotSentYetQueue();
            while (iterator.hasNext()) {
                Object msgType = iterator.next();
                if (msgType != null && resolveSend((T) msgType)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 发送对象类型的消息
     * 如果发送失败就存到发送失败队列
     *
     * @param msg
     */
    public synchronized void send(T msg) {
        if (!resolveSend(msg)) {
            //TODO 此 处将发送失败的消息加入失败队列罗需要修改
            slcMsgNotSentYetQueue.addMsgToQueue(msg);
        }
    }

    /**
     * 决定要发送消息了
     *
     * @param msg
     * @return
     */
    protected boolean resolveSend(T msg) {
        if (webSocket != null && this.currentConnectState == SlcWsConstant.CONNECT_STATE_OPEN) {
            webSocket.send(new Gson().toJson(msg));
            return true;
        }
        return false;
    }
}
