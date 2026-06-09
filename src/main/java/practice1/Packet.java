package practice1;

import java.util.concurrent.atomic.AtomicLong;

public class Packet {
    public static final byte B_MAGIC = 0x13;
    public static final int MAX_MESSAGE_LENGTH = 1024 * 1024;

    private static final AtomicLong nextPktId = new AtomicLong(1);

    private final byte bSrc;
    private final long bPktId;
    private final Message message;

    public Packet(byte bSrc, Message message) {
        this.bSrc = bSrc;
        this.bPktId = nextPktId.getAndIncrement();
        this.message = message;
    }

    public Packet(byte bSrc, long bPktId, Message message) {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.message = message;
    }

    public byte getSrc() {
        return bSrc;
    }

    public long getPktId() {
        return bPktId;
    }

    public Message getMessage() {
        return message;
    }
}