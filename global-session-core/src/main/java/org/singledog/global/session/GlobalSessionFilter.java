package org.singledog.global.session;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Adam on 2017/8/15.
 */
public class GlobalSessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final WrappedRequest wrappedRequest = new WrappedRequest((HttpServletRequest) request, (HttpServletResponse) response);
        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void destroy() {

    }
}
