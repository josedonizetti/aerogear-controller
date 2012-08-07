package org.jboss.aerogear.controller.router;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.aerogear.controller.spi.SecurityProvider;
import org.jboss.aerogear.controller.view.View;
import org.jboss.aerogear.controller.view.ViewResolver;

public class DefaultRouter implements Router {

    private Routes routes;
    private final BeanManager beanManager;
    private ViewResolver viewResolver;
    private ControllerFactory controllerFactory;

    @Inject
    private SecurityProvider securityProvider;

    @Inject
    public DefaultRouter(RoutingModule routes, BeanManager beanManager, ViewResolver viewResolver, ControllerFactory controllerFactory,
                         SecurityProvider securityProvider) {
        this.routes = routes.build();
        this.beanManager = beanManager;
        this.viewResolver = viewResolver;
        this.controllerFactory = controllerFactory;
        this.securityProvider = securityProvider;
    }

    @Override
    public boolean hasRouteFor(HttpServletRequest httpServletRequest) {
        return routes.hasRouteFor(extractMethod(httpServletRequest), extractPath(httpServletRequest));
    }

    private String extractPath(HttpServletRequest httpServletRequest) {
        ServletContext servletContext = httpServletRequest.getServletContext();
        String contextPath = servletContext.getContextPath();

        return httpServletRequest.getRequestURI().substring(contextPath.length());
    }

    private RequestMethod extractMethod(HttpServletRequest httpServletRequest) {
        return RequestMethod.valueOf(httpServletRequest.getMethod());
    }

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException {
        try {
            final String requestPath = extractPath(request);
            Route route = routes.routeFor(extractMethod(request), requestPath);

            if(route.isSecured() && securityProvider.isRouteAllowed(route)) {
                //TODO Call the security spi services
            }

            ParametersExtractor parametersExtractor = new DefaultParametersExtractor(request.getParameterMap(), route, requestPath);
            Object[] params = parametersExtractor.extract();
            
            Object result = route.getTargetMethod().invoke(getController(route), params);
            String viewPath = viewResolver.resolveViewPathFor(route);
            View view = new View(viewPath, result);
            if (view.hasModelData()) {
                request.setAttribute(view.getModelName(), view.getModel());
            }
            request.getRequestDispatcher(view.getViewPath()).forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

  
    private Object getController(Route route) {
        return controllerFactory.createController(route.getTargetClass(), beanManager);
    }

}
