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
package io.informant.plugin.servlet;

import io.informant.api.UnresolvedMethod;
import io.informant.shaded.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.Enumeration;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
// HttpSession wrapper does not make assumptions about the @Nullable properties of the underlying
// javax.servlet.http.HttpSession since it's just an interface and could theoretically return null
// even where it seems to not make sense
@ThreadSafe
class HttpSession {

    private static final UnresolvedMethod getIdMethod = UnresolvedMethod.from(
            "javax.servlet.http.HttpSession", "getId");
    private static final UnresolvedMethod isNewMethod = UnresolvedMethod.from(
            "javax.servlet.http.HttpSession", "isNew");
    private static final UnresolvedMethod getAttributeNamesMethod = UnresolvedMethod.from(
            "javax.servlet.http.HttpSession", "getAttributeNames");
    private static final UnresolvedMethod getAttributeMethod = UnresolvedMethod.from(
            "javax.servlet.http.HttpSession", "getAttribute", String.class);

    private final Object realSession;

    static HttpSession from(Object realSession) {
        return new HttpSession(realSession);
    }

    private HttpSession(Object realSession) {
        this.realSession = realSession;
    }

    String getId() {
        String id = getIdMethod.invokeWithDefaultOnError(realSession, "");
        if (id == null) {
            return "";
        } else {
            return id;
        }
    }

    boolean isNew() {
        return isNewMethod.invokeWithDefaultOnError(realSession, false);
    }

    Enumeration<?> getAttributeNames() {
        Enumeration<?> attributeNames = (Enumeration<?>) getAttributeNamesMethod
                .invokeWithDefaultOnError(realSession, null);
        if (attributeNames == null) {
            return Collections.enumeration(ImmutableSet.of());
        } else {
            return attributeNames;
        }
    }

    @Nullable
    Object getAttribute(String name) {
        return getAttributeMethod.invokeWithDefaultOnError(realSession, name,
                "<error calling HttpSession.getAttribute()>");
    }
}