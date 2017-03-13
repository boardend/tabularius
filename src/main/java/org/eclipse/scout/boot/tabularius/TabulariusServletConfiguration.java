package org.eclipse.scout.boot.tabularius;

import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.scout.boot.ui.ScoutBootWebappListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.security.IPrincipalProducer;
import org.eclipse.scout.rt.server.commons.HttpSessionMutex;
import org.eclipse.scout.rt.server.commons.authentication.ServletFilterHelper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@Controller
@RequestMapping("/")
public class TabulariusServletConfiguration extends WebMvcConfigurerAdapter {

	public static final String WEBJARS_CONTEXT_PATH = "/webjars";

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public void loginRedirect(HttpServletResponse httpServletResponse) throws IOException {
		redirect(httpServletResponse);
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public void logoutRedirect(HttpServletResponse httpServletResponse) throws IOException {
		redirect(httpServletResponse);
	}
	
	protected void redirect(HttpServletResponse httpServletResponse) throws IOException {
		httpServletResponse.sendRedirect("/");
	}
    
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.setOrder(-1);
		if (!registry.hasMappingForPattern(WEBJARS_CONTEXT_PATH + "/**")) {
			registry.addResourceHandler(WEBJARS_CONTEXT_PATH + "/**")
					.addResourceLocations("classpath:/META-INF/resources/webjars/");
		}
	}
	
	@Bean
	public ServletListenerRegistrationBean<ServletContextListener> scoutSpringWebappListener() {
		return new ServletListenerRegistrationBean<>(new ScoutBootWebappListener());
	}

	@Bean
	public ServletListenerRegistrationBean<HttpSessionMutex> httpSessionMutex() {
		return new ServletListenerRegistrationBean<>(new HttpSessionMutex());
	}

	@Bean
	public FilterRegistrationBean jaasFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new GenericFilterBean() {
			public final void doFilter(final ServletRequest request, final ServletResponse response,
					final FilterChain chain) throws ServletException, IOException {
				final PrivilegedExceptionAction<Object> continueChain = new PrivilegedExceptionAction<Object>() {
					public Object run() throws IOException, ServletException {
						chain.doFilter(request, response);
						return null;
					}
				};
				try {
					final HttpServletRequest req = (HttpServletRequest) request;
					Principal principal = req.getUserPrincipal();
					if (principal == null) {
						principal = BEANS.get(IPrincipalProducer.class).produce(req.getSession(true).getId());
					}
					Subject.doAs(BEANS.get(ServletFilterHelper.class).createSubject(principal), continueChain);
				} catch (PrivilegedActionException e) {
					throw new ServletException(e.getMessage(), e);
				}
			}
		});
		registration.addUrlPatterns("/*");
		registration.setOrder(1);
		return registration;
	}

}
