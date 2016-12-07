package models;

/**
 * Supported message types.
 */
public enum MessageType {
    ACKNOWLEDGEMENT("ACK"),
    BYE("BYE"),
    DEAD_USER("DED"),
    HELLO("HLO"),
    HOST_ROOM("HST"),
    LEADER("LDR"),
    MESSAGE("MSG"),
    NEGATIVE_ACKNOWLEDGEMENT("NAK"),
    USER_RANK_ORDER("ORD"),
    REQUEST_ROOM_LIST("ROM"),
    ROOM_LIST("LST"),
    USER("USR"),
    YOU("YOU"),
    PROCESS("PRC"),
    QUEUE("QUE"),
    HOST_UPDATED("RPL");
    LEADER_VOTE("VOT");

    private final String protocolCode;

    /**
     * Constructs a MessageType enum with the protocol code.
     * @param protocolCode The protocol code.
     */
    MessageType(final String protocolCode) {
        this.protocolCode = protocolCode;
    }

    /**
     * Returns the protocol code, e.g. ACK.
     * @return The protocol code.
     */
    public String getProtocolCode() {
        return protocolCode;
    }
}
