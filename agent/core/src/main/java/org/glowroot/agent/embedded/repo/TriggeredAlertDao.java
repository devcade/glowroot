/*
 * Copyright 2015-2017 the original author or authors.
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
package org.glowroot.agent.embedded.repo;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.glowroot.agent.embedded.util.DataSource;
import org.glowroot.agent.embedded.util.ImmutableColumn;
import org.glowroot.agent.embedded.util.ImmutableIndex;
import org.glowroot.agent.embedded.util.Schemas.Column;
import org.glowroot.agent.embedded.util.Schemas.ColumnType;
import org.glowroot.agent.embedded.util.Schemas.Index;
import org.glowroot.common.repo.TriggeredAlertRepository;

class TriggeredAlertDao implements TriggeredAlertRepository {

    private static final ImmutableList<Column> triggeredAlertColumns = ImmutableList.<Column>of(
            ImmutableColumn.of("alert_config_version", ColumnType.VARCHAR));

    private static final ImmutableList<Index> triggeredAlertIndexes = ImmutableList.<Index>of(
            ImmutableIndex.of("triggered_alert_idx", ImmutableList.of("alert_config_version")));

    private final DataSource dataSource;

    TriggeredAlertDao(DataSource dataSource) throws Exception {
        this.dataSource = dataSource;
        dataSource.syncTable("triggered_alert", triggeredAlertColumns);
        dataSource.syncIndexes("triggered_alert", triggeredAlertIndexes);
    }

    @Override
    public void insert(String agentId, String alertConfigVersion) throws Exception {
        dataSource.update("insert into triggered_alert (alert_config_version) values (?)",
                alertConfigVersion);
    }

    @Override
    public boolean exists(String agentId, String alertConfigVersion) throws Exception {
        return dataSource.queryForExists(
                "select 1 from triggered_alert where alert_config_version = ?", alertConfigVersion);
    }

    @Override
    public void delete(String agentId, String alertConfigVersion) throws Exception {
        dataSource.update("delete from triggered_alert where alert_config_version = ?",
                alertConfigVersion);
    }

    @Override
    public List<String> read(String agentId) throws Exception {
        return dataSource.queryForStringList("select alert_config_version from triggered_alert");
    }
}
