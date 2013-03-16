/**
 * Copyright 2011-2013 the original author or authors.
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
package io.informant.local.store;

import io.informant.common.Clock;
import io.informant.config.ConfigModule;
import io.informant.config.ConfigService;
import io.informant.core.SnapshotSink;
import io.informant.marker.OnlyUsedByTests;
import io.informant.marker.ThreadSafe;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Ticker;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
@ThreadSafe
public class StorageModule {

    private static final Logger logger = LoggerFactory.getLogger(StorageModule.class);

    private final RollingFile rollingFile;
    private final SnapshotDao snapshotDao;
    private final ScheduledExecutorService scheduledExecutor;

    public StorageModule(ConfigModule configModule, DataSourceModule dataSourceModule)
            throws Exception {
        Ticker ticker = configModule.getTicker();
        Clock clock = configModule.getClock();
        File dataDir = configModule.getDataDir();
        ConfigService configService = configModule.getConfigService();
        int rollingSizeMb = configService.getGeneralConfig().getRollingSizeMb();
        DataSource dataSource = dataSourceModule.getDataSource();

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true)
                .setNameFormat("Informant-StorageBackgroundTasks").build();
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        rollingFile = new RollingFile(new File(dataDir, "informant.rolling.db"),
                rollingSizeMb * 1024, scheduledExecutor, ticker);
        snapshotDao = new SnapshotDao(dataSource, rollingFile, clock);
        new SnapshotReaper(configService, snapshotDao, clock).start(scheduledExecutor);
    }

    public RollingFile getRollingFile() {
        return rollingFile;
    }

    public SnapshotDao getSnapshotDao() {
        return snapshotDao;
    }

    public SnapshotSink getSnapshotSink() {
        return snapshotDao;
    }

    @OnlyUsedByTests
    public void close() {
        logger.debug("close()");
        scheduledExecutor.shutdownNow();
        try {
            rollingFile.close();
        } catch (IOException e) {
            // warning only since it occurs during shutdown anyways
            logger.warn(e.getMessage(), e);
        }
    }
}