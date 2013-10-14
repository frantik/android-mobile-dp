/**
 * Copyright 2013 작은광명
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dvdprime.mobile.android.model;

/**
 * Filter Information
 * 
 * @author 작은광명
 * 
 */
public class Filter
{
    /** 회원 아이디 */
    private String targetId;
    
    /** 회원 닉네임 */
    private String targetNick;
    
    /** 체크 여부 */
    private boolean checked;
    
    // ///////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    // ///////////////////////////////////////////////////////////////////////////////
    public Filter(String id, String nick)
    {
        this.targetId = id;
        this.targetNick = nick;
    }

    // ///////////////////////////////////////////////////////////////////////////////
    //
    // Getter/Setter
    //
    // ///////////////////////////////////////////////////////////////////////////////
    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetNick() {
        return targetNick;
    }

    public void setTargetNick(String targetNick) {
        this.targetNick = targetNick;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    public void toggleChecked()
    {
        this.checked = !this.checked;
    }
    
}
