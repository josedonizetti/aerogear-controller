package org.jboss.aerogear.controller.spi;

public interface AuthenticationProvider {
    public boolean hasRole(AerogearUser user, String param);

    public interface AerogearUser {
        //put your wish here
    }
}
