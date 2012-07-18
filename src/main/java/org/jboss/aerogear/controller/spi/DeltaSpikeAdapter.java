package org.jboss.aerogear.controller.spi;

public class DeltaSpikeAdapter implements AuthenticationProvider{
    @Override
    public boolean hasRole(AerogearUser user, String param) {
        return false;
    }
}
