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

import java.sql.Timestamp;

/**
 * 令牌模型类。
 */
public class Token {

    private Integer id;
    private String value;
    private String type;
    private Timestamp expiration_ts;
    private String send_to;
    private Integer created_by;

    /**
     * No-arg constructor.
     */
    public Token() {

    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Accessor Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Timestamp getExpiration_ts() { return expiration_ts; }
    public void setExpiration_ts(Timestamp expiration_ts) { this.expiration_ts = expiration_ts; }

    public String getSend_to() { return send_to; }
    public void setSend_to(String send_to) { this.send_to = send_to; }

    public Integer getCreated_by() { return created_by; }
    public void setCreated_by(Integer created_by) { this.created_by = created_by; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        final Token token = (Token) o;
        return getValue() == token.getValue();
    }
}
