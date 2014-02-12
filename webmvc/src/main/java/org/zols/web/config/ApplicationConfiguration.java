package org.zols.web.config;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.mobile.device.site.SitePreferenceHandlerInterceptor;
import org.springframework.mobile.device.site.SitePreferenceHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.zols.swagger.RestDocumentationConfig;
import org.zols.web.interceptor.PagePopulationInterceptor;

@Configuration
@EnableWebMvc
@Import({RestDocumentationConfig.class, ViewConfiguration.class, ControllerConfiguration.class,SecurityConfig.class})
@ComponentScan(basePackages = {"org.zols"})
public class ApplicationConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
        return new DeviceResolverHandlerInterceptor();
    }

    @Bean
    public SitePreferenceHandlerInterceptor sitePreferenceHandlerInterceptor() {
        return new SitePreferenceHandlerInterceptor();
    }

    @Bean
    public SitePreferenceHandlerMethodArgumentResolver sitePreferenceHandlerMethodArgumentResolver() {
        return new SitePreferenceHandlerMethodArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers); //To change body of generated methods, choose Tools | Templates.
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
        argumentResolvers.add(sitePreferenceHandlerMethodArgumentResolver());
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    // Maps resources path to webapp/resources
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:static/");
        // map all static resources coming to '/usage/**' to the resource files under the 'swagger' directory
        ResourceHandlerRegistration registration = registry.addResourceHandler("/usage/**");
        registration.addResourceLocations("classpath:swagger/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jsonHttpMessageConverter());
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    @Bean
    public JacksonObjectMapper jacksonObjectMapper() {
        return new JacksonObjectMapper();
    }

    @Bean
    public MappingJacksonHttpMessageConverter jsonHttpMessageConverter() {
        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
        converter.setObjectMapper(jacksonObjectMapper());
        return converter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deviceResolverHandlerInterceptor());
        registry.addInterceptor(sitePreferenceHandlerInterceptor());
        registry.addInterceptor(pagePopulationInterceptor());
    }

    @Bean
    public PagePopulationInterceptor pagePopulationInterceptor() {
        return new PagePopulationInterceptor();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }
}
