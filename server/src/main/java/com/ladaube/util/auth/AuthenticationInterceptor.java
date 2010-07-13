package com.ladaube.util.auth;

import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.util.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * Interceptor class for authentication. Intercepts incoming requests (event handling stage) and
 * handles the login process transparently for protected action events.
 *
 * @see com.ladaube.util.auth.LoginActionBean
 */
@Intercepts(LifecycleStage.EventHandling)
public class AuthenticationInterceptor implements Interceptor, AuthConstants {

    private String loginUrl = "/login";

    /** Log instance used to log information from this class. */
    private static final Log log = Log.getInstance(AuthenticationInterceptor.class);    

    public Resolution intercept(ExecutionContext executionContext) throws Exception {
        if (requiresAuthentication(executionContext)) {
            log.debug("Authentication is required...");
            if (!authenticated(executionContext)) {
                log.debug("Nobody's logged in, forward to login page");
                // forward to login page, passing the requested URI as an argument
                HttpServletRequest request = executionContext.getActionBeanContext().getRequest();
                String requestedPage = request.getRequestURI();
                // strip context path if needed
                String contextPath = request.getContextPath();
                if (requestedPage.startsWith(contextPath)) {
                    requestedPage = requestedPage.substring(contextPath.length());
                }
                String queryString = request.getQueryString();
                if (queryString!=null) {
                    requestedPage += queryString;
                }
                return new ForwardResolution(loginUrl).addParameter(REQ_PARAM_TARGET_URL, requestedPage);
            }
        }
        return executionContext.proceed();
    }

    /**
     * Return true if passed executionContext corresponds to an authenticated user, false otherwise.
     * This implementation checks if there is a user in the current session.
     *
     * @param executionContext the execution context
     * @return true if passed executionContext corresponds to an authenticated user, false otherwise
     */
    protected boolean authenticated(ExecutionContext executionContext) {
        ActionBeanContext abc = executionContext.getActionBeanContext();
        if (abc==null) {
            log.warn("No action bean context available ! Is the interceptor annotated with LifecycleStage.EventHandling ?");
            return false;
        }
        HttpServletRequest request = abc.getRequest();
        if (request==null) {
            log.warn("No http request available !");
            return false;
        }
        HttpSession session = request.getSession();
        Object user = session.getAttribute(SESSION_ATTR_CURRENT_USER);
        if (user!=null) {
            log.debug("Found user in session");
            return true;
        } else {
            log.debug("nobody is authenticated for the current session");
            return false;
        }
    }

    /**
     * Return true if passed executionContext requires authentication, false otherwise. Typically uses urls or bean classes
     * to determine it. This implementation checks if the bean class (if any) is annotated with @RequiresAuthentication. 
     * @param executionContext the execution context
     * @return true if passed authentication requires authentication
     */
    protected boolean requiresAuthentication(ExecutionContext executionContext) {
        ActionBean ab = executionContext.getActionBean();
        if (ab!=null) {
            Class<?> beanClass = ab.getClass();
            if (beanClass.isAnnotationPresent(RequiresAuthentication.class)) {
                // bean class annotated, all handlers require authentication
                log.debug("class ", beanClass.getName(), " is annotated, authentication required");
                return true;
            } else {
                Method handler = executionContext.getHandler();
                if (handler!=null && handler.isAnnotationPresent(RequiresAuthentication.class)) {
                    log.debug("handler ", handler.getName(), " is annotated, authentication required");
                    return handler.isAnnotationPresent(RequiresAuthentication.class);
                }
            }
        }
        log.debug("no annotations found, authentication isn't required");
        return false;
    }
    
}
