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
package com.dvdprime.mobile.android.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Multimedia Util
 * 
 * @author 작은광명
 * 
 */
public class MultimediaUtil {
    
    private static List<String> multiMediaLists = new ArrayList<String>();

    static {
        multiMediaLists.add("youtube.com");
        multiMediaLists.add("videofarm.daum.net");
        multiMediaLists.add("player.vimeo.com");
        multiMediaLists.add("pandora.tv");
        multiMediaLists.add("nate.com");
    }

    /**
     * 멀티미디어 파일 형식인지 확인
     * 
     * @param url
     *            확인할 URL
     * @return
     */
    public static boolean isMultimediaType(String url) {
        return url != null && StringUtil.contains(url, multiMediaLists);
    }
}
