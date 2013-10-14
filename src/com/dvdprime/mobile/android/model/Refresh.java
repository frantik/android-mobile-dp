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
 * Refresh Model
 * 
 * @author 작은광명
 * 
 */
public class Refresh
{
    /**
     * 종료
     */
    public static final int DONE = -1;
    
    /**
     * 리스트형
     */
    public static final int LIST = 1;
    
    /**
     * 상세형
     */
    public static final int VIEW = 2;
    
    /**
     * 새로고침 종류
     */
    private int type;
    
    // ///////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    // ///////////////////////////////////////////////////////////////////////////////
    public Refresh(int type)
    {
        this.type = type;
    }
    
    // ///////////////////////////////////////////////////////////////////////////////
    //
    // Getter/Setter
    //
    // ///////////////////////////////////////////////////////////////////////////////
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
}
