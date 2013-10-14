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
 * Account Information
 * 
 * @author 작은광명
 * 
 */
public class Account
{
    /** 회원 아이디 */
    private String id;
    
    /** 회원 비밀번호 */
    private String password;
    
    /** 로그인 결과 코드 */
    private String resultCode;
    
    // ///////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    // ///////////////////////////////////////////////////////////////////////////////
    public Account(String id, String password, String resultCode)
    {
        this.id = id;
        this.password = password;
        this.resultCode = resultCode;
    }
    
    public Account(String resultCode)
    {
        this.resultCode = resultCode;
    }
    
    // ///////////////////////////////////////////////////////////////////////////////
    //
    // Getter/Setter
    //
    // ///////////////////////////////////////////////////////////////////////////////
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getResultCode()
    {
        return resultCode;
    }
    
    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    @Override
    public String toString()
    {
        return "Account [id=" + id + ", password=" + password + ", resultCode=" + resultCode + "]";
    }
}
