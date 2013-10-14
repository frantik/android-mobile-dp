/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dvdprime.mobile.android.volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyLog;
import com.dvdprime.mobile.android.DpApp;
import com.dvdprime.mobile.android.constants.Config;

public class MultipartRequest extends Request<String> {
    
    private MultipartEntity mEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

    private final Map<String, String> mParams;

    private final Response.Listener<String> mListener;

    public MultipartRequest(String url, Response.Listener<String> listener, ErrorListener errorListener, Map<String, String> params) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mParams = params;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        if (mParams != null && !mParams.isEmpty()) {
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                try {
                    mEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName(Config.EUC_KR)));
                } catch (UnsupportedEncodingException e) {
                    VolleyLog.e("UnsupportedEncodingException");
                }
            }
        }

    }

    @Override
    public String getBodyContentType() {
        return mEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success("Completed", getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.put("Accept", "*/*");
        DpApp.addCookie(headers);

        return headers;
    }
}
