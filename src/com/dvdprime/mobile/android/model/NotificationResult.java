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
 * 알림 요청 결과
 * 
 * @author 작은광명
 * 
 */
public class NotificationResult {
    /**
     * 요청 회원 아이디
     */
    private String id;

    /**
     * 페이지
     */
    private int page;

    /**
     * 페이지당 아이템 수
     */
    private int limit;

    /**
     * 최초 요청시간
     */
    private long startTime;

    // //////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    // //////////////////////////////////////////////////////////////
    public NotificationResult() {}

    // //////////////////////////////////////////////////////////////
    //
    // Getter / Setter
    //
    // //////////////////////////////////////////////////////////////
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

}
