package org.singledog.global.session.starter;

import org.singledog.global.session.GlobalSessionFilter;
import org.singledog.global.session.SpringContextUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.Collections;

/**
 * Created by Adam on 2017/9/13.
 */
@Configuration
public class GlobalSessionAutoConfiguration {

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setName("session-filter");//filter name
        bean.setFilter(new GlobalSessionFilter());//filter implement
        bean.setDispatcherTypes(DispatcherType.REQUEST);
        bean.setUrlPatterns(Collections.singleton("/*"));//url mapping
        return bean;
    }

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

}
