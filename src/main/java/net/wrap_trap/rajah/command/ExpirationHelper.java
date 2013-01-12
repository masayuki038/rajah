package net.wrap_trap.rajah.command;

import net.wrap_trap.rajah.Element;

public class ExpirationHelper {

    public static long expireDateFromNow(long expire) {
        return System.currentTimeMillis() + expire;
    }

    public static long remainingFromNow(long tsOfExpiration) {
        return tsOfExpiration - System.currentTimeMillis();
    }

    public static boolean isExpired(Element e) {
        Long expireDate = e.getExpire();
        if (expireDate == null) {
            return false;
        }
        return (expireDate < System.currentTimeMillis());
    }
}
