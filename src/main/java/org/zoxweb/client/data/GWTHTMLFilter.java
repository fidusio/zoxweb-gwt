/*
 * Copyright (c) 2012-2017 ZoxWeb.com LLC.
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
package org.zoxweb.client.data;

import com.google.gwt.regexp.shared.RegExp;

public class GWTHTMLFilter {

    public static final String TAG_START =
            "^\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>";
    public static final String TAG_END =
            "\\</\\w+\\>$";
    public static final String TAG_SELG_CLOSING =
            "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>";
    public static final String HTML_ENTITY =
            "&[a-zA-Z][a-zA-Z0-9]+;";


    private GWTHTMLFilter() {

    }

    /**
     * The HTML format pattern.
     */
    public static final RegExp HTML_PATTERN =
            RegExp.compile
                    (
                            "(" + TAG_START + ".*" + TAG_END + ")|(" + TAG_SELG_CLOSING + ")|(" + HTML_ENTITY + ")"
                    );


    /**
     * Checks if given input String is HTML.
     * @param str of type html to be checked
     * @return true is html
     */
    public static boolean isHTML(String str) {
        boolean ret = false;

        if (str != null) {
            ret = HTML_PATTERN.test(str);
        }

        return ret;
    }

}