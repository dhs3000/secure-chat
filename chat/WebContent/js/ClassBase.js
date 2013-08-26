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
 * Base class that enables easily serializing objects with JSON and deserializing into the right objects with the right class.
 *
 * Adds isType() methods if a type name was given.
 *
 * If no type name is given JSON might not work across different clients.
 *
 */
D.scoped(function() {
    // Helper to call 'new' on a given constructor and give the argument array to it
    // http://stackoverflow.com/questions/3362471/how-can-i-call-a-javascript-constructor-using-call-or-apply
    var newObjectOf = function(constructor, args) {
            var instance = Object.create(constructor.prototype);
            var result = constructor.apply(instance, args);
            return result !== undefined ? result : instance;
        },
        CLASS_BASE_MAKER = "CLASS_BASE",
        knownClassTypes = {},
        extendInternal = function(extender) {
            var args = Array.prototype.slice.call(arguments, 1),
                typeName,
                hasOwnTypeName = false,
                subclass = extender.apply(this, args);
            if (args.length <= 1) {
                typeName = subclass.type();
            } else {
                typeName = args[0];
                hasOwnTypeName = true;
            }

            subclass.extend = ClassBase.extend;
            subclass.extendLocal = ClassBase.extendLocal;
            if (hasOwnTypeName) {
                // Create isType helpers to easily distinguish between subclasses
                this.prototype["is" + typeName] = function() {
                    return false;
                };
                subclass.prototype["is" + typeName] = function() {
                    return true;
                };
            }

            if (knownClassTypes[typeName]) {
                throw "Type names must be unique! '" + typeName + "' is already used.";
            }

            subclass.prototype.toString = function() {
                var result = this.type() + "(",
                    first = true;
                for (var attr in this) {
                    if(this.hasOwnProperty(attr)) {
                        if (!first) {
                            result += ", ";
                        }
                        result += attr + "=" + this[attr];
                        first = false;
                    }
                }
                return result + ")";
            };

            // Save all new types, so that from JSON can create the right objects
            knownClassTypes[typeName] = subclass;

            subclass.prototype.toJSON = function() {
                var data = {};
                for (var property in this) {
                    if (this.hasOwnProperty(property)) {
                        data[property] = this[property];
                    }
                }
                var result = {
                    CLASS_BASE: CLASS_BASE_MAKER,
                    type : typeName,
                    data : data
                };
                return result;
            };

            subclass.prototype.toJSONString = function() {
                var result = this.toJSON();
                return JSON.stringify(result, function(key, value) {
                    if (value instanceof ClassBase) {
                        return value.toJSON();
                    }
                    return value;
                });
            };

            var constructorArgumentNames = D.functionParameterNamesOf(subclass);
            subclass.fromJSON = function(j) {
                var i = 0,
                    l = constructorArgumentNames.length,
                    name,
                    value,
                    values = [];
                for (; i < l; i++) {
                    name = constructorArgumentNames[i];
                    // If this json-part represents a ClassBaseObject, convert it to its real object
                    value = ClassBase.fromJSON(j[name]);
                    values.push(value);
                }
                return newObjectOf(subclass, values);
            };

            return subclass;
        };

    D.extend("ClassBase", {
            init: function() {
            }
       }
    );

    ClassBase.fromJSON = function(j) {
        var classbaseMarker = j[CLASS_BASE_MAKER],
            type = j.type,
            data = j.data;
        if (classbaseMarker === CLASS_BASE_MAKER && type && data) {
            var newClass = knownClassTypes[type];
            if (newClass.fromJSON) {
                return newClass.fromJSON(data);
            }
        }
        return j;
    };

    ClassBase.fromJSONString = function(text) {
        var j = JSON.parse(text);
        return ClassBase.fromJSON(j);
    };
    ClassBase.extend = function() {
        var args = Array.prototype.slice.call(arguments, 0);
        return extendInternal.apply(this, [D.extend].concat(args));
    };
    ClassBase.extendLocal = function() {
        var args = Array.prototype.slice.call(arguments, 0);
        return extendInternal.apply(this, [D.extendLocal].concat(args));
    };
});


