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

D.singleton("PassphraseGenerator", function() {
    'use strict';
    var createRandomizer = function(min, max) {
            return function() {
                return Math.floor(Math.random() * (max - min + 1)) + min;
            };
        },

        symbols = [ "^", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "q", "w", "e", "r", "t", "z", "u", "i", "o", "p", "+", "a", "s", "d", "f", "g", "h", "j", "k", "l", "#", "<", "y", "x", "c",
            "v", "b", "n", "m", ",", ".", "-", "!", "$", "%", "&", "/", "(", ")", "=", "?", "Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P", "*", "A", "S", "D", "F", "G", "H", "J", "K", "L", "'",
            ">", "Y", "X", "C", "V", "B", "N", "M", ";", ":", "_", "@", "~", "|" ],

        defaultLength = 30;

    return {
        generate: function(length) {
            var l = length ? length : defaultLength,
                randomizer = createRandomizer(0, symbols.length - 1),
                pwd = "",
                i = 0,
                current = undefined;
            for (; i < l; i++) {
                current = randomizer();
                pwd += symbols[current];
            }
            return pwd;
        }
    };
});