package com.boluo.web;

import javax.ws.rs.Path;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author mixueqiang
 * @since Apr 4, 2016
 */
@Path("/dashboard")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DashboardResource extends BaseResource {

}
