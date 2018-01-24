/**
 * Copyright 2017 Goldman Sachs.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.gs.obevo.db.impl.platforms.mssql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gs.obevo.api.appdata.Change;
import com.gs.obevo.api.platform.ChangeType;
import com.gs.obevo.impl.reader.TextMarkupDocumentReader;
import com.gs.obevo.db.impl.platforms.sqltranslator.PostColumnSqlTranslator;
import com.gs.obevo.db.impl.platforms.sqltranslator.PostParsedSqlTranslator;
import com.gs.obevo.db.sqlparser.syntaxparser.CreateTable;
import com.gs.obevo.db.sqlparser.syntaxparser.CreateTableColumn;

public class MsSqlToHsqlSqlTranslator implements PostColumnSqlTranslator, PostParsedSqlTranslator {
    private final Pattern varbinaryDefaultPattern = Pattern.compile("(?i)varbinary\\s*([^\\(])");

    @Override
    public String handleAnySqlPostTranslation(String string, Change change) {
        if (change != null && change.getMetadataSection() != null
                && change.getMetadataSection().isTogglePresent(TextMarkupDocumentReader.TOGGLE_DISABLE_QUOTED_IDENTIFIERS)) {
            if (!change.getChangeType().getName().equals(ChangeType.VIEW_STR)) {
                // only needed for HSQL seemingly for views only, seemingly not for H2
                string = string.replace('"', '\'');
            }
        }

        Matcher varbinaryDefaultMatcher = this.varbinaryDefaultPattern.matcher(string);
        if (varbinaryDefaultMatcher.find()) {
            string = varbinaryDefaultMatcher.replaceFirst("varbinary(1)" + varbinaryDefaultMatcher.group(1));
        }

        return string;
    }

    @Override
    public String handlePostColumnText(String postColumnText, CreateTableColumn column, CreateTable table) {
        // only for ASE - need "GENERATED BY DEFAULT AS IDENTITY" as the regular IDENTITY is stored as PK
        postColumnText = postColumnText.replaceAll("(?i)\\bidentity\\s*(\\(.*\\))?", "GENERATED BY DEFAULT AS IDENTITY ");

        return postColumnText;
    }
}
