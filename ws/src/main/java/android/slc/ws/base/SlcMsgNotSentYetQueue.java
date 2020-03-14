package android.slc.ws.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 发送的消息队列
 */
public class SlcMsgNotSentYetQueue {
    private static SlcMsgNotSentYetQueue slcMsgNotSentYetQueue = new SlcMsgNotSentYetQueue();
    private final List<Object> unsentMessageList = new ArrayList<>();

    private SlcMsgNotSentYetQueue() {

    }

    /**
     * 获取实例
     *
     * @return
     */
    protected static SlcMsgNotSentYetQueue getInstance() {
        return slcMsgNotSentYetQueue;
    }

    public void addMsgToQueue(Object msg) {
        synchronized (unsentMessageList) {
            unsentMessageList.add(msg);
        }
    }

    public Iterator<Object> getMsgNotSentYetQueue() {
        synchronized (unsentMessageList) {
            return unsentMessageList.iterator();
        }
    }
}
