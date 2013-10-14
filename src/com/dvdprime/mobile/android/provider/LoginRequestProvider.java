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
package com.dvdprime.mobile.android.provider;

import static com.dvdprime.mobile.android.util.LogUtil.LOGD;
import static com.dvdprime.mobile.android.util.LogUtil.makeLogTag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;

import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.constants.Config;
import com.dvdprime.mobile.android.constants.ResultCode;
import com.dvdprime.mobile.android.model.Account;
import com.dvdprime.mobile.android.ui.LoginActivity;
import com.dvdprime.mobile.android.util.StringUtil;

/**
 * Login Request to dvdprime.com
 * 
 * @author 작은광명
 * 
 */
public class LoginRequestProvider extends AsyncTask<String, Void, Void> {
    /** TAG */
    private static final String TAG = makeLogTag(LoginRequestProvider.class.getSimpleName());

    @Override
    protected Void doInBackground(String... params) {
        try {
            if (params == null || params.length != 3) {
                return null;
            }

            String param = StringUtil.format("ReturnUrl=http://dp.com&url_home=&id={0}&pw={1}", params[1], params[2]);

            URL url = new URL(Config.LOGIN_URL);
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(false);

                OutputStream os = connection.getOutputStream();
                os.write(param.getBytes(Config.EUC_KR));
                os.flush();
                os.close();

                LOGD(TAG, "request url: " + url.toString());

                StringBuffer cookie = new StringBuffer();
                Map<String, List<String>> m = connection.getHeaderFields();
                if (m.containsKey("Set-Cookie")) {
                    List<String> cookieList = m.get("Set-Cookie");
                    for (String c : cookieList) {
                        cookie.append(StringUtil.substringBefore(c, ";")).append(";");
                    }
                }

                if (connection.getResponseCode() == 200) {
                    StringBuffer sb = new StringBuffer();
                    BufferedReader in = null;

                    in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Config.EUC_KR));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        sb.append(inputLine).append("\n");
                    }

                    // 실패 문자 목록
                    List<String> failList = new ArrayList<String>();
                    failList.add("FAIL");
                    failList.add("image_key");
                    failList.add("history.back");
                    // 실패 문자가 포함되어 있으면 null을 반환
                    if (StringUtil.contains(sb.toString(), failList)) {
                        cookie.setLength(0);
                    }
                }

                // 요청 TAG가 설정이면 바로 설정으로 이벤트를 호출한다.
                if (StringUtil.equals(params[0], makeLogTag(LoginActivity.class.getSimpleName()))) {
                    String resultCode = cookie.length() == 0 ? ResultCode.LOGIN_FAIL : ResultCode.LOGIN_SUCCESS;

                    EventBusProvider.getInstance().post(new Account(params[1], params[2], resultCode));
                    DpApp.setCookies(cookie.toString());
                } else {
                    DpApp.setCookies(cookie.toString());
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

        return null;
    }

}
