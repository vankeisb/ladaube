package com.ladaube.util.rpc;

import java.lang.annotation.*;

/**
 * This annotation is used to mark action bean event methods as
 * RPC enabled. The Fat Client Interceptor uses information specified
 * in this annotation's 'alternateResolution' attribute in order to
 * manage RPC-style calls.
 * 
 * @author Remi VANKEISBELCLK - remi 'at' rvkb.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FatClientEvent {
    String alternateResolution();
}
