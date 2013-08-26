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

D.singleton("ChatMessages", function() {
    'use strict';

    var Named = ClassBase.extendLocal("Named", {
        init: function(name) {
            Named.$super(this);
            this.name = name;
        },
        itsMe: function(username) {
            return this.name === username;
        }
    });

    var TextMessage = Named.extendLocal("TextMessage", {
        init: function(name, timestamp, message) {
            TextMessage.$super(this, name);
            this.message = message;
            this.timestamp = timestamp;
        }
    });

    var UserText = ClassBase.extendLocal("UserText", {
        init: function(text) {
            UserText.$super(this);
            this.text = text;
        },
        toViewString: function() {
            return D.HTML.newlineToBr(this.text);
        }
    });

    var JoinMessage = Named.extendLocal("JoinMessage", {
        init: function(name) {
            JoinMessage.$super(this, name);
        }
    });

    var HelloMessage = Named.extendLocal("HelloMessage", {
        init: function(name) {
            HelloMessage.$super(this, name);
        }
    });

    return {
        fromJSON: function(text) {
            return ClassBase.fromJSONString(text);
        },

        createTextMessage: function(name, message) {
            return new TextMessage(name, new Timestamp(), new UserText(message));
        },
        createJoinMessage: function(name) {
            return new JoinMessage(name);
        },
        createHelloMessage: function(name) {
            return new HelloMessage(name);
        }
    };
});