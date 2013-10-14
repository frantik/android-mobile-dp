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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.dvdprime.mobile.android.R;
import com.dvdprime.mobile.android.constants.ResultCode;
import com.dvdprime.mobile.android.model.Account;
import com.dvdprime.mobile.android.provider.EventBusProvider;
import com.dvdprime.mobile.android.util.StringUtil;

/**
 * Confirm Dialog
 * 
 * @author 작은광명
 * 
 */
public class CustomConfirmDialog extends DialogFragment {
    public static CustomConfirmDialog newInstance(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", message);
        CustomConfirmDialog frag = new CustomConfirmDialog();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_confirm_dialog, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle(getString(R.string.alert_dialog_alert));
        alertDialogBuilder.setMessage(getArguments().getString("msg", ""));
        alertDialogBuilder.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 설정의 로그인정보 삭제일 경우 삭제 이벤트 전송
                if (StringUtil.equals(getArguments().getString("msg"), getString(R.string.alert_account_delete_message))) {
                    EventBusProvider.getInstance().post(new Account(ResultCode.LOGIN_REMOVE));
                }
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return alertDialogBuilder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
