package org.jboss.aerogear.controller.router;

import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jboss.aerogear.controller.log.AeroGearLogger;
import org.jboss.aerogear.controller.util.StringUtils;

import br.com.caelum.iogi.Iogi;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

public class DefaultParametersExtractor implements ParametersExtractor {

  private Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
  
  private HttpServletRequest request;
  private Route route;
  private String requestPath;
  private Map<String, String[]> requetParameters;

  public DefaultParametersExtractor(Map<String, String[]> requetParameters, Route route, String requestPath) {
    this.requetParameters = requetParameters;
    this.route = route;
    this.requestPath = requestPath;
  }

  public Object[] extract() {
    if (route.isParameterized()) {
        return extractPathParameters(requestPath, route);
    } else {
        return extractRequestParameters(request, route);
    }
  }
  
  
  private Object[] extractPathParameters(String requestPath, Route route) {
        // TODO: extract this from resteasy
        final int paramOffset = route.getPath().indexOf('{');
        final CharSequence param = requestPath.subSequence(paramOffset, requestPath.length());
        return new Object[]{param.toString()};
    }

    private Object[] extractRequestParameters(HttpServletRequest request, Route route) {
        LinkedList<Parameter> parameters = createParameterListFromRequest();
        
        Class<?>[] parameterTypes = route.getTargetMethod().getParameterTypes();
        if (parameterTypes.length == 1) {
            return createTargetMethodParameters(parameters, parameterTypes);
        }

        return new Object[0];  //To change body of created methods use File | Settings | File Templates.
    }

  private Object[] createTargetMethodParameters(LinkedList<Parameter> parameters,
      Class<?>[] parameterTypes) {
    Class<?> parameterType = parameterTypes[0];
    Target<?> target = Target.create(parameterType, StringUtils.downCaseFirst(parameterType.getSimpleName()));
    Object instantiate = iogi.instantiate(target, parameters.toArray(new Parameter[]{}));
    return new Object[]{instantiate};
  }

  private LinkedList<Parameter> createParameterListFromRequest() {
    LinkedList<Parameter> parameters = new LinkedList<Parameter>();

        for (Map.Entry<String, String[]> entry : requetParameters.entrySet()) {
            String[] value = entry.getValue();
            if (value.length == 1) {
                parameters.add(new Parameter(entry.getKey(), value[0]));
            } else {
                AeroGearLogger.LOGGER.multivaluedParamsUnsupported();
                continue;
            }
        }
    return parameters;
  }


}