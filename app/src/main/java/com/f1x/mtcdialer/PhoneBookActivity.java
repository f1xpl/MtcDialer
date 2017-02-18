package com.f1x.mtcdialer;

import android.os.Bundle;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by COMPUTER on 2017-02-17.
 */

public abstract class PhoneBookActivity extends BluetoothServiceActivity {
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mPhoneBookRecords = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneBookReceiver.unregister(this);
    }

    @Override
    protected void onServiceConnected() {
        try {
            List<String> phoneBookRecords = mBluetoothServiceInterface.getPhoneBookList();

            if(!phoneBookRecords.isEmpty()) {
                buildPhoneBook(phoneBookRecords);
                onPhoneBookFetchFinished();
            } else {
                mPhoneBookReceiver.register(this);
                mBluetoothServiceInterface.syncPhonebook();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(PhoneBookActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void buildPhoneBook(List<String> phoneBookRecords) {
        for(String phoneBookRecord : phoneBookRecords) {
            String[] parsedRecord = phoneBookRecord.split("\\^");
            String phoneNumber = parsedRecord[1].replaceAll("[^\\d\\+]", "");

            mPhoneBookRecords.put(parsedRecord[0], phoneNumber);
        }
    }

    protected abstract void onPhoneBookFetchFinished();

    private final PhoneBookReceiver mPhoneBookReceiver = new PhoneBookReceiver() {
        @Override
        public void onPhoneBookRecordFetched(String record) {
            mRawPhoneBookRecords.add(record);
        }

        @Override
        public void onPhoneBookFetchFinished() {
            this.unregister(PhoneBookActivity.this);
            PhoneBookActivity.this.buildPhoneBook(mRawPhoneBookRecords);

            try {
                PhoneBookActivity.this.mBluetoothServiceInterface.setPhoneBookList(mRawPhoneBookRecords);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(PhoneBookActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            PhoneBookActivity.this.onPhoneBookFetchFinished();
            mRawPhoneBookRecords.clear();
        }

        List<String> mRawPhoneBookRecords = new ArrayList<>();
    };

    protected Map<String, String> mPhoneBookRecords;
}
