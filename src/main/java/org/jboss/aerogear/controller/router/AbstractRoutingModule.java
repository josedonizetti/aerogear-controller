package org.jboss.aerogear.controller.router;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractRoutingModule implements RoutingModule {
    private List<RouteBuilder> routes = new LinkedList<RouteBuilder>();

    public abstract void configuration();

    public RouteBuilder route() {
        RouteBuilder route = Routes.route();
        routes.add(route);
        return route;
    }

    @Override
    public Routes build() {
        configuration();
        return Routes.from(routes);
    }

    public static <T> T param(Class<T> clazz) {
        return null;
    }

    public static <T> T param() {
        return null;
    }

    public static String pathParam(String id) {
        return id;
    }
}
