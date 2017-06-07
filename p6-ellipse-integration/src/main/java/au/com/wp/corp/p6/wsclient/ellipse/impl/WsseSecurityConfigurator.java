package au.com.wp.corp.p6.wsclient.ellipse.impl;

import javax.annotation.PostConstruct;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.util.List;

/**
 * A Spring bean that adds the {@code WsseSecurityHeaderHandler} to a JAX-WS generated service interface
 * and configures username and password.
 *
 * @author n039126
 */
public class WsseSecurityConfigurator {

    private BindingProvider bindingProvider;
    private String userName;
    private String password;

    @PostConstruct
    public void addWsseSecurityHeaderHandler() {
        List<Handler> handlerChain = bindingProvider.getBinding().getHandlerChain();
        handlerChain.add(0, new WsseSecurityHeaderHandler(userName, password)); // At before logger
        bindingProvider.getBinding().setHandlerChain(handlerChain);
    }

    public void setBindingProvider(BindingProvider bindingProvider) {
        this.bindingProvider = bindingProvider;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
