package models;

/**
 * Supported message types.
 */
public enum MessageType {
    ACKNOWLEDGEMENT("ACK"),
    BYE("BYE"),
    DISCONNECTED("DED"),
    HELLO("HLO"),
    HOST_ROOM("HST"),
    LEADER("LDR"),
    LIST_ROOMS("LST"),
    MESSAGE("MSG"),
    NEGATIVE_ACKNOWLEDGEMENT("NAK"),
    ORDER("ORD"),
    REQUEST_ROOMS("ROM"),
    USER("USR"),
    YOU("YOU");

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
