/**
 * Copyright (C) 2014 Gaixie.ORG.
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

package org.gaixie.jibu.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResouceBundle 工具类。
 *
 */
public class BundleHandler {
    private static final Logger logger = LoggerFactory.getLogger(BundleHandler.class);
    ResourceBundle bundle;

    public BundleHandler(Locale locale) {
        bundle = ResourceBundle.getBundle("i18n/Resources",locale);
    }

    /**
     * 和 ResouceBundle.getString() 方法一样，不过当 key 不存在时，直接返回 key。
     *
     * @param     key ResouceBundle 中的 key
     * @return    ResouceBundle 中 key 对应的值，如果没找到，直接返回 key 本身
     */
    public String get(String key) {
        try { 
            return bundle.getString(key); 
        }catch(MissingResourceException e) { 
            logger.warn("Missing resource.", e);
            return key; 
        }
    }
}
