/*
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.openjpa.jdbc.sql;

import org.apache.openjpa.jdbc.kernel.exps.FilterValue;

/**
 * Base dictionary for the IBM DB2 family of databases.
 */
public abstract class AbstractDB2Dictionary
    extends DBDictionary {

    public AbstractDB2Dictionary() {
        numericTypeName = "DOUBLE";
        bitTypeName = "SMALLINT";
        smallintTypeName = "SMALLINT";
        tinyintTypeName = "SMALLINT";
        longVarbinaryTypeName = "BLOB";
        varbinaryTypeName = "BLOB";

        toUpperCaseFunction = "UPPER(CAST({0} AS VARCHAR(1000)))";
        toLowerCaseFunction = "LOWER(CAST({0} AS VARCHAR(1000)))";
        stringLengthFunction = "LENGTH(CAST({0} AS VARCHAR(1000)))";

        trimLeadingFunction = "LTRIM({0})";
        trimTrailingFunction = "RTRIM({0})";
        trimBothFunction = "LTRIM(RTRIM({0}))";

        // in DB2, "for update" seems to be ignored with isolation
        // levels below REPEATABLE_READ... force isolation to behave like RR
        forUpdateClause = "FOR UPDATE WITH RR";

        supportsLockingWithDistinctClause = false;
        supportsLockingWithMultipleTables = false;
        supportsLockingWithOrderClause = false;
        supportsLockingWithOuterJoin = false;
        supportsLockingWithInnerJoin = false;
        supportsLockingWithSelectRange = false;

        requiresAutoCommitForMetaData = true;
        requiresAliasForSubselect = true;

        supportsAutoAssign = true;
        autoAssignClause = "GENERATED BY DEFAULT AS IDENTITY";
        lastGeneratedKeyQuery = "VALUES(IDENTITY_VAL_LOCAL())";

        // DB2 doesn't understand "X CROSS JOIN Y", but it does understand
        // the equivalent "X JOIN Y ON 1 = 1"
        crossJoinClause = "JOIN";
        requiresConditionForCrossJoin = true;
    }

    public void indexOf(SQLBuffer buf, FilterValue str, FilterValue find,
        FilterValue start) {
        buf.append("(LOCATE(CAST((");
        find.appendTo(buf);
        buf.append(") AS VARCHAR(1000)), CAST((");
        str.appendTo(buf);
        buf.append(") AS VARCHAR(1000))");
        if (start != null) {
            buf.append(", CAST((");
            start.appendTo(buf);
            buf.append(") AS INTEGER) + 1");
        }
        buf.append(") - 1)");
    }

    public void substring(SQLBuffer buf, FilterValue str, FilterValue start,
        FilterValue end) {
        buf.append("SUBSTR(CAST((");
        str.appendTo(buf);
        buf.append(") AS VARCHAR(1000)), CAST((");
        start.appendTo(buf);
        buf.append(") AS INTEGER) + 1");
        if (end != null) {
            buf.append(", CAST((");
            end.appendTo(buf);
            buf.append(") AS INTEGER) - CAST((");
            start.appendTo(buf);
            buf.append(") AS INTEGER)");
        }
        buf.append(")");
    }
}
