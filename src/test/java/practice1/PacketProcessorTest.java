package practice1;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PacketProcessorTest {

    @Test
    void shouldEncodeAndDecodePacketSuccessfully() throws Exception {
        String jsonPayload = "{\"command\":\"login\",\"user\":\"sofiia\"}";
        Message originalMessage = new Message(105, 999, jsonPayload);

        Packet originalPacket = new Packet((byte) 1, originalMessage);

        byte[] networkBytes = PacketProcessor.encode(originalPacket);

        Assertions.assertThat(networkBytes).isNotEmpty();

        Packet decodedPacket = PacketProcessor.decode(networkBytes);

        Assertions.assertThat(decodedPacket.getSrc()).isEqualTo(originalPacket.getSrc());
        Assertions.assertThat(decodedPacket.getPktId()).isEqualTo(originalPacket.getPktId());

        Message decodedMessage = decodedPacket.getMessage();
        Assertions.assertThat(decodedMessage.getCType()).isEqualTo(originalMessage.getCType());
        Assertions.assertThat(decodedMessage.getBUserId()).isEqualTo(originalMessage.getBUserId());
        Assertions.assertThat(decodedMessage.getPayload()).isEqualTo(originalMessage.getPayload());
    }
}