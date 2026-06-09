package practice1;

import java.nio.charset.StandardCharsets;

public class Message {
    private final int cType;
    private final int bUserId;
    private final String payload;

    public Message(int cType, int bUserId, String payload) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.payload = payload;
    }

    public Message(int cType, int bUserId, byte[] payloadBytes) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.payload = new String(payloadBytes, StandardCharsets.UTF_8);
    }

    public int getCType() {
        return cType;
    }

    public int getBUserId() {
        return bUserId;
    }

    public String getPayload() {
        return payload;
    }

    public byte[] getPayloadBytes() {
        return payload.getBytes(StandardCharsets.UTF_8);
    }
}