package net.wrap_trap.rajah.protocol;

public class RedisProtocolReadException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5722389144033287838L;

    public RedisProtocolReadException(String message, String line, int lineNumber, int columnNumber) {
        super(String.format("%s, target: %s, line: %d, %d", message, line, lineNumber, columnNumber));
    }
}
