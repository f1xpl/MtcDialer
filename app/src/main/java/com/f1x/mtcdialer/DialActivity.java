package com.f1x.mtcdialer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

/**
 * Created by COMPUTER on 2017-02-17.
 */

public class DialActivity extends BluetoothServiceActivity {
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_transparent);
    }

    @Override
    public void onServiceConnected() {
        final String number = extractPhoneNumber(getIntent());

        if (number != null && !number.isEmpty()) {
            try {
                mBluetoothServiceInterface.dialOut(number);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        DialActivity.this.finish();
    }

    String extractPhoneNumber(Intent intent) {
        Uri uri = intent.getData();

        if(uri != null && uri.getScheme().matches("tel|sms|smsto|mms|mmsto")) {
            return PhoneNumberUtils.normalizeNumber(uri.getSchemeSpecificPart());
        } else {
            return null;
        }
    }

    @Override
    public void onServiceDisconnected() {

    }
}
