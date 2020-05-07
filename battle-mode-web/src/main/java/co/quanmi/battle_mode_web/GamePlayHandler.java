package co.quanmi.battle_mode_web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GamePlayHandler {


    private TextToSpeechGenerator textToSpeechGenerator;
    private WebView webView;
    private boolean isTextToSpeechNeeded = false;
    private FrameLayout rootLayout;
    private Context context;
    private GameEventListener gameEventListener;
    private static final String TAG = "QunamiSdk";

    public GamePlayHandler() {

    }

    public void setUpTextToSpeechEngine(@NonNull Activity activity, TTSEventListener ttsEventListener) {
        initTextToSpeech(activity, ttsEventListener);
    }

    public void initTextToSpeechForGamePlay(@NonNull Activity activity, TTSEventListener ttsEventListener) {
        initTextToSpeech(activity, ttsEventListener);
        isTextToSpeechNeeded = true;
    }

    public void sendResultToTTS(Context context, int requestCode, int resultCode, @Nullable Intent data) {
        textToSpeechGenerator.resultData(requestCode, resultCode, data, context);
    }

    public boolean isHindiLanguageAvailableInTTS() {
        return textToSpeechGenerator.isHindiLanguageAvailable();
    }

    public boolean isEnglishLanguageAvailableInTTS() {
        return textToSpeechGenerator.isEnglishLanguageAvailable();
    }

    public void installTTSData(@NonNull Activity activity) {
        textToSpeechGenerator.installTTSData(activity);
    }

    private void initTextToSpeech(Activity activity, TTSEventListener ttsEventListener) {
        textToSpeechGenerator = new TextToSpeechGenerator(activity, ttsEventListener);
    }

    private void changeTTSLanguage(String lang) {
        if (isTextToSpeechNeeded) {
            textToSpeechGenerator.setPreferredLanguage(lang);
        }
    }

    private void speakOutQuestion(String question) {
        if (isTextToSpeechNeeded) {
            textToSpeechGenerator.speakQuestions(question);
        }
    }

    private void speakOutOption(String option) {
        if (isTextToSpeechNeeded) {
            textToSpeechGenerator.speakOptions(option);
        }
    }


    public void stopTTS() {
        if (textToSpeechGenerator != null) {
            textToSpeechGenerator.stopTTS();
        }
    }

    public boolean toggleTTS() {
        return textToSpeechGenerator.toggleSpeech();
    }

    private void setInfinityTTS() {
        if (isTextToSpeechNeeded) {
            textToSpeechGenerator.setTTSEnabled();
        }
    }

    public void initGamePlay(Context context, FrameLayout rootLayout, GameEventListener gameEventListener) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.gameEventListener = gameEventListener;
    }

    public void setBackGroundColor(@ColorInt int color) {
        webView.setBackgroundColor(color);
    }

    public void loadUrl(String url) {
        initWebView(context, rootLayout);
        webView.loadUrl(url);
        if (isTextToSpeechNeeded) {
            setInfinityTTS();
        }
    }

    @SuppressLint("JavascriptInterface")
    public void addJavaScriptInterface(Object object, String name) {
        webView.addJavascriptInterface(object, name);
    }

    public void onPauseGame() {
        stopSpeaking();
        if (webView != null) {
            webView.destroy();
        }
    }

    private void stopSpeaking() {
        if (isTextToSpeechNeeded) {
            textToSpeechGenerator.speakQuestions("");
        }
    }

    public void onDestroyGame() {
        onPauseGame();
        stopTTS();
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
                onError(error);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                readWebConsoleData(consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    private void readWebConsoleData(String message) {
        if (message.equals("retry")) {
            onRetry();
        } else if (message.equals("play_again")) {
            onPlay();
        } else if (message.equals("add_balance")) {
            onAddMoney();
        } else if (message.equals("answer clicked")) {
            stopSpeaking();
        } else if (message.startsWith("gameState#")) {
            String gameState = message.substring(message.indexOf("#") + 1);
            onGameStateChanged(gameState);
        } else if (message.startsWith("hi#") || message.startsWith("eng#")) {
            parseQuestionAndOptions(message);
        } else {
            onConsoleLog(message);
        }
    }

    private void onGameStateChanged(String gameState) {
        printLog("onGameStateChanged " + gameState);
        if (gameEventListener != null) {
            gameEventListener.onGameStateChanged(gameState);
        }
    }

    private void onConsoleLog(String message) {
        printLog("onConsoleLog" + message);
        if (gameEventListener != null) {
            gameEventListener.onConsoleLog(message);
        }
    }

    private void onError(WebResourceError error) {
        printLog("onError " + error.toString());
        if (gameEventListener != null) {
            gameEventListener.onError(error);
        }
    }

    private void onRetry() {
        printLog("onRetryClicked");
        if (gameEventListener != null) {
            gameEventListener.onRetryClicked();
        }
    }

    private void onPlay() {
        printLog("onPlayClicked");
        if (gameEventListener != null) {
            gameEventListener.onPlayAgainClicked();
        }
    }

    private void onAddMoney() {
        printLog("onAddMoneyCLicked");
        if (gameEventListener != null) {
            gameEventListener.onAddMoneyCLicked();
        }
    }

    private void parseQuestionAndOptions(String message) {
        printLog("Speak question");
        if (isTextToSpeechNeeded) {
            try {
                String lang = message.substring(0, message.indexOf("#"));
                String json = message.substring(message.indexOf("#") + 1);
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
                changeTTSLanguage(lang);
                speakOutQuestion(questionText);
                for (String optionText : optionArray) {
                    speakOutOption(optionText);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void printLog(String msg) {
        Log.i(TAG, msg);
    }

}
