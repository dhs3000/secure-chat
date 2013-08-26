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
package de.dennishoersch.web.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import de.dennishoersch.lang.Throwables;

/**
 * @author hoersch
 * 
 */
public class ServletContextResourceResolver implements ResourceResolver {
    private static final Logger logger = Logger.getLogger(ServletContextResourceResolver.class);
    private final ServletContext _servletContext;

    public ServletContextResourceResolver(ServletContext servletContext) {
        _servletContext = servletContext;
    }

    @Override
    public URL resolve(String path) {
        try {
            // logger.debug("try resolving '" + path + "' with servletContext '" + _servletContext + "'...");
            // String realPath = _servletContext.getRealPath("/" + path);
            // logger.debug("Resolved to '" + realPath + "'.");
            // return new File(realPath).toURI().toURL();

            URL resource = _servletContext.getResource(path);
            logger.debug("Resolved resource: " + resource);
            return resource;
        } catch (MalformedURLException e) {
            throw Throwables.throwUnchecked(e);
        }
    }
}
