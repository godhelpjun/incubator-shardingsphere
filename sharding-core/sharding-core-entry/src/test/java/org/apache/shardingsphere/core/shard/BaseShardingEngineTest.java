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

package org.apache.shardingsphere.core.shard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.api.hint.HintManager;
import org.apache.shardingsphere.underlying.common.constant.properties.ShardingSphereProperties;
import org.apache.shardingsphere.underlying.common.constant.properties.PropertiesConstant;
import org.apache.shardingsphere.sql.parser.relation.statement.impl.CommonSQLStatementContext;
import org.apache.shardingsphere.sql.parser.sql.statement.dal.DALStatement;
import org.apache.shardingsphere.underlying.route.RouteUnit;
import org.apache.shardingsphere.core.route.ShardingRouteResult;
import org.apache.shardingsphere.core.route.router.sharding.condition.ShardingCondition;
import org.apache.shardingsphere.core.route.router.sharding.condition.ShardingConditions;
import org.apache.shardingsphere.core.route.type.RoutingResult;
import org.apache.shardingsphere.core.route.type.RoutingUnit;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RequiredArgsConstructor
@Getter
public abstract class BaseShardingEngineTest {
    
    private final String sql;
    
    private final List<Object> parameters;
    
    protected final ShardingSphereProperties getProperties() {
        Properties result = new Properties();
        result.setProperty(PropertiesConstant.SQL_SHOW.getKey(), Boolean.TRUE.toString());
        return new ShardingSphereProperties(result);
    }
    
    protected final ShardingRouteResult createSQLRouteResult() {
        ShardingRouteResult result = new ShardingRouteResult(new CommonSQLStatementContext(new DALStatement()), new ShardingConditions(Collections.<ShardingCondition>emptyList()));
        RoutingResult routingResult = new RoutingResult();
        routingResult.getRoutingUnits().add(new RoutingUnit("ds"));
        result.setRoutingResult(routingResult);
        return result;
    }
    
    protected final void assertSQLRouteResult(final ShardingRouteResult actual) {
        assertThat(actual.getRouteUnits().size(), is(1));
        RouteUnit actualRouteUnit = actual.getRouteUnits().iterator().next();
        assertThat(actualRouteUnit.getDataSourceName(), is("ds"));
        assertThat(actualRouteUnit.getSqlUnit().getSql(), is(sql));
        assertThat(actualRouteUnit.getSqlUnit().getParameters(), is(parameters));
    }
    
    @Test
    public void assertShardWithHintDatabaseShardingOnly() {
        HintManager.getInstance().setDatabaseShardingValue("1");
        assertShard();
        HintManager.clear();
    }
    
    @Test
    public void assertShardWithoutHint() {
        assertShard();
    }
    
    protected abstract void assertShard();
}
