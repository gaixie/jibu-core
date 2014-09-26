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

package org.gaixie.jibu.model;

/**
 * 附加查询条件模型类。
 * <p>
 * 用于传递分页，排序等条件信息。</p>
 */
public class Criteria {
    
    private int start;
    private int limit;
    private int total;
    private String dir;
    private String sort;
    private String summary;
    
    /**
     * No-arg constructor.
     */
    public Criteria() {
        
    }

    /**
     * Simple constructor.
     *
     * @param start 分页起始记录数
     * @param limit 每页规定的记录数
     * @param dir 排序方式 ASC 或者 DESC
     * @param sort 排序字段，多个以逗号分隔
     */
    public Criteria(int start, int limit, String dir, String sort) {
        this.start = start;
        this.limit = limit;
        this.dir = dir;
        this.sort = sort;
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Accessor Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~//    
    /**
     * 得到本次分页的起始记录数。
     *
     * @return 起始记录数，如果 {@code <=0 }，并且{@code getLimit() > 0} 
     * 表示首次分页读取 。<br> 
     * 如果 {@code >0 }，并且{@code getLimit() > 0} 表示非首次分页读取。 
     * @see #getLimit()
     */
    public int getStart() { return start; }
    public void setStart(int start) { this.start = start; }

    /**
     * 得到每页读取的记录数。
     *
     * @return 每页需读取的记录数，如果 {@code <= 0 }，非分页查询。
     * @see #getStart()
     */
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    /**
     * 得到分页查询的总记录数。
     *
     * @return 总记录数，每次分页都读取，如果量大，且更新少的表，建议放入 Cache。
     * @see #getStart()
     */
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    /**
     * 得到排序方式。
     *
     * @return ASC 或者 DESC。
     * @see #getSort()
     */
    public String getDir() { return dir; }
    public void setDir(String dir) { this.dir = dir; }

    /**
     * 需要排序的字段。
     *
     * @return 排序字段，可以为多个，以逗号分隔，为 null表示无须排序。
     * @see #getDir()
     */
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    /**
     * 需要合计的字段。
     *
     * @return 合计字段，次数和金额统一以 String 类型返回。
     */
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
