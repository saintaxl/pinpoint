/*
 * Copyright 2014 NAVER Corp.
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

package com.navercorp.pinpoint.plugin.jdbc.hana;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.navercorp.pinpoint.bootstrap.context.DatabaseInfo;

/**
 * @author emeroad
 */
public class HanaUrlParserTest {

    private Logger logger = LoggerFactory.getLogger(HanaUrlParserTest.class);
    private HanaJdbcUrlParser jdbcUrlParser = new HanaJdbcUrlParser();

    //@Test
    public void testURIParse() throws Exception {

        URI uri = URI.create("jdbc:mysql:replication://10.98.133.22:3306/test_lucy_db");
        logger.debug(uri.toString());
        logger.debug(uri.getScheme());

        // URI parsing has limitation.
        try {
            URI oracleRac = URI.create("jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=on)" +
                    "(ADDRESS=(PROTOCOL=TCP)(HOST=1.2.3.4) (PORT=1521))" +
                    "(ADDRESS=(PROTOCOL=TCP)(HOST=1.2.3.5) (PORT=1521))" +
                    "(CONNECT_DATA=(SERVICE_NAME=service)))");

            logger.debug(oracleRac.toString());
            logger.debug(oracleRac.getScheme());
            Assert.fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void mysqlParse1() {

        DatabaseInfo dbInfo = jdbcUrlParser.parse("jdbc:sap://10.58.108.180:30215?reconnect=true&currentschema=JOBDB8");
        Assert.assertEquals(dbInfo.getType(), HanaConstants.HANA);
        Assert.assertEquals(dbInfo.getHost().get(0), ("10.58.108.180:30215"));
        Assert.assertEquals(dbInfo.getDatabaseId(), "JOBDB8");
        Assert.assertEquals(dbInfo.getUrl(), "jdbc:sap://10.58.108.180:30215");
        System.out.print(dbInfo);
    }

    //@Test
    public void mysqlParse2() {

        DatabaseInfo dbInfo = jdbcUrlParser.parse("jdbc:mysql://10.98.133.22:3306/test_lucy_db");
        Assert.assertEquals(dbInfo.getType(), HanaConstants.HANA);
        Assert.assertEquals(dbInfo.getHost().get(0), "10.98.133.22:3306");

        Assert.assertEquals(dbInfo.getDatabaseId(), "test_lucy_db");
        Assert.assertEquals(dbInfo.getUrl(), "jdbc:mysql://10.98.133.22:3306/test_lucy_db");
        logger.info(dbInfo.toString());
        logger.info(dbInfo.getMultipleHost());
    }

    //@Test
    public void mysqlParse3() {
        DatabaseInfo dbInfo = jdbcUrlParser.parse("jdbc:mysql://61.74.71.31/log?useUnicode=yes&amp;characterEncoding=UTF-8");
        Assert.assertEquals(dbInfo.getType(), HanaConstants.HANA);
        Assert.assertEquals(dbInfo.getHost().get(0), "61.74.71.31");
        Assert.assertEquals(dbInfo.getDatabaseId(), "log");
        Assert.assertEquals(dbInfo.getUrl(), "jdbc:mysql://61.74.71.31/log");
        logger.info(dbInfo.toString());
    }

    //@Test
    public void mysqlParseCookierunMaster() {
        DatabaseInfo dbInfo = jdbcUrlParser.parse("jdbc:mysql://10.115.8.209:5605/db_cookierun?useUnicode=true&characterEncoding=UTF-8&noAccessToProcedureBodies=true&autoDeserialize=true&elideSetAutoCommits=true&sessionVariables=time_zone='%2B09:00',tx_isolation='READ-COMMITTED'");
        Assert.assertEquals(dbInfo.getType(), HanaConstants.HANA);
        Assert.assertEquals(dbInfo.getHost().get(0), "10.115.8.209:5605");
        Assert.assertEquals(dbInfo.getDatabaseId(), "db_cookierun");
        Assert.assertEquals(dbInfo.getUrl(), "jdbc:mysql://10.115.8.209:5605/db_cookierun");
        logger.info(dbInfo.toString());
    }


    //@Test
    public void mysqlParseCookierunSlave() {
        DatabaseInfo dbInfo = jdbcUrlParser.parse("jdbc:mysql:loadbalance://10.118.222.35:5605/db_cookierun?useUnicode=true&characterEncoding=UTF-8&noAccessToProcedureBodies=true&autoDeserialize=true&elideSetAutoCommits=true&sessionVariables=time_zone='%2B09:00',tx_isolation='READ-UNCOMMITTED'");
        Assert.assertEquals(dbInfo.getType(), HanaConstants.HANA);
        Assert.assertEquals(dbInfo.getHost().get(0), "10.118.222.35:5605");
        Assert.assertEquals(dbInfo.getDatabaseId(), "db_cookierun");
        Assert.assertEquals(dbInfo.getUrl(), "jdbc:mysql:loadbalance://10.118.222.35:5605/db_cookierun");
        logger.info(dbInfo.toString());
    }

    //@Test
    public void mysqlParseCookierunSlave2() {
        DatabaseInfo dbInfo = jdbcUrlParser.parse("jdbc:mysql:loadbalance://10.118.222.35:5605,10.118.222.36:5605/db_cookierun?useUnicode=true&characterEncoding=UTF-8&noAccessToProcedureBodies=true&autoDeserialize=true&elideSetAutoCommits=true&sessionVariables=time_zone='%2B09:00',tx_isolation='READ-UNCOMMITTED'");
        Assert.assertEquals(dbInfo.getType(), HanaConstants.HANA);
        Assert.assertEquals(dbInfo.getHost().get(0), "10.118.222.35:5605");
        Assert.assertEquals(dbInfo.getHost().get(1), "10.118.222.36:5605");
        Assert.assertEquals(dbInfo.getDatabaseId(), "db_cookierun");
        Assert.assertEquals(dbInfo.getUrl(), "jdbc:mysql:loadbalance://10.118.222.35:5605,10.118.222.36:5605/db_cookierun");
        logger.info(dbInfo.toString());
    }
}
