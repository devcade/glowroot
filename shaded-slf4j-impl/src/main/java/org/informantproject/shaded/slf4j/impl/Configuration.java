/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.informantproject.shaded.slf4j.impl;

import org.slf4j.LoggerFactory;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
class Configuration {

    private static final boolean useUnshadedSlf4j;

    static {
        useUnshadedSlf4j = tryUnshadedSlf4j();
    }

    static boolean useUnshadedSlf4j() {
        return useUnshadedSlf4j;
    }

    private static boolean tryUnshadedSlf4j() {
        try {
            LoggerFactory.getLogger(Configuration.class);
            org.slf4j.impl.StaticLoggerBinder.getSingleton();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
