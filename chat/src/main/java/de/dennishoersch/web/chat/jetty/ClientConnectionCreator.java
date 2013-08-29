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

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import de.dennishoersch.web.chat.ClientConnection;

/**
 * @author hoersch
 * 
 */
public class ClientConnectionCreator implements WebSocketCreator, BeanFactoryAware {
    private final Logger logger = Logger.getLogger(getClass());

    private BeanFactory beanFactory;

    public ClientConnectionCreator() {
    }

    @Override
    public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp) {
        ClientConnection connection = beanFactory.getBean(ClientConnection.class);
        logger.debug("ClientConnection: " + connection);
        return connection.getWebSocket();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
