package org.jboss.aerogear.controller.router;

public class RouteBuilderImpl implements RouteBuilder {

    private RouteDescriptor routeDescriptor;

    @Override
    public OnMethods from(String path) {
        return routeDescriptor = new RouteDescriptor(path);
    }

    @Override
    public Route build() {
        return new DefaultRoute(routeDescriptor.getPath(), routeDescriptor.getMethods(), routeDescriptor.getTargetClass(),
                routeDescriptor.getTargetMethod(), routeDescriptor.getRoles());
    }

    @Override
    public String toString() {
        return "RouteBuilderImpl{" +
                "routeDescriptor=" + routeDescriptor +
                '}';
    }
}
