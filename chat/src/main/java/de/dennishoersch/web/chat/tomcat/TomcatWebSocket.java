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

package de.dennishoersch.web.chat.tomcat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.Logger;

import de.dennishoersch.web.chat.ClientConnection;
import de.dennishoersch.web.chat.WebSocket;

/**
 * @author hoersch
 * 
 */
class TomcatWebSocket extends MessageInbound implements WebSocket {
    private static final Logger logger = Logger.getLogger(TomcatWebSocket.class);
    private ClientConnection _clientConnection;

    TomcatWebSocket() {
    }

    @Override
    public void sendMessage(String message) throws IOException {
        getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
    }

    @Override
    protected void onOpen(WsOutbound outbound) {
        _clientConnection.onOpen();
    }

    @Override
    protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onTextMessage(CharBuffer msg) throws IOException {
        _clientConnection.onTextMessage(msg.toString());
    }

    @Override
    protected void onClose(int status) {
        logger.debug("onClose - status code: " + status);
        _clientConnection.onClose();
    }

    public void setClientConnection(ClientConnection clientConnection) {
        _clientConnection = clientConnection;
    }

}
