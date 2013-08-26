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

/**
 * @author hoersch
 * 
 */
public class Function {

    private final org.mozilla.javascript.Function _function;
    private final ScriptCompiler _compiler;

    Function(ScriptCompiler compiler, org.mozilla.javascript.Function function) {
        _compiler = compiler;
        _function = function;
    }

    public Object call(Object... args) {
        return _compiler.call(_function, args);
    }
}
