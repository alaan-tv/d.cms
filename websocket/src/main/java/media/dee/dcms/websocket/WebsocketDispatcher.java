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

public interface WebsocketDispatcher {

    /**
     * Invoked when a new websocket session is connected
     * @param session: the new connected session.
     */
    void sessionConnected(Session session);

    /**
     * Invoked when the session is closed
     * @param session the closed session
     */
    void sessionClosed(Session session);

    /**
     * Invoked when the client sends a message to the server via websocket
     * @param session: the connected session
     * @param message: the message as string.
     */
    void onMessage(Session session, String message);
}
