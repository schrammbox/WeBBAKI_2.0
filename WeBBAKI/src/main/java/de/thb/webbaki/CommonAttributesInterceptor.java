package de.thb.webbaki;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

public class CommonAttributesInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            HttpSession session = request.getSession(false);
            if(session != null) {
                long now = new Date().getTime();
                long lastAccessed = session.getLastAccessedTime();
                long timeoutPeriod = session.getMaxInactiveInterval();
                long remainingTime = ((timeoutPeriod * 1000) - (now - lastAccessed)) / 1000;
                modelAndView.addObject("remainingTime", remainingTime);
            }else{
                modelAndView.addObject("remainingTime", null);
            }
        }
    }
}

