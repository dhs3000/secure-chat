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

package de.dennishoersch.web.chat;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import de.dennishoersch.web.websocket.chat.db.UserRepository;

/**
 * @author hoersch
 * 
 */
public class ChatRoom {
    private final Logger logger = Logger.getLogger(getClass());
    private final UserRepository _userRepository = new UserRepository();

    private final Encryption _encryption;

    private final Collection<ClientConnection> _clients = Collections.newSetFromMap(new ConcurrentHashMap<ClientConnection, Boolean>());

    public ChatRoom(Encryption encryption) {
        _encryption = encryption;
    }

    public final void broadcast(ClientConnection sourceClient, String message) {
        for (ClientConnection otherClient : _clients) {
            if (otherClient == sourceClient) {
                continue;
            }
            try {
                otherClient.sendMessage(message);
            } catch (IOException e) {
                logger.error("IOException on broadcast to client!", e);
            }

        }
    }

    public void add(ClientConnection handler) {
        _clients.add(handler);
    }

    public void remove(ClientConnection handler) {
        _clients.remove(handler);
    }

    /**
     * @param webSocket
     * @return a new ClientConnection
     */
    public ClientConnection newClientConnection(WebSocket webSocket) {
        return new ClientConnectionImpl(this, _userRepository, webSocket, _encryption);
    }
}
