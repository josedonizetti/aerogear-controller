package org.jboss.aerogear.controller.spi;

@Deprecated
public class DeltaSpikeAdapter implements AuthenticationProvider{
    @Override
    public boolean hasRole(AerogearUser user, String param) {
        return false;
    }
}
