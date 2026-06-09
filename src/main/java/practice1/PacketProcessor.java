package practice1;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class PacketProcessor {

    public static byte[] encode(Packet packet) throws Exception {
        Message msg = packet.getMessage();
        byte[] payloadBytes = msg.getPayloadBytes();

        ByteBuffer msgBuffer = ByteBuffer.allocate(4 + 4 + payloadBytes.length);
        msgBuffer.putInt(msg.getCType());
        msgBuffer.putInt(msg.getBUserId());
        msgBuffer.put(payloadBytes);

        byte[] encryptedMessage = CryptoUtil.encrypt(msgBuffer.array());
        int wLen = encryptedMessage.length;

        ByteBuffer packetBuffer = ByteBuffer.allocate(16 + wLen + 2);

        packetBuffer.put(Packet.B_MAGIC);
        packetBuffer.put(packet.getSrc());
        packetBuffer.putLong(packet.getPktId());
        packetBuffer.putInt(wLen);

        byte[] headerBytes = Arrays.copyOfRange(packetBuffer.array(), 0, 14);
        short headerCrc = Crc16.calculateCrc(headerBytes);
        packetBuffer.putShort(headerCrc);

        packetBuffer.put(encryptedMessage);

        byte[] payloadForCrc = Arrays.copyOfRange(packetBuffer.array(), 16, 16 + wLen);
        short payloadCrc = Crc16.calculateCrc(payloadForCrc);
        packetBuffer.putShort(payloadCrc);

        return packetBuffer.array();
    }

    public static Packet decode(byte[] packetBytes) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(packetBytes);

        byte magic = buffer.get();
        if (magic != Packet.B_MAGIC) {
            throw new IllegalArgumentException("Невірний байт на початку пакета (bMagic).");
        }

        byte bSrc = buffer.get();
        long bPktId = buffer.getLong();
        int wLen = buffer.getInt();

        short expectedHeaderCrc = buffer.getShort();
        byte[] headerBytes = Arrays.copyOfRange(packetBytes, 0, 14);
        short actualHeaderCrc = Crc16.calculateCrc(headerBytes);
        if (expectedHeaderCrc != actualHeaderCrc) {
            throw new SecurityException("CRC16 заголовка не збігається. Дані пошкоджено.");
        }

        byte[] encryptedMessage = new byte[wLen];
        buffer.get(encryptedMessage);

        short expectedPayloadCrc = buffer.getShort();
        short actualPayloadCrc = Crc16.calculateCrc(encryptedMessage);
        if (expectedPayloadCrc != actualPayloadCrc) {
            throw new SecurityException("CRC16 повідомлення не збігається. Дані пошкоджено.");
        }

        byte[] decryptedMessageBytes = CryptoUtil.decrypt(encryptedMessage);

        ByteBuffer msgBuffer = ByteBuffer.wrap(decryptedMessageBytes);
        int cType = msgBuffer.getInt();
        int bUserId = msgBuffer.getInt();

        byte[] jsonBytes = new byte[decryptedMessageBytes.length - 8];
        msgBuffer.get(jsonBytes);

        Message message = new Message(cType, bUserId, jsonBytes);

        return new Packet(bSrc, bPktId, message);
    }
}