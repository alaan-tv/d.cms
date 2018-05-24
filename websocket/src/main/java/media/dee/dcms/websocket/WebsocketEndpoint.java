/*
 * Copyright (c) 2002-2018 "dee media"
 *
 *
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


package media.dee.dcms.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import java.util.function.Consumer;

public abstract class WebsocketEndpoint {

    private final BundleContext bundleContext;
    private final Class<? extends WebsocketDispatcher> serviceClass;
    private final ServiceTracker<WebsocketDispatcher, WebsocketDispatcher> serviceTracker;

    public WebsocketEndpoint(Class<? extends WebsocketDispatcher> serviceClass) {
        this.serviceClass = serviceClass;
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        this.bundleContext = bundle.getBundleContext();
        this.serviceTracker = new ServiceTracker<>(this.bundleContext, this.serviceClass.getName(), null);
        this.serviceTracker.open();
    }

    @Override
    public void finalize(){
        this.serviceTracker.close();
    }

    private void handle(Consumer<WebsocketDispatcher> consumer) {
        ServiceReference<? extends WebsocketDispatcher> serviceReference = this.bundleContext.getServiceReference(this.serviceClass);
        WebsocketDispatcher handler = this.bundleContext.getService(serviceReference);
        consumer.accept(handler);
        this.bundleContext.ungetService(serviceReference);
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        if( this.serviceTracker.isEmpty() )
            return;
        this.serviceTracker.getService().sessionConnected(session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        if( this.serviceTracker.isEmpty() )
            return;
        this.serviceTracker.getService().sessionClosed(session);
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) {
        if( this.serviceTracker.isEmpty() )
            return;
        this.serviceTracker.getService().onMessage(session, message);
    }

}
