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

import javax.servlet.Servlet;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import de.dennishoersch.web.chat.spring.profiles.Profiles;

@Configuration
@Profiles.Jetty
public class JettyConfiguration {
    private final Logger logger = Logger.getLogger(getClass());

    @Bean
    public WebSocketCreator clientConnectionCreator() {
        return new ClientConnectionCreator();
    }

    @Bean
    public Servlet webSocketServlet() {
        return new JettyWebSocketServlet(clientConnectionCreator());
    }

    @Bean
    @Scope("prototype")
    public JettyWebSocket webSocket() {
        logger.debug("Created new JettyWebSocket");
        return new JettyWebSocket();
    }

}
