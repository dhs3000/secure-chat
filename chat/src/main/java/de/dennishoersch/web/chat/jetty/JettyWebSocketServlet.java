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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import de.dennishoersch.web.chat.ChatRoom;
import de.dennishoersch.web.chat.Encryption;
import de.dennishoersch.web.util.ServletContextResourceResolver;

/**
 * @author hoersch
 * 
 */
public class JettyWebSocketServlet extends WebSocketServlet {
    private static final long serialVersionUID = 1L;

    private ChatRoom _chatRoom;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContextResourceResolver resourceResolver = new ServletContextResourceResolver(config.getServletContext());
        _chatRoom = new ChatRoom(new Encryption(resourceResolver));
        super.init(config);
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        ClientConnectionCreator creator = new ClientConnectionCreator(_chatRoom);
        factory.setCreator(creator);
    }
}
