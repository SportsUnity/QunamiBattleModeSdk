package co.quanmi.battle_mode_web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

class TextToSpeechGenerator implements android.speech.tts.TextToSpeech.OnInitListener {

    private static final int TTS_CHECK = 201;
    private TextToSpeech textToSpeech;
    private boolean isTTSAvailable = false;
    private boolean isLanguageAvailable = false;
    private boolean isTTSEnabled = false;

    private static final String LANGUAGE_HINDI = "hi";
    private static final String LANGUAGE_ENGLISH = "en";
    private TTSEventListener ttsEventListener = null;

    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
            //TODO set sound half
            if (ttsEventListener != null) {
                ttsEventListener.onSpeakStart();
            }
        }

        @Override
        public void onDone(String s) {
            //TODO set sound full
            if (ttsEventListener != null) {
                ttsEventListener.onSpeakDone();
            }
        }

        @Override
        public void onError(String s) {

        }
    };

    public TextToSpeechGenerator(Activity activity, TTSEventListener ttsEventListener) {
        this.ttsEventListener = ttsEventListener;
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
            if (ttsEventListener != null) {
                ttsEventListener.onTTSError();
            }
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

    public void setPreferredLanguage(String preferredLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPreferredLanguageAboveInLollipop(preferredLanguage);
        } else {
            setPreferredLanguageBellowLollipop(preferredLanguage);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setPreferredLanguageAboveInLollipop(String preferredLanguage) {
        if (isTTSAvailable) {
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

    private void setPreferredLanguageBellowLollipop(String preferredLanguage) {
        if (isTTSAvailable) {
            Locale locale = new Locale(preferredLanguage, "IN");
            int avail = textToSpeech.isLanguageAvailable(locale);
            switch (avail) {
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

    public boolean isHindiLanguageAvailable() {
        return isLanguageAvailable(LANGUAGE_HINDI);
    }

    public boolean isEnglishLanguageAvailable() {
        return isLanguageAvailable(LANGUAGE_ENGLISH);
    }

    private boolean isLanguageAvailable(String ttsLanguage) {
        boolean isAvail = false;
        if (isTTSAvailable) {
            Locale locale = new Locale(ttsLanguage, "IN");
            int avail = textToSpeech.isLanguageAvailable(locale);
            switch (avail) {
                case TextToSpeech.LANG_AVAILABLE:
                    isAvail = true;
                    break;
                case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                    isAvail = true;
                    break;
                default:
                    isAvail = false;
                    break;
            }
        } else {
            if (ttsEventListener != null) {
                ttsEventListener.onTTSError();
            }
        }
        return isAvail;
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

    public void installTTSData(Activity activity) {
        Intent installTTSIntent = new Intent();
        installTTSIntent.setAction(android.speech.tts.TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        activity.startActivity(installTTSIntent);
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
