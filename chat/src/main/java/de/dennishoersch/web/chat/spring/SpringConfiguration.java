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
package de.dennishoersch.web.chat.spring;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import de.dennishoersch.web.chat.ChatRoom;
import de.dennishoersch.web.chat.ClientConnection;
import de.dennishoersch.web.chat.ClientConnectionImpl;
import de.dennishoersch.web.chat.Encryption;
import de.dennishoersch.web.chat.WebSocket;
import de.dennishoersch.web.chat.db.UserRepository;
import de.dennishoersch.web.chat.db.UserRepositoryImpl;
import de.dennishoersch.web.chat.jetty.JettyConfiguration;
import de.dennishoersch.web.chat.tomcat.TomcatConfiguration;
import de.dennishoersch.web.util.ServletContextResourceResolver;

@Configuration
@ComponentScan(basePackages = "de.dennishoersch.web")
@Import({
        TomcatConfiguration.class, JettyConfiguration.class
})
public class SpringConfiguration {
    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ServletContext servletContext;

    @Bean
    public ChatRoom chatRoom() {
        return new ChatRoom();
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryImpl();
    }

    @Bean
    public Encryption encryption() {
        ServletContextResourceResolver resourceResolver = new ServletContextResourceResolver(servletContext);
        return new Encryption(resourceResolver);
    }

    @Bean
    @Scope("prototype")
    public ClientConnection clientConnection(WebSocket webSocket) {
        ClientConnectionImpl clientConnection = new ClientConnectionImpl(chatRoom(), userRepository(), webSocket, encryption());
        webSocket.setClientConnection(clientConnection);
        logger.debug("Created client connection with " + webSocket);
        return clientConnection;
    }

}
