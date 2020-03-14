package android.slc.ws.core;

import java.util.Comparator;

public class SocketListenerComparable implements Comparator<SlcWebSocket.OnSocketListener> {
    @Override
    public int compare(SlcWebSocket.OnSocketListener o1, SlcWebSocket.OnSocketListener o2) {
        return Integer.compare(o1.getPriority(), o2.getPriority());
    }
}
