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

import com.fasterxml.jackson.databind.JsonNode;
import media.dee.dcms.websocket.impl.session.RemoteSession;

/**
 * Manages the sessions connected to the server and remote connections.
 */
public interface SessionManager{

    /**
     * Invoked when a new websocket session is connected
     * @param session: the new connected session.
     * @return the session wrapper
     */
    Session sessionConnected(org.eclipse.jetty.websocket.api.Session session);

    /**
     * Invoked when the session is closed
     * @param session the closed session
     * @return the session wrapper
     */
    Session sessionClosed(org.eclipse.jetty.websocket.api.Session session);


    /**
     * Remove session locally, should be invoked only cross cluster.
     * @param session the closed session wrapper
     * @return the session wrapper
     */
    Session sessionClosed(RemoteSession session);

    /**
     * return the session wrapper of a jetty session.
     * @param session: jetty websocket session
     * @return session wrapper
     */
    Session get(org.eclipse.jetty.websocket.api.Session session);


    /**
     * return session wrapper by session id
     * @param id: the id of the session
     * @return session wrapper
     */
    Session get(String id);

    /**
     * send a message to directly connected clients to the current node.
     * @param message message to be sent to client
     * @return number of sent messages
     */
    long send(JsonNode message);

    /**
     * send a message to session
     */
    boolean send(Session session, JsonNode message);

    /**
     * broadcast a message to all connected clients
     */
    void broadcast(JsonNode message);

    /**
     * add a remote session to the current node.
     * @param session: remote session.
     */
    void addSession(RemoteSession session);

    /**
     * close a session, which requestd by remotely
     * @param session: remote session
     */
    boolean closeSession(RemoteSession session);
}
