package com.wong.learn.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * �൱��ԭweb.xml�Ĺ��ܡ�
 * ʵ����Ӧ��ʵ�ֵ���{@link ServletContainerInitializer}��
 * ��spring������ʵ�������{@link SpringServletContainerInitializer}����������ֻ��Զ���ʵ����{@link WebApplicationInitializer}�ӿڵ��࣬
 * ����ʵ��tomcat�����Զ�����
 *
 * @author wangjuntao
 * @date 2017-4-19
 * @since
 */
public class DefaultWebApplicationInitializer implements
		WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		
		//����contextLoader,�����һ������
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(ApplicationConfig.class);
		servletContext.addListener(new ContextLoaderListener(rootContext));
		
		/*
		 * Spring-Session������
		 * spring-session��jar������Ⱪ¶һ���С�springSessionRepositoryFilter����bean(������{@link SpringHttpSessionConfiguration})
		 * ���bean��{@link SessionRepositoryFilter}��
		 * ������DelegatingFilterProxy(String beanname)�����������������Զ�ע�����springSessionRepositoryFilter��
		 * ���������filter
		 */
		FilterRegistration.Dynamic sessionFilterConfig = servletContext.addFilter("springSessionRepositoryFilter", 
				new DelegatingFilterProxy("springSessionRepositoryFilter"));
		sessionFilterConfig.addMappingForUrlPatterns(getSessionDispatcherTypes(), false, "/*");
		sessionFilterConfig.setAsyncSupported(true);
		
		 // ���������
		FilterRegistration.Dynamic encodeFilterConfig = servletContext.addFilter("encodingFilter", new CharacterEncodingFilter());
		encodeFilterConfig.setAsyncSupported(true);
		encodeFilterConfig.addMappingForUrlPatterns(getSessionDispatcherTypes(), false, "/*");
		encodeFilterConfig.setInitParameter("encoding", "UTF-8");
		
		//DispatchServlet
		AnnotationConfigWebApplicationContext mvcContext = new AnnotationConfigWebApplicationContext();
		mvcContext.register(SpringMvcConfig.class);
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatchServlet",new DispatcherServlet(mvcContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.setAsyncSupported(true);
		dispatcher.addMapping("/");
	}
	
	protected EnumSet<DispatcherType> getSessionDispatcherTypes() {
		return EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR,
				DispatcherType.ASYNC);
	}

}
