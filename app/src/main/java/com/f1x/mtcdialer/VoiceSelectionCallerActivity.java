package com.f1x.mtcdialer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by COMPUTER on 2017-02-17.
 */

public class VoiceSelectionCallerActivity extends PhoneBookActivity {
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_voice_selection_caller);
    }

    @Override
    protected void onPhoneBookFetchFinished() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, this.getText(R.string.SpeechPrompt));

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onServiceDisconnected() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            Toast.makeText(this, this.getText(R.string.SpeechNotRecognized), Toast.LENGTH_LONG).show();
            VoiceSelectionCallerActivity.this.finish();
            return;
        }

        List<String> texts = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (texts != null && texts.isEmpty()) {
            Toast.makeText(this, this.getText(R.string.SpeechNotRecognized), Toast.LENGTH_LONG).show();
            VoiceSelectionCallerActivity.this.finish();
            return;
        }

        String input = texts.get(0);
        if (input.isEmpty()) {
            Toast.makeText(this, this.getText(R.string.SpeechNotRecognized), Toast.LENGTH_LONG).show();
            VoiceSelectionCallerActivity.this.finish();
            return;
        }

        processSpeechInput(input);
        VoiceSelectionCallerActivity.this.finish();
    }

    private void processSpeechInput(String input) {
        String phoneNumber = null;

        if (mPhoneBookRecords.containsKey(input)) {
            phoneNumber = mPhoneBookRecords.get(input);
        } else if (input.replaceAll("\\s+", "").matches("\\+?\\d+?")) {
            phoneNumber = input;
        }

        if (phoneNumber != null) {
            final String message = String.format(this.getString(R.string.CallingTo), input);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            Intent dialIntent = new Intent(this, DialActivity.class);
            dialIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(dialIntent);
        } else {
            String text = String.format(this.getString(R.string.PhoneBookEntryNotFound), input);
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }
    }
}
