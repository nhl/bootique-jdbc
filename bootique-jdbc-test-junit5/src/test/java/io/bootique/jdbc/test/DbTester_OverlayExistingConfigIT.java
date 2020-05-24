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
package io.bootique.jdbc.test;

import io.bootique.BQCoreModule;
import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.junit5.BQApp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbTester_OverlayExistingConfigIT extends BaseJdbcTesterTest {

    @RegisterExtension
    static final DbTester db = DbTester.derbyDb();

    @BQApp(skipRun = true)
    static final BQRuntime app = Bootique.app()
            .autoLoadModules()
            .module(db.setOrReplaceDataSource("myDS"))
            .module(b -> BQCoreModule.extend(b).setProperty("bq.jdbc.myDS.jdbcUrl", "test"))
            .createRuntime();

    @Test
    @Order(0)
    @DisplayName("Existing DataSource config properties must be ignored")
    public void testDerby() {
        // assertion details are irrelevant here... We just need to make sure the DB started
        run(app, c -> assertEquals("Apache Derby", c.getMetaData().getDatabaseProductName()));
    }
}
