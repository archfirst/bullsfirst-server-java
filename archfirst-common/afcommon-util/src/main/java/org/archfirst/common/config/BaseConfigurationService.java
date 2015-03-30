/**
 * Copyright 2010 Archfirst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archfirst.common.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseConfig
 *
 * @author Naresh Bhatia
 */
public abstract class BaseConfigurationService implements ConfigurationService {
    private static final Logger logger =
        LoggerFactory.getLogger(BaseConfigurationService.class);
    
    private final CompositeConfiguration config;
    
    protected BaseConfigurationService() {
        this.config = new CompositeConfiguration();
    }
    
    protected void load(String filename) {
        PropertiesConfiguration propertiesConfiguration =
            new PropertiesConfiguration();
        try {
            propertiesConfiguration.load(filename);
            config.addConfiguration(propertiesConfiguration);
        }
        catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
        logger.info("Loaded {}", filename);
    }

    @Override
    public String getString(String key) {
        String value = config.getString(key);
        if (value==null) {
            logger.warn("Property {} is not defined", key);
        }
        return value;
    }
}