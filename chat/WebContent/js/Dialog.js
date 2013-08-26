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

/**
 * Startup the application.
 */
function Dialog() {
    'use strict';
    var user = {
        },
        views = new Views("content"),
        viewOnMessageHandler = function(msg) {
        },
        onConnectionEstablishedHandler = function(sc) {
            views.show('login.html', user);
        },
        onMessageHandler = function(msg){
            viewOnMessageHandler(msg);
        },
        server = new ServerConnection("localhost:7070/chat/websocket", onConnectionEstablishedHandler, onMessageHandler);

    views.registerPage('login.html', function() {
        viewOnMessageHandler = function(msg) {
            // ENABLE PAGE
            if (msg === "success") {
                user["name"] = jQuery("input[name='name']").val();
                views.show('chat.html', user);
                server.sendMessage(ChatMessages.createJoinMessage(user["name"]));
            } else {
                // set error message and stuff
                console.error("Unexpected message: " + msg);
            }
        };

        setTimeout(function() {
            jQuery("input[name='name']").focus();
        }, 100);

        jQuery("#content form").submit(function() {
            var name = jQuery("input[name='name']"),
                pwd = jQuery("input[name='pwd']"),
                msg = {
                    "name": name.val(),
                    "pwd": pwd.val()
                    };
            // DISABLE PAGE
            server.sendMessage(JSON.stringify(msg));
            return false;
        });
    });

    views.registerPage('chat.html', function() {
        viewOnMessageHandler = function(msg) {
            var message = ChatMessages.fromJSON(msg);
            console.debug("Got message: " + message.type());
            if (message.isTextMessage()) {
                var html = views.getPartial("chat-message.partial", message),
                    newMessage = jQuery(html).hide();

                jQuery("#messages").prepend(newMessage);
                newMessage.fadeIn();
                return;
            }
            if (message.isJoinMessage() && !message.itsMe(user.name)) {
                var html = views.getPartial("chat-join.partial", message),
                    newMessage = jQuery(html).hide();

                jQuery("#users").prepend(newMessage);
                newMessage.fadeIn();

                server.sendMessage(ChatMessages.createHelloMessage(user["name"]));
                return;
            }
            if (message.isHelloMessage() && !message.itsMe(user.name)) {
                var html = views.getPartial("chat-hello.partial", message),
                    newMessage = jQuery(html).hide();

                jQuery("#users").prepend(newMessage);
                newMessage.fadeIn();
                return;
            }
        };

        var form = jQuery("#content form");
        form.submit(function() {
            var message = jQuery("#new-message"),
                text = D.HTML.escape(message.val()),
                textMessage = ChatMessages.createTextMessage(user["name"], text);

            message.val('').focus();
            server.sendMessage(textMessage);
            return false;
        });

        jQuery('#content textarea').keydown(function (e) {
            if (e.ctrlKey && e.keyCode == 13) { // Ctrl-Enter pressed
                form.submit();
            }
        });

        setTimeout(function() {
            jQuery("#new-message").focus();
        }, 100);
    });

    return {
        start: function() {
            server.init();
        }
    };
};