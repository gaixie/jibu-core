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
 * 用户模型类。
 */
public class User {

    private Integer id;
    private String fullname;
    private String username;
    private String password;
    private Integer type;
    private String emailaddress;
    private Timestamp registered_ts;
    private Integer invited_by;
    private Boolean enabled;

    /**
     * No-arg constructor.
     */
    public User() {

    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Accessor Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmailaddress() { return emailaddress; }
    public void setEmailaddress(String emailaddress) { this.emailaddress = emailaddress; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public Boolean getEnabled() { return enabled; }

    public Timestamp getRegistered_ts() { return registered_ts; }
    public void setRegistered_ts(Timestamp registered_ts) { this.registered_ts = registered_ts; }

    public Integer getInvited_by() { return invited_by; }
    public void setInvited_by(Integer invited_by) { this.invited_by = invited_by; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        final User user = (User) o;
        return getUsername().equals(user.getUsername());
    }

    public int hashCode() {
        return getUsername().hashCode();
    }
}
