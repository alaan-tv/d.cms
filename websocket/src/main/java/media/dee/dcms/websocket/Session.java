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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Future;

public interface Session {

    /**
     * get the identifier of the session
     * @return id of the session
     */
    String getId();

    /**
     * Request a close of the current conversation with a normal status code and no reason phrase.
     * <p>
     * This will enqueue a graceful close to the remote endpoint.
     *
     */
    void close();


    /**
     * Returns the version of the websocket protocol currently being used. This is taken as the value of the Sec-WebSocket-Version header used in the opening
     * handshake. i.e. "13".
     *
     * @return the protocol version
     */
    String getProtocolVersion();

    /**
     * Get the address of the remote side.
     *
     * @return the remote side address
     */
    InetSocketAddress getRemoteAddress();


    /**
     * Return true if and only if the underlying socket is using a secure transport.
     *
     * @return whether its using a secure transport
     */
    boolean isSecure();

    /**
     * Send a Json message, blocking until all bytes of the message has been transmitted.
     * <p>
     * Note: this is a blocking call
     *
     * @param json the json message
     */
    void send(JsonNode json) throws IOException;

    /**
     * Initiates the asynchronous transmission of a text message. This method returns before the message is transmitted. Developers may provide a callback to be
     * notified when the message has been transmitted, or may use the returned Future object to track progress of the transmission. Errors in transmission are
     * given to the developer in the WriteResult object in either case.
     *
     * @param json the json message to  be sent to the client
     *            the text being sent
     * @return the Future object representing the send operation.
     */
    Future<Void> sendByFuture(JsonNode json);


    /**
     * get attributes attached to this session, attributes will be synchroinzed over the cluster nodes.
     * @return map of attributes
     */
    Map<String,Object> getAttributes();

    /**
     * sets attributes attached to this session, attributes will be synchroinzed over the cluster nodes.
     * @param  map of attributes
     */
    void setAttributes(Map<String, Object> map);
}
