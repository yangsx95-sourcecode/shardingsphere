/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.database.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global data source registry.
 * 全局的数据源注册器，是一个单例的对象
 * 内部有缓存的数据源map以及数据库表map
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class GlobalDataSourceRegistry {
    
    private static final GlobalDataSourceRegistry INSTANCE = new GlobalDataSourceRegistry();

    /**
     * key 为数据源名称，value 为数据源对象
     */
    private final Map<String, DataSource> cachedDataSources = new ConcurrentHashMap<>();

    /**
     * key 为标明，value位数据库的名字
     */
    private final Map<String, String> cachedDatabaseTables = new ConcurrentHashMap<>();
    
    /**
     * Get instance of global data source registry.
     *
     * @return got instance
     */
    public static GlobalDataSourceRegistry getInstance() {
        return INSTANCE;
    }
}
