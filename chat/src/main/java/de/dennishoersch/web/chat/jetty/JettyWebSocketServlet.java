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
package de.dennishoersch.web.chat.jetty;

import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * @author hoersch
 * 
 */
public class JettyWebSocketServlet extends WebSocketServlet {
    private static final long serialVersionUID = 1L;

    private final WebSocketCreator webSocketCreator;

    public JettyWebSocketServlet(WebSocketCreator webSocketCreator) {
        this.webSocketCreator = webSocketCreator;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator(webSocketCreator);
    }
}
