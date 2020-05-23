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

package io.bootique.jdbc.test.jdbc;

import io.bootique.jdbc.test.*;
import io.bootique.jdbc.test.connector.DbConnector;
import io.bootique.jdbc.test.connector.IdentifierQuotationStrategy;
import io.bootique.jdbc.test.metadata.DbColumnMetadata;
import io.bootique.jdbc.test.metadata.TableFQName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines API to build a SQL
 *
 * @since 0.24
 */
public abstract class StatementBuilder<T extends StatementBuilder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementBuilder.class);

    protected ObjectValueConverter objectValueConverter;
    protected BindingValueToStringConverter valueToStringConverter;
    protected IdentifierQuotationStrategy quoter;
    protected DbConnector channel;

    protected List<Binding> bindings;
    protected StringBuilder sqlBuffer;

    public StatementBuilder(
            DbConnector channel,
            ObjectValueConverter objectValueConverter,
            BindingValueToStringConverter valueToStringConverter,
            IdentifierQuotationStrategy quoter) {

        this.channel = channel;
        this.objectValueConverter = objectValueConverter;
        this.quoter = quoter;
        this.valueToStringConverter = valueToStringConverter;

        this.bindings = new ArrayList<>();
        this.sqlBuffer = new StringBuilder();
    }

    protected void bind(PreparedStatement statement) {
        for (int i = 0; i < bindings.size(); i++) {
            bindings.get(i).bind(statement, i);
        }
    }

    protected void log(String sql, List<Binding> bindings) {

        if (!LOGGER.isInfoEnabled()) {
            return;
        }

        if (bindings.isEmpty()) {
            LOGGER.info(sql);
            return;
        }

        String toLog = bindings
                .stream()
                .map(b -> b.getColumn().getName() + "->" + valueToStringConverter.convert(b.getValue()))
                .collect(Collectors.joining(", ", sql + " [", "]"));

        LOGGER.info(toLog);
    }

    protected String getSql() {
        return sqlBuffer.toString();
    }

    public T append(String sql) {
        sqlBuffer.append(sql);
        return (T) this;
    }

    public T appendTableName(TableFQName name) {

        if (name.hasCatalog()) {
            sqlBuffer.append(quoter.quoted(name.getCatalog())).append(".");
        }

        if (name.hasSchema()) {
            sqlBuffer.append(quoter.quoted(name.getSchema())).append(".");
        }

        sqlBuffer.append(quoter.quoted(name.getTable()));

        return (T) this;
    }

    public T appendIdentifier(String sqlIdentifier) {
        sqlBuffer.append(quoter.quoted(sqlIdentifier));
        return (T) this;
    }

    public T appendBinding(String columnName, int valueType, Object value) {
        return appendBinding(new DbColumnMetadata(columnName, valueType, false, true), value);
    }

    public T appendBinding(DbColumnMetadata column, Object value) {
        sqlBuffer.append("?");
        bindings.add(new Binding(column, objectValueConverter.convert(value)));
        return (T) this;
    }
}
