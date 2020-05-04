package co.quanmi.battle_mode_web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

public class WebViewHandler {


    private TextToSpeechGenerator textToSpeechGenerator;
    private WebView webView;
    private Context context;
    private boolean isTextToSpeechNeeded = false;

    public void initTextToSpeech(Activity activity) {
        textToSpeechGenerator = new TextToSpeechGenerator(activity);
        isTextToSpeechNeeded = true;
    }

    public void sendResultToTTS(int requestCode, int resultCode, @Nullable Intent data, Context context) {
        textToSpeechGenerator.resultData(requestCode, resultCode, data, context);
    }

    public void setTTSLanguage() {
        textToSpeechGenerator.setPreferredLanguage();
    }

    public void changeTTSLanguage(Context context, String lang) {
        setTTSLanguage();
    }

    public void speakOutQuestion(String question) {
        textToSpeechGenerator.speakQuestions(question);
    }

    public void speakOutOption(String option) {
        textToSpeechGenerator.speakOptions(option);
    }


    public void stopTTS() {
        if (textToSpeechGenerator != null) {
            textToSpeechGenerator.stopTTS();
        }
    }

    public boolean toggleTTS() {
        return textToSpeechGenerator.toggleSpeech();
    }

    public boolean isTTSEnabled() {
        return textToSpeechGenerator.isTTSEnabled();
    }

    public void setInfinityTTS() {
        textToSpeechGenerator.setTTSEnabled();
    }

    public boolean isTTSFeasible() {
        return (textToSpeechGenerator.isTTSAvailable() && textToSpeechGenerator.isLanguageAvailable());
    }

    public String getPreferredLanguage() {
        return textToSpeechGenerator.getPreferredLanguage();
    }


    private void initWebView(Context context, FrameLayout rootLayout) {
        if (webView != null && webView.isShown()) {
            webView.destroy();
        }
        webView = new WebView(context);
        rootLayout.removeAllViews();
        rootLayout.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //  showWebViewErrorDialog();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                //readWebConsoleData(message);
            }
        });
    }

    public void setBackGroundColor(@ColorInt int color) {

        webView.setBackgroundColor(color);
    }

    private void loadUrl(String url) {
        webView.loadUrl(url);
    }


   /* private void readWebConsoleData(String message) {
        if (message.equals(ADD_MONEY)) {
            if (!FreedomPassGamePlayHandler.getUnlimitedCardStatus(getApplicationContext())) {
                shoulStartMatch = true;
                addMoneyDialog();
            } else {
                requestUserCurrency(metadata);
            }
        } else if (message.equals(GAME_START)) {
            setInfinityTTS();
            setTTSLanguage();
        } else if (message.contains(SPEAK_HINDI)) {
            parseQuestionAndOptions(message, "hi");
        } else if (message.contains(SPEAK_ENGLSISH)) {
            parseQuestionAndOptions(message, "en");
        } else if (message.contains(STOP_READING)) {
            gamePlayLogic.speakOutQuestion("");
        } else if (message.contains(INITIATE_MATCH)) {
            replayGame();
        } else if (message.contains(POST_MATCH_DATA)) {
            setPostMatchData(message);
        } else if (message.contains(DISPLAY_POST_MATCH)) {
            displayPostMatch();
        }
    }*/

    private void parseQuestionAndOptions(String message, String lang) {
        try {
            String json = message.substring(message.indexOf("# ") + 1);
            String questionText = "";
            ArrayList<String> optionArray = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
            questionText = jsonObject.getString("question_text");
            JSONArray optionsArray = jsonObject.getJSONArray("options");
            for (int i = 0; i < optionsArray.length(); i++) {
                JSONObject options = optionsArray.getJSONObject(i);
                String optionText = options.getString("value");
                optionArray.add(optionText);
            }
            changeTTSLanguage(context, lang);
            speakOutQuestion(questionText);
            for (String optionText : optionArray) {
                speakOutOption(optionText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
