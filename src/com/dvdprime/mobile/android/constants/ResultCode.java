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
package com.dvdprime.mobile.android.constants;

/**
 * Network Result Code
 * 
 * @author 작은광명
 * 
 */
public class ResultCode {
    // ////////////////////////////////////////////////////////////////////////////
    //
    // Login Result Code
    //
    // ////////////////////////////////////////////////////////////////////////////
    /** 로그인 성공 */
    public static final String LOGIN_SUCCESS = "L00";

    /** 로그인 요청 */
    public static final String LOGIN_REQUEST = "L01";

    /** 로그인 실패 */
    public static final String LOGIN_FAIL = "L99";

    /** 로그인 서비스 재시도 */
    public static final String LOGIN_RETRY = "L98";

    /** 로그인 정보 삭제 */
    public static final String LOGIN_REMOVE = "L97";

    /** 플리커 정보 삭제 */
    public static final String FLICKR_REMOVE = "L96";

    /** 필터 정보 삭제 */
    public static final String FILTER_REMOVE = "F97";

}
