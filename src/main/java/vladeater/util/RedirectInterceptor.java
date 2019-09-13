package vladeater.util;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author XE on 29.08.2019
 * @project vladeater
 */

public class RedirectInterceptor extends HandlerInterceptorAdapter {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            String args = request.getQueryString() != null ? request.getQueryString() + "?" : "";
            String url = request.getRequestURI() + args;
            response.setHeader("Turbolinks-Location", url);
        }
    }
}
