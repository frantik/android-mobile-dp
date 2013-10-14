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
package com.dvdprime.mobile.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.ResultCode;
import com.dvdprime.mobile.android.model.Account;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.util.AndroidUtil;

/**
 * Login Dialog
 * 
 * @author 작은광명
 * 
 */
@Deprecated
public class CustomLoginDialog extends Dialog {

    public CustomLoginDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.fragment_login_dialog);
        setTitle(R.string.login_dialog_title);
        setListenerEvent();
    }

    private void setListenerEvent() {
        final EditText mUserName = (EditText) findViewById(R.id.username_edit_text);
        final EditText mPassword = (EditText) findViewById(R.id.password_edit_text);
        final Button mLeftButton = (Button) findViewById(R.id.negative_button);
        final Button mRightButton = (Button) findViewById(R.id.positive_button);

        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserName.getText().length() < 1) {
                    Toast.makeText(getContext(), getContext().getString(R.string.toast_login_id_validation_message), Toast.LENGTH_SHORT).show();
                } else if (mPassword.getText().length() < 1) {
                    Toast.makeText(getContext(), getContext().getString(R.string.toast_login_pw_validation_message), Toast.LENGTH_SHORT).show();
                } else {
                    // 키보드 숨기기
                    AndroidUtil.setKeyboardVisible(getContext(), mUserName, false);
                    // 로그인 요청 전송
                    EventBusProvider.getInstance().post(new Account(mUserName.getText().toString(), mPassword.getText().toString(), ResultCode.LOGIN_REQUEST));
                    dismiss();
                }
            }
        });
    }
}
