package com.f1x.mtcdialer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by COMPUTER on 2017-02-18.
 */

public abstract class PhoneBookReceiver extends BroadcastReceiver {
    public PhoneBookReceiver() {
        mRegistered = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BLUETOOTH_REPORT_ACTION)) {
            if (intent.hasExtra(PHONEBOOK_RECORD_EXTRA)) {
                onPhoneBookRecordFetched(intent.getStringExtra(PHONEBOOK_RECORD_EXTRA));
            } else if (intent.hasExtra(PHONEBOOK_SYNC_END_EXTRA)) {
                onPhoneBookFetchFinished();
            }
        }
    }

    public void register(Context context) {
        if(!mRegistered) {
            mRegistered = true;

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BLUETOOTH_REPORT_ACTION);

            context.registerReceiver(this, intentFilter);
        }
    }

    public void unregister(Context context) {
        if(mRegistered) {
            mRegistered = false;
            context.unregisterReceiver(this);
        }
    }

    public abstract void onPhoneBookRecordFetched(String record);
    public abstract void onPhoneBookFetchFinished();

    private boolean mRegistered;

    private final String BLUETOOTH_REPORT_ACTION = "com.microntek.bt.report";
    private final String PHONEBOOK_RECORD_EXTRA = "phonebook_record";
    private final String PHONEBOOK_SYNC_END_EXTRA = "phonebook_end";
}
