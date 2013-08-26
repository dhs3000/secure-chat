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
package de.dennishoersch.web.js;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

import de.dennishoersch.lang.Throwables;

/**
 * 
 * @author hoersch
 */
public final class ScriptCompiler {
    protected static final Logger logger = Logger.getLogger(ScriptCompiler.class);

    protected final Scriptable _scope;

    public ScriptCompiler() {
        this(null);
    }

    public ScriptCompiler(final List<URL> libsToLoad) {
        _scope = doInContext(new Block<Scriptable, IOException>() {
            @Override
            public Scriptable apply(Context context) throws IOException {
                Global global = new Global();
                global.init(context);

                Scriptable scope = context.initStandardObjects(global);
                scope.put("console", scope, Context.toObject(new Console(logger), scope));

                if (libsToLoad != null) {
                    for (URL url : libsToLoad) {
                        InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream());
                        try {
                            context.evaluateReader(scope, inputStreamReader, url.toString(), 1, null);
                        } finally {
                            inputStreamReader.close();
                        }
                    }
                }
                return scope;
            }
        });
    }

    public Function compile(final String function) {
        return new Function(this, doInContext(new Block<org.mozilla.javascript.Function, RuntimeException>() {
            @Override
            public org.mozilla.javascript.Function apply(Context context) throws RuntimeException {
                return context.compileFunction(_scope, function, "function_" + function.hashCode() + ".js", 1, null);
            }
        }));
    }

    Object call(final org.mozilla.javascript.Function function, final Object... args) {
        return doInContext(new Block<Object, RuntimeException>() {
            @Override
            public Object apply(Context context) throws RuntimeException {
                Object result = function.call(context, _scope, null, args);
                return result;
            }
        });

    }

    private <T> T doInContext(Block<T, ? extends Throwable> block) {
        Context cx = Context.enter();
        try {
            cx.setOptimizationLevel(-1);
            cx.setLanguageVersion(Context.VERSION_1_7);

            return block.apply(cx);
        } catch (Throwable e) {
            // if (e instanceof JavaScriptException) {
            // Scriptable value = (Scriptable) ((JavaScriptException) e).getValue();
            // if (value != null && ScriptableObject.hasProperty(value, "message")) {
            // String message = ScriptableObject.getProperty(value, "message").toString();
            // throw new RuntimeException(message, e);
            // }
            // }
            throw Throwables.throwUnchecked(e);
        } finally {
            Context.exit();
        }
    }

    interface Block<T, EX extends Throwable> {
        T apply(Context context) throws EX;
    }

    /**
     * To enable usage of 'console' in scripts.
     * 
     * http://getfirebug.com/wiki/index.php/Console_API
     */
    public static class Console {
        private final Logger _logger;

        Console(final Logger logger) {
            _logger = logger;
        }

        /**
         * @param message
         */
        public void log(Object message) {
            _logger.debug(message);
        }

        /**
         * @param message
         */
        public void info(Object message) {
            _logger.info(message);
        }

        /**
         * @param message
         */
        public void debug(Object message) {
            _logger.debug(message);
        }

        /**
         * @param message
         */
        public void warn(Object message) {
            _logger.warn(message);
        }

        /**
         * @param message
         */
        public void error(Object message) {
            _logger.error(message);
        }

    }
}