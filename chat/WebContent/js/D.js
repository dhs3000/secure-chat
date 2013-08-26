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

(function(global) {
    'use strict';

    /**
     * Helper to declare / create packages.
     * @returns the last package (Object)
     */
    var createNamespace = function (packageName) {
            if (typeof (packageName) != "string") {
                throw new Error("Can only declare packages in string format!");
            }
            var packageParts = packageName.split("."),
                currentPackage = global,
                i = 0,
                len = packageParts.length;
            if (packageName.length > 0) {
                for (; i < len; i++) {
                    if (!currentPackage[packageParts[i]]) {
                        currentPackage[packageParts[i]] = {};
                    }
                    currentPackage = currentPackage[packageParts[i]];
                }
            }
            return currentPackage;
        },

        toNamespaceAndClassname = function(type) {
            var i = type.lastIndexOf("."),
                dot1 = (i > 0 ? i : 0),
                dot2 = (i > 0 ? i + 1 : 0),
                n = type.substring(0, dot1),
                c = type.substring(dot2);
            return {
                namespace: n,
                classname: c
            };
        },

        assign = function(typeName, obj) {
            var info = toNamespaceAndClassname(typeName);
            var namespace = createNamespace(info.namespace);
            namespace[info.classname] = obj;
        },

        SUPER_OBJECT = function() {
        },
        localClassCounter = 0,
        extendInternal = function() {
            var parentClass,
                typeName,
                constructorAndMethods,
                args = Array.prototype.slice.call(arguments, 0);

            if (args.length === 2) {
                parentClass = (this == D ? SUPER_OBJECT : this);
                typeName = args[0];
                constructorAndMethods = args[1];
            } else if (args.length === 3) {
                parentClass = args[0];
                typeName = args[1];
                constructorAndMethods = args[2];
            } else {
                throw "Usage: extend(parentClass, typeName, constructorAndMethods) or extend(typeName, constructorAndMethods) got " + args.length + " Arguments!";
            }

            if (!parentClass) {
                throw "parentClass is required!";
            }

            if (!typeName) {
                throw "Type name is required!";
            }
            if (!constructorAndMethods) {
                throw "Init constructor and methods for new type are required!";
            }
            if (!constructorAndMethods.init) {
                throw "Constructor (init-function) for new type is required!";
            }

            var subclass = constructorAndMethods.init;

            // create prototype of subclass without invoking the parents constructor
            subclass.prototype = Object.create(parentClass.prototype);
            subclass.prototype.constructor = subclass;

            // Enable call to super constructor: CLASS_NAME.$super(this, ...arguments);
            var superPrototype  = parentClass.prototype;
            subclass.$super = function(childThis) {
                var args = Array.prototype.slice.call(arguments, 1);
                superPrototype.constructor.apply(childThis, args);
            };

            // Copy this method so that it is available in the new Class
            subclass.extend = D.extend;
            subclass.extendLocal = D.extendLocal;

            // Make subclass and its objects aware of its name
            subclass["type"] = function() {
                return typeName;
            };
            subclass.prototype["type"] = function() {
                return typeName;
            };

            for (var m in constructorAndMethods) {
                if (m !== "init" && constructorAndMethods.hasOwnProperty(m)) {
                    subclass.prototype[m] = constructorAndMethods[m];
                }
            }

            return subclass;
        },
        D = createNamespace("D");

    /**
     * Creates an object (or function) with the given creator function.
     *
     * Usage:
     * var myObject = D.scoped(function() {
     *      var scopedVar = ...;
     *      return function(...) {
     *          ... use scopedVar ...
     *      };
     * });
     *
     */
    D.scoped = function(creator) {
        return creator();
    };

    /**
     * Creates a singleton instance with the given creator function and assigns it to to name.
     * Name might include the namespace.
     * Usage:
     * D.singleton('de.dennishoersch.MySingletonObject', function() {
     *     return {
     *          publicMethod: function() {
     *              ...
     *          }
     *     };
     * });
     */
    D.singleton = function(name, objectOrCreator) {
        var obj;
        if (typeof(objectOrCreator) == 'function') {
            obj = objectOrCreator();
        } else {
            obj = objectOrCreator;
        }
        assign(name, obj);
    };

    /**
     * Creates a new subclass by extending a parent class.
     * If parent is absent uses Object (or this if used as method at already extended class).
     * 'init' is the constructor of the new class.
     *
     * D.extend("bla.blub.Fancy", {
     *     init: function(myArgument) {
     *         bla.blub.Fancy.$super(this, ...);
     *         this.myvar = myArgument;
     *     }
     * });
     *
     * var o = new bla.blub.Fancy("value");
     *
     * Two ways:
     * D.extend(bla.blub.Fancy, "bla.blub.Sub1", {
     *     init: function(myArgument) {
     *         bla.blub.Sub1.$super(this, myArgument);
     *     }
     * });
     *
     * or:
     *
     * bla.blub.Fancy.extend("bla.blub.Sub2", {
     *     init: function(myArgument) {
     *         bla.blub.Sub2.$super(this, myArgument);
     *     }
     * });
     *
     * var s1 = new bla.blub.Sub1();
     * var s2 = new bla.blub.Sub2();
     *
     * @returns the new subclass
     */
    D.extend = function() {
        var args = Array.prototype.slice.call(arguments, 0),
            subclass = extendInternal.apply(this, args);

        assign(subclass.type(), subclass);

        return subclass;
    };
    SUPER_OBJECT.extend = D.extend;

    /**
     * In opposite to extend() this one does not declare the class in the global namespace.
     * It is not necessary to have an type name here. It makes the local class type names unique. Without type name the get an internal name.
     *
     */
    D.extendLocal = function() {
        var typeName,
            args = Array.prototype.slice.call(arguments, 0),
            args_;
        if (args.length === 1) {
            typeName = "__LOCAL_CLASS__";
            args_ = [];
            args_[0] = typeName + "_" + localClassCounter++;
            args_[1] = args[0];
            args = args_;
        } else if (args.length === 2) {
            typeName = args[0];
            args[0] = typeName + "_" + localClassCounter++;
        } else if (args.length === 3) {
            typeName = args[1];
            args[1] = typeName + "_" + localClassCounter++;
        } else {
            throw "Usage: extendLocal(parentClass, typeName, constructorAndMethods) or extendLocal(typeName, constructorAndMethods) or extendLocal(constructorAndMethods) got " + args.length + " Arguments!";
        }
        return extendInternal.apply(this, args);
    };
    SUPER_OBJECT.extendLocal = D.extendLocal;

    /**
     * Extracts the names of the parameter of the given function as String Array.
     */
    D.functionParameterNamesOf = D.scoped(function() {
        // http://stackoverflow.com/questions/1007981/how-to-get-function-parameter-names-values-dynamically-from-javascript
        var STRIP_COMMENTS = /((\/\/.*$)|(\/\*[\s\S]*?\*\/))/mg,
            FN_ARGS = /^function\s*[^\(]*\(\s*([^\)]*)\)/m,
            FN_ARG_SPLIT = /,/,
            FN_ARG = /^\s*(.*)\s*$/;

        return function(fn) {
            if (!(typeof fn == 'function')) {
                throw "Argument must be a function!";
            }

            var parameterNames = [],
                fnText = fn.toString().replace(STRIP_COMMENTS, ''),
                argDecl = fnText.match(FN_ARGS),
                list = argDecl[1].split(FN_ARG_SPLIT),
                i = 0,
                l = list.length;

            for (; i < l; i++) {
                list[i].replace(FN_ARG, function(all, name) {
                    parameterNames.push(name);
                });
            }

            return parameterNames;
        };
    });

    return D;
}(window));

