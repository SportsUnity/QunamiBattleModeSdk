package co.quanmi.battle_mode_web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.Nullable;

class TextToSpeechGenerator implements android.speech.tts.TextToSpeech.OnInitListener {

    private static final int TTS_CHECK = 201;
    private TextToSpeech textToSpeech;
    private boolean isTTSAvailable = false;
    private boolean isLanguageAvailable = false;
    private Context context;
    private boolean isTTSEnabled = false;

    private static final String LANGUAGE_HINDI = "hi";
    private static final String LANGUAGE_ENGLISH = "en";

    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
            //TODO set sound half
        }

        @Override
        public void onDone(String s) {
            //TODO set sound full
        }

        @Override
        public void onError(String s) {

        }
    };

    public TextToSpeechGenerator(Activity activity) {
        this.context = activity.getApplicationContext();
        checkTTS_Data(activity);
    }

    private void checkTTS_Data(Activity activity) {
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        activity.startActivityForResult(checkTTSIntent, TTS_CHECK);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            isTTSAvailable = true;
            textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
            isTTSEnabled = true;
        } else if (status == TextToSpeech.ERROR) {
            isTTSAvailable = false;
        }
    }

    public void resultData(int requestCode, int resultCode, @Nullable Intent data, Context context) {
        if (requestCode == TTS_CHECK) {
            if (resultCode == android.speech.tts.TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                textToSpeech = new TextToSpeech(context, this);
                Log.d("TTS", "tts create");
            }
        }
    }

    public void setPreferredLanguage() {
        if (isTTSAvailable) {

            String preferredLanguage = getPreferredLanguage();

            Locale locale = new Locale(preferredLanguage, "IN");
            int avail = textToSpeech.isLanguageAvailable(locale);

            switch (avail) {
                case TextToSpeech.LANG_AVAILABLE:
                    textToSpeech.setLanguage(Locale.forLanguageTag(preferredLanguage));
                    isLanguageAvailable = true;
                    break;
                case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                    textToSpeech.setLanguage(locale);
                    isLanguageAvailable = true;
                    break;
                default:
                    isLanguageAvailable = false;
                    break;
            }
        }
    }

    public String getPreferredLanguage() {
        String preferredLanguage = LANGUAGE_ENGLISH;
        return preferredLanguage;
    }

    public boolean isTTSAvailable() {
        return isTTSAvailable;
    }

    public boolean isLanguageAvailable() {
        return isLanguageAvailable;
    }

    public void installTTS_Data(Activity activity) {

        if (!isTTSAvailable || !isLanguageAvailable) {

            Intent installTTSIntent = new Intent();
            installTTSIntent.setAction(android.speech.tts.TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            activity.startActivity(installTTSIntent);

        }
    }

    public void speakQuestions(String questionText) {
        questionText = questionText.replaceAll("_{2,}", "dash").replaceAll("[ _ ]{2,}", " dash ").replaceAll("[_.]{2,}", " dash ");
        if (isTTSAvailable() && isLanguageAvailable && isTTSEnabled()) {
            textToSpeech.speak(questionText, TextToSpeech.QUEUE_FLUSH, null, "qz");
        }

    }

    public void speakOptions(String option) {
        if (isTTSAvailable() && isLanguageAvailable && isTTSEnabled()) {
            textToSpeech.speak(option, TextToSpeech.QUEUE_ADD, null, "op");
        }

    }

    public boolean toggleSpeech() {
        isTTSEnabled = !isTTSEnabled;
        return isTTSEnabled;
    }

    public void setTTSEnabled() {
        isTTSEnabled = true;
    }

    public boolean isTTSEnabled() {
        return isTTSEnabled;
    }

    public void stopTTS() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
