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
package de.dennishoersch.web.chat.db;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Function;

/**
 * @author hoersch
 * 
 */
public class User {
    private final String name;
    private final String passwordHash;

    public User(String name, String password) {
        this.name = name;
        this.passwordHash = DigestUtils.md5Hex(password);
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isPasswordCorrect(String password) {
        return getPasswordHash().equals(DigestUtils.md5Hex(password));
    }

    public static Function<User, String> toName() {
        return UserToName.INSTANCE;
    }

    private enum UserToName implements Function<User, String> {
        INSTANCE;

        @Override
        public String apply(User input) {
            return input.getName();
        }
    }
}
