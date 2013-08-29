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

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 *
 * @author hoersch
 */
public class DelegatingWebSocketServlet implements Servlet {

    @Autowired
    private Servlet webSocketServlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
        webSocketServlet.init(config);
    }

    @Override
    public void destroy() {
        webSocketServlet.destroy();
    }

    @Override
    public ServletConfig getServletConfig() {
        return webSocketServlet.getServletConfig();
    }

    @Override
    public String getServletInfo() {
        return webSocketServlet.getServletInfo();
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        webSocketServlet.service(request, response);
    }

}
