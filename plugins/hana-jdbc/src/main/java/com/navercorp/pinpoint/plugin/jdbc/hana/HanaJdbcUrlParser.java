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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.navercorp.pinpoint.bootstrap.context.DatabaseInfo;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.DefaultDatabaseInfo;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcUrlParser;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.StringMaker;

/**
 * @author emeroad
 */
public class HanaJdbcUrlParser extends JdbcUrlParser {

    @Override
    public DatabaseInfo doParse(String url) {
        if (isLoadbalanceUrl(url)) {
            return parseLoadbalancedUrl(url);
        }
        return parseNormal(url);
    }

    private DatabaseInfo parseLoadbalancedUrl(String url) {
        // jdbc:sap://1.2.3.4:5678/test_db
        StringMaker maker = new StringMaker(url);
        maker.after("jdbc:sap:");
        // 1.2.3.4:5678 In case of replication driver could have multiple values
        // We have to consider mm db too.
        String host = maker.after("//").before('/').value();

        // Decided not to cache regex. This is not invoked often so don't waste memory.
        String[] parsedHost = host.split(",");
        List<String> hostList = Arrays.asList(parsedHost);


        String databaseId = maker.next().afterLast('/').before('?').value();
        String normalizedUrl = maker.clear().before('?').value();
        DatabaseInfo info = new DefaultDatabaseInfo(HanaConstants.HANA, HanaConstants.HANA_EXECUTE_QUERY, url, normalizedUrl, hostList, databaseId);
        return info;
    }

    private boolean isLoadbalanceUrl(String url) {
        return false;
    }

    private DatabaseInfo parseNormal(String url) {
        StringMaker maker = new StringMaker(url);
        maker.after("jdbc:hana:");
        // 1.2.3.4:5678 In case of replication driver could have multiple values
        // We have to consider mm db too.
        String host = maker.after("//").before('?').value();
        List<String> hostList = new ArrayList<String>(1);
        hostList.add(host);
        // String port = maker.next().after(':').before('/').value();
        String queryString = maker.next().afterLast('?').value();
        String[] params = queryString.split("[&]");
        String databaseId = "";
        for(String param : params){
            if(param.trim().startsWith("currentschema")){
                databaseId = param.trim().split("=")[1];
            }
        }
        String normalizedUrl = maker.clear().before('?').value();
        return new DefaultDatabaseInfo(HanaConstants.HANA, HanaConstants.HANA_EXECUTE_QUERY, url, normalizedUrl, hostList, databaseId);
    }
}