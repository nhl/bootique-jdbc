/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.jdbc.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.bootique.jdbc.managed.ManagedDataSourceFactoryProxy;

import java.io.IOException;

/**
 * Custom Jackson deserializer for {@link ManagedDataSourceFactoryProxy}.
 *
 * @since 0.25
 */
public class ManagedDataSourceFactoryProxyDeserializer extends StdDeserializer<ManagedDataSourceFactoryProxy> {

    protected ManagedDataSourceFactoryProxyDeserializer() {
        super(ManagedDataSourceFactoryProxy.class);
    }

    @Override
    public ManagedDataSourceFactoryProxy deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        // TODO: how do we pass a "type" value down to ManagedDataSourceFactoryProxy? It is stripped from JsonNode by
        // Jackson, so we can't distinguish missing "type" from invalid.

        JsonNode node = p.readValueAsTree();

        return new ManagedDataSourceFactoryProxy(node);
    }
}
