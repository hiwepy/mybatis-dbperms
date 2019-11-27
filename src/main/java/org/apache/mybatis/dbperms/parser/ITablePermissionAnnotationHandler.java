/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.mybatis.dbperms.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.plugin.meta.MetaStatementHandler;
import org.apache.mybatis.dbperms.annotation.RequiresPermission;

public interface ITablePermissionAnnotationHandler {

	default boolean match(MetaStatementHandler metaHandler, String tableName) {
		return true;
	}
	
	/**
     * 表名 SQL 处理
     *
     * @param metaHandler 元对象
     * @param originalSQL        当前执行 SQL
     * @param tableName  表名
     * @return
     */
    default String process(MetaStatementHandler metaHandler, String originalSQL, RequiresPermission permission) {
        String permissionedSQL = dynamicPermissionedSQL(metaHandler, permission);
        if (null != permissionedSQL) {
        	Pattern pattern_find = Pattern.compile("(" + permission.table() + ")+");
        	// 匹配所有匹配的表名
    		Matcher matcher = pattern_find.matcher(originalSQL);
    		// 查找匹配的片段
			while (matcher.find()) {
				// 获取匹配的内容
				String full_segment = matcher.group(0);
				// 取得{}内容开始结束位置
				int begain = originalSQL.indexOf(full_segment);
				int end = begain + full_segment.length();
				originalSQL = originalSQL.substring(0, begain) + permissionedSQL + originalSQL.substring(end);
			}
        }
        return originalSQL;
    }

    /**
     * <p>
     * 是否执行 SQL 解析 parser 方法
     * </p>
     *
     * @param metaHandler 元对象
     * @param sql        SQL 语句
     * @return SQL 信息
     */
    default boolean doFilter(final MetaStatementHandler metaHandler, final String sql) {
        // 默认 true 执行 SQL 解析, 可重写实现控制逻辑
        return true;
    }
    
    /**
     * 生成动态表名，无改变返回 NULL
     *
     * @param metaHandler 元对象
     * @param sql        当前执行 SQL
     * @param tableName  表名
     * @return String
     */
    String dynamicPermissionedSQL(MetaStatementHandler metaHandler, RequiresPermission permission);
    
}
