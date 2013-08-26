
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

function ServerConnection(url, onConnectionEstablishedHandler, onMessageHandler) {
    'use strict';
    var ws = undefined,
        clientCrypt = undefined,
        serverCrypt = undefined,

        createEncryptedMessage = function(msg) {
            var passphrase = PassphraseGenerator.generate(32),
                ciphertext = Aes.Ctr.encrypt(msg, passphrase, 256),
                //var origtext = Aes.Ctr.decrypt(ciphertext, passphrase, 256);
                key = serverCrypt.encrypt(passphrase),
                data = ciphertext,
                result = {
                    'key': key,
                    'data': data
                };
            return JSON.stringify(result);
        },

        extractDecryptedMessage = function(text) {
            var msg = JSON.parse(text);

            var key = msg.key;
            var data = msg.data;

            var passphrase = clientCrypt.decrypt(key);
            var realData = Aes.Ctr.decrypt(data, passphrase, 256);
            return realData;
        },

        initializeConnectionHandler = function(data) {
            console.debug("Got server key, start initializing the connection and send client public key.");

            clientCrypt = new JSEncrypt();
            clientCrypt.getPrivateKey(); // Create private key.

            serverCrypt = new JSEncrypt();
            serverCrypt.setPublicKey(data.data);

            ws.onmessage = confirmConnectionHandler;

            ws.send(createEncryptedMessage(clientCrypt.getPublicKey()));

        },

        confirmConnectionHandler = function(data) {
            var msg = extractDecryptedMessage(data.data);
            if (msg == "connected") {
                console.debug("Got confirmation");
                ws.onmessage = connectionHandler;

                onConnectionEstablishedHandler(self);

                ws.send(createEncryptedMessage(msg));
            }
        },

        connectionHandler = function(data) {
            var msg = extractDecryptedMessage(data.data);
            // handler und sender auslagern!
            onMessageHandler(msg);
        };

    return {
        sendMessage: function(msg_) {
            var msg = msg_;
            if (msg instanceof ClassBase) {
                console.debug("Send message: " + msg.type());
                msg = msg.toJSONString();
            }
            ws.send(createEncryptedMessage(msg));
        },

        init: function() {
            ws = new WebSocket("ws://" + url);
            ws.onopen = function() {
                console.log("Websocket Ready!!");
            };
            ws.onclose = function() {
                console.log("Websocket Closed!!");
            };
            ws.onerror = function() {
                console.log("Websocket Error!!");
            };
            ws.onmessage = initializeConnectionHandler;
        }
    };
};