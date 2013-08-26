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
import java.security.KeyPair;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import de.dennishoersch.lang.Throwables;
import de.dennishoersch.web.websocket.chat.db.User;
import de.dennishoersch.web.websocket.chat.db.UserRepository;

/**
 * @author hoersch
 * 
 */
public class ClientConnectionImpl implements ClientConnection {
    protected static final Logger logger = Logger.getLogger(ClientConnectionImpl.class);

    protected final UserRepository _userRepository;

    protected final ChatRoom _chatRoom;

    private final WebSocket _webSocket;

    protected final Encryption _encryption;

    protected final KeyPair _serverRsaKeyPair;

    protected String _clientPublicKey;

    interface ConnectionHandler {
        /**
         * Might throw an Exception to indicate something went wrong.
         * 
         * @param msg
         * @return the result
         */
        String handle(String msg);

        /**
         * @return the next handler
         */
        ConnectionHandler next();
    }

    private ConnectionHandler _connectionHandler = new StartConnectionHandler();

    public ClientConnectionImpl(ChatRoom chatRoom, UserRepository userRepository, WebSocket webSocket, Encryption encryption) {
        _chatRoom = chatRoom;
        _userRepository = userRepository;
        _webSocket = webSocket;
        _encryption = encryption;

        _serverRsaKeyPair = _encryption.generateRSAKeyPair();
    }

    @Override
    public void sendMessage(String message) throws IOException {
        _webSocket.sendMessage(message);
    }

    @Override
    public void onOpen() {
        logger.debug("onOpen");
        if (_connectionHandler instanceof StartConnectionHandler) {
            String result = ((StartConnectionHandler) _connectionHandler).onOpen();
            try {
                _webSocket.sendMessage(result);
            } catch (IOException e) {
                throw Throwables.throwUnchecked(e);
            }
            return;
        }
        throw new IllegalStateException("More then one attempt to open a connection from one client!");

    }

    @Override
    public void onTextMessage(String message) {
        // verschiedene Handler
        // bei exception: onError auslösen

        logger.debug("onTextMessage : " + message);

        String decryptedMessage = extractDecryptedMessage(message);
        String result = _connectionHandler.handle(decryptedMessage);

        _connectionHandler = _connectionHandler.next();

        if (result != null) {
            String encrypedMessage = createEncrypedMessage(result);
            try {
                _webSocket.sendMessage(encrypedMessage);
            } catch (IOException e) {
                throw Throwables.throwUnchecked(e);
            }
        }

    }

    @Override
    public void onClose() {
        // FIXME: mit timeout? oder was wenn browser zu?
        _chatRoom.remove(this);
    }

    protected final class StartConnectionHandler implements ConnectionHandler {

        /**
         * 
         * @return the message to send onOpen connection
         */
        public String onOpen() {
            String publicKey = _encryption.transformToPublicKey(_serverRsaKeyPair);
            return publicKey;
        }

        @Override
        public String handle(String msg) {

            _clientPublicKey = msg;

            return "connected";

        }

        @Override
        public ConnectionHandler next() {
            return new ConfirmConnectionHandler();
        }

    }

    protected final class ConfirmConnectionHandler implements ConnectionHandler {

        @Override
        public String handle(String msg) {
            String confirmation = msg;
            if (confirmation.equals("connected")) {
                return null;
            }
            throw new IllegalStateException("Connection can't be established successfully. Client response: " + msg);
        }

        @Override
        public ConnectionHandler next() {
            return new LoginConnectionHandler();
        }
    }

    protected final class LoginConnectionHandler implements ConnectionHandler {

        @Override
        public String handle(String msg) {
            try {
                JSONObject json = new JSONObject(msg);
                String name = json.getString("name");
                String pwd = json.getString("pwd");

                User user = _userRepository.findUserByName(name);
                if (user != null && user.isPasswordCorrect(pwd)) {
                    _chatRoom.add(ClientConnectionImpl.this);
                    return "success";
                }
                return "failure";
            } catch (JSONException e) {
                throw Throwables.throwUnchecked(e);
            }
        }

        @Override
        public ConnectionHandler next() {
            return new NormalConnectionHandler();
        }
    }

    protected final class NormalConnectionHandler implements ConnectionHandler {

        @Override
        public String handle(String msg) {

            // Send message to all clients connected
            _chatRoom.broadcast(ClientConnectionImpl.this, msg);
            return msg;
        }

        @Override
        public ConnectionHandler next() {
            return this;
        }
    }

    private final String encryptAES(String key, String input) {
        return _encryption.encryptAES(key, input);
    }

    private final String createEncrypedMessage(String msg) {
        try {
            String aesKey = _encryption.generatePassphrase();

            String result = encryptRSA(_clientPublicKey, aesKey);

            String data = encryptAES(aesKey, msg);

            //@formatter:off
            return new JSONObject()
                        .put("key", result)
                        .put("data", data)
                        .toString();
            //@formatter:on
        } catch (JSONException e) {
            throw Throwables.throwUnchecked(e);
        }
    }

    private final String extractDecryptedMessage(String msg) {
        try {
            JSONObject jsonIn = new JSONObject(msg);
            String key = getEncryptionKey(jsonIn);

            String data = jsonIn.getString("data");
            String clientPublicKey = decryptAES(key, data);
            return clientPublicKey;
        } catch (JSONException e) {
            throw Throwables.throwUnchecked(e);
        }
    }

    private final String getEncryptionKey(JSONObject json) throws JSONException {
        String aesKey = json.getString("key");

        String decrypt = _encryption.decryptRSA(_serverRsaKeyPair, aesKey);
        return decrypt;
    }

    private final String encryptRSA(String publicKey, String input) {
        return _encryption.encryptRSA(publicKey, input);
    }

    private final String decryptAES(String key, String input) {
        return _encryption.decryptAES(key, input);
    }
}
