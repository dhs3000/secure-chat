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
package de.dennishoersch.web.websocket.chat.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author hoersch
 * 
 */
public class UserRepository {

    private final Map<String, User> _users = Maps.uniqueIndex(users(), User.toName());

    private static Collection<User> users() {
        return Arrays.asList(new User("dennis", "pwd"), new User("megs", "pwd!"));
    }

    public User findUserByName(String name) {
        return _users.get(name);
    }
}
