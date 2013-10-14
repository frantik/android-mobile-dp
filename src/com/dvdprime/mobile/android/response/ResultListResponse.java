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
package com.dvdprime.mobile.android.response;

import java.util.List;

/**
 * Result가 포함된 List형 반환 클래스
 * 
 * @author 작은광명
 */
public class ResultListResponse extends DefaultResponse {

    /** 요청 파라미터가 적용된 파라미터 */
    private Object result;

    /** 결과 아이템 수 */
    private int count;

    /** 아이템 목록 */
    private List<?> list;

    // //////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    // //////////////////////////////////////////////////////////////////////////////////
    public ResultListResponse() {

    }

    // //////////////////////////////////////////////////////////////////////////////////
    //
    // Getter/Setter
    //
    // //////////////////////////////////////////////////////////////////////////////////
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

}
