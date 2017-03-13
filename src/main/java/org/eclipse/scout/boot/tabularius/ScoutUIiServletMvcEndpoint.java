package org.eclipse.scout.boot.tabularius;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.scout.rt.ui.html.UiServlet;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.mvc.AbstractNamedMvcEndpoint;
import org.springframework.boot.actuate.endpoint.mvc.EndpointHandlerMapping;
import org.springframework.boot.actuate.endpoint.mvc.MvcEndpoints;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ServletWrappingController;
import org.springframework.web.util.UrlPathHelper;

@Configuration
@ConfigurationProperties(prefix = "endpoints.scout")
public class ScoutUIiServletMvcEndpoint extends AbstractNamedMvcEndpoint
		implements InitializingBean, ApplicationContextAware, ServletContextAware, DisposableBean {

	private final ServletWrappingController controller = new ServletWrappingController();

	public ScoutUIiServletMvcEndpoint() {
		super("scout", "/", true);
		this.controller.setServletClass(UiServlet.class);
		this.controller.setServletName("scout");
	}

	@Configuration
	protected static class EndpointHandlerMappingConfiguration {

		@Autowired
		public void handlerMapping(MvcEndpoints endpoints, ListableBeanFactory beanFactory, EndpointHandlerMapping mapping) {
			mapping.setDetectHandlerMethodsInAncestorContexts(true);
			mapping.setOrder(0);
		}
	}

	@RequestMapping("/**")
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.controller.handleRequest(new ScoutBootUiServletPathStripper(request, getPath()), response);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.controller.afterPropertiesSet();
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.controller.setServletContext(servletContext);
	}

	@Override
	public final void setApplicationContext(ApplicationContext context) throws BeansException {
		this.controller.setApplicationContext(context);
	}

	@Override
	public void destroy() {
		this.controller.destroy();
	}

	private static class ScoutBootUiServletPathStripper extends HttpServletRequestWrapper {

		private final String path;

		private final UrlPathHelper urlPathHelper;

		ScoutBootUiServletPathStripper(HttpServletRequest request, String path) {
			super(request);
			this.path = path;
			this.urlPathHelper = new UrlPathHelper();
		}

		@Override
		public String getPathInfo() {
			String value = this.urlPathHelper.decodeRequestString((HttpServletRequest) getRequest(),
					super.getRequestURI());
			if (value.contains(this.path)) {
				value = value.substring(value.indexOf(this.path) + this.path.length());
			}
			int index = value.indexOf("?");
			if (index > 0) {
				value = value.substring(0, index);
			}
			return "/" + value;
		}
	}
}