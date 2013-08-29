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
package de.dennishoersch.web.chat.spring.profiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * @author hoersch
 */
public enum Profiles {
    TOMCAT,

    JETTY;

    private final Logger logger = Logger.getLogger(getClass());

    protected boolean isActive(ConfigurableWebApplicationContext applicationContext) {
        String serverInfo = applicationContext.getServletContext().getServerInfo();
        return serverInfo.toLowerCase().contains(name().toLowerCase());
    }

    public static void addProfiles(ConfigurableWebApplicationContext applicationContext) {
        for (Profiles profile : values()) {
            profile.setIfActive(applicationContext);
        }
    }

    private void setIfActive(ConfigurableWebApplicationContext applicationContext) {
        if (isActive(applicationContext)) {
            logger.debug(String.format("Profile '%s' is active.", this.name()));
            applicationContext.getEnvironment().addActiveProfile(this.name());
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Profile("TOMCAT")
    public @interface Tomcat {
        //
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Profile("JETTY")
    public @interface Jetty {
        //
    }
}
