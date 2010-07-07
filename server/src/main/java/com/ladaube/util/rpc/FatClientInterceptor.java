package com.ladaube.util.rpc;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.ajax.JavaScriptResolution;

import java.lang.reflect.Method;

/**
 * Stripes interceptor used for easy RPC-enabling
 * of Stripes ActionBeans.
 * This interceptor handles validation and user specific error
 * cases out of the box, and works with the @FatClientEvent annotation
 * in order to RPC-enable Action events (like returning JSON instead of
 * HTML) without any intrusion on your existing code.
 * The interceptor uses a "special" request parameter in order to
 * distinguish "regular" and "RPC" requests.
 * 
 * @author Remi@ VANKEISBELCLK - remi 'at' rvkb.com
 */
@Intercepts({
        LifecycleStage.BindingAndValidation,
        LifecycleStage.CustomValidation,
        LifecycleStage.EventHandling
        })
public class FatClientInterceptor implements Interceptor, ConfigurableComponent {

    /** default special parameter name */
    public static final String PARAM_DEFAULT_NAME = "stripesFatClient";
    public static final String FAT_CLIENT_PARAM = "FatClient.RequestParamName";

    private String fatClientParamName;

    /**
     * Initialize the interceptor, sets the name of the special request parameter to
     * be used from the Stripes configuration.
     */
    public void init(Configuration configuration) throws Exception {
        fatClientParamName = configuration.getBootstrapPropertyResolver().getProperty(FAT_CLIENT_PARAM);
        if (fatClientParamName==null) {
            fatClientParamName = PARAM_DEFAULT_NAME;
        }

    }

    /**
     * Intercepts execution and behaves as needed for RPC style calls :
     * <ul>
     *   <li>Stream back errors if any</li>
     *   <li>Invoke actual event handler and then grab the "fat client resolution"
     *      to be used</li>
     * </ul>
     */
    public Resolution intercept(ExecutionContext context) throws Exception {

        // process binding/validation/event
        Resolution result = context.proceed();

        // activate on special parameter
        String paramFatClient = context.
                getActionBeanContext().
                getRequest().
                getParameter(fatClientParamName);
        if (paramFatClient!=null) {

            // Special parameter found in the request, we handle this one
            // the RPC way !

            // first, do we have errors ?
            ValidationErrors errors =
                    context.getActionBeanContext().getValidationErrors();
            if (errors.size() > 0) {
                // validation errors found, return them serialized
                result = serializeErrors(context);
            } else {

                // no validation errors, we handle event the RPC way
                // if we find the annotation...

                if (context.getLifecycleStage().equals(LifecycleStage.EventHandling)) {

                    // event already handled, we return fat client resolution if available from the bean
                    // lookup for @FatClientEvent on the event handler
                    
                    Method evtHandler = context.getHandler();
                    FatClientEvent annot = evtHandler.getAnnotation(FatClientEvent.class);
                    if (annot!=null) {
                        // yep, fat client event, let's invoke the appropriate method
                        // and return the fat client resolution...
                        String fatMethodName = annot.alternateResolution();
                        Class clazz = context.getActionBean().getClass();
                        Method m = clazz.getDeclaredMethod(fatMethodName);
                        if (m==null) {
                            throw new IllegalArgumentException("The method '" + fatMethodName + " does not exist on class " + clazz);
                        } else {
                            if (Resolution.class.isAssignableFrom(m.getReturnType())) {
                                result = (Resolution)m.invoke(context.getActionBean());
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Serializes validation errors and streams them back to the caller.
     * This implementation uses JavaScriptResolution in order to serialize the
     * ValidationErrors object to JSON. <br/>
     * You can extend the interceptor and override this method if you need other types of serialization.
     */
    public Resolution serializeErrors(ExecutionContext context) {
        ValidationErrors errors = context.getActionBeanContext().getValidationErrors();
        return new JavaScriptResolution(errors);
    }

}
