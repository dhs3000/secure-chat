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

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import de.dennishoersch.web.chat.ChatRoom;
import de.dennishoersch.web.chat.ClientConnection;

/**
 * @author hoersch
 * 
 */
class ClientConnectionCreator implements WebSocketCreator {
    protected static final Logger logger = Logger.getLogger(ClientConnectionCreator.class);

    private final ChatRoom _chatRoom;

    ClientConnectionCreator(ChatRoom chatRoom) {
        _chatRoom = chatRoom;
    }

    @Override
    public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp) {
        logger.debug("ChatRoom: " + _chatRoom);
        JettyWebSocket result = new JettyWebSocket();
        ClientConnection clientConnection = _chatRoom.newClientConnection(result);
        result.setClientConnection(clientConnection);
        return result;
    }

}
