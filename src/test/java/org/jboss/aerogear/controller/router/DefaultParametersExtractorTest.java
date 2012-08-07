package org.jboss.aerogear.controller.router;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.jboss.aerogear.controller.RoutesTest;
import org.jboss.aerogear.controller.SampleController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultParametersExtractorTest {

  @Mock
  private Route route;
  
  @Test 
  public void extractOneParameterFromPath() {
    when(route.isParameterized()).thenReturn(true);
    when(route.getPath()).thenReturn("/controller/{id}");
    ParametersExtractor parametersExtractor = new DefaultParametersExtractor(null, route, "/controller/1");
    Object[] parameters = parametersExtractor.extract();
    assertThat(parameters[0]).isEqualTo("1");
  }
  
  @Test 
  public void extractParameterFromControllerMethod() throws Exception {
    Class clazz = SampleController.class;
    Method method = SampleController.class.getMethod("save", RoutesTest.Car.class);
    
    when(route.getTargetMethod()).thenReturn(method);
    when(route.getTargetClass()).thenReturn(clazz);
    
    Map<String, String[]> requestParameters = buildRequestParameters();
    ParametersExtractor parametersExtractor = new DefaultParametersExtractor(requestParameters, route, "/controller/2012/03");
    Object[] parameters = parametersExtractor.extract();
    
    RoutesTest.Car car = (RoutesTest.Car) parameters[0];
    
    assertThat(car.getName()).isEqualTo("delorean");
  }
  
  
  private Map<String, String[]> buildRequestParameters(){
    Map<String, String[]> parameters = new HashMap<String, String[]>();
    parameters.put("car.name", new String[]{"delorean"});
    return parameters;
  }
}
