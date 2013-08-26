/*
 * Copyright 2012-2013 Dennis Hörsch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dennishoersch.web.chat.jetty;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import de.dennishoersch.web.chat.ClientConnection;

/**
 * @author hoersch
 * 
 */
@WebSocket
public class JettyWebSocket implements de.dennishoersch.web.chat.WebSocket {
    private static final Logger logger = Logger.getLogger(JettyWebSocket.class);

    private Session _session;

    private ClientConnection _clientConnection;

    JettyWebSocket() {
    }

    @Override
    public void sendMessage(String message) throws IOException {
        _session.getRemote().sendString(message);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this._session = session;
        _clientConnection.onOpen();
    }

    @OnWebSocketMessage
    public void onText(String message) throws IOException {
        _clientConnection.onTextMessage(message);
    }

    @OnWebSocketClose
    public void onClose(int status, String reason) {
        logger.debug("onClose - status code: " + status);
        _clientConnection.onClose();
    }

    public void setClientConnection(ClientConnection clientConnection) {
        _clientConnection = clientConnection;
    }
}
