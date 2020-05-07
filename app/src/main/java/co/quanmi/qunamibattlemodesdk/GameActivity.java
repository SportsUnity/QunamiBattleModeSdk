package co.quanmi.qunamibattlemodesdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import co.quanmi.battle_mode_web.GameEventListener;
import co.quanmi.battle_mode_web.GamePlayHandler;
import co.quanmi.battle_mode_web.TTSEventListener;

public class GameActivity extends AppCompatActivity {
    private String url1 = "https://partner-stage.qunami.co:88?ui_data={\"user_id\": \"Dada4\", \"q_lang\": \"e\", \"full_name\": \"Dada4\", \"photo\": \"\", \"eng_only\": false}&sfs_data=b'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiRGFkYTQifQ.Sc0RMhp43mQPNSSAzrYcMN8x2ZwkU-C45tU5RjvBjpc'&sfs=partner-sfs-stg.sportsunity.co&smartfoxServerConfig={\"host\": \"partner-sfs-stg.sportsunity.co\", \"port\": 8843, \"useSSL\": true, \"zone\": \"BBMock\", \"debug\": true}";
    private String url2 = "https://partner-stage.qunami.co:88?ui_data={\"user_id\": \"Dada3\", \"q_lang\": \"e\", \"full_name\": \"Dada3\", \"photo\": \"\", \"eng_only\": false}&sfs_data=b'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiRGFkYTMifQ.VmNKui7wWVJGbGUudIrh1itsTHJX4lJZn8EGxe-fCYA'&sfs=partner-sfs-stg.sportsunity.co&smartfoxServerConfig={\"host\": \"partner-sfs-stg.sportsunity.co\", \"port\": 8843, \"useSSL\": true, \"zone\": \"BBMock\", \"debug\": true}";
    private GamePlayHandler gamePlayHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gamePlayHandler = new GamePlayHandler();
        boolean isTTS = getIntent().getBooleanExtra("is_tts", true);
        if (isTTS) {
            gamePlayHandler.initTextToSpeechForGamePlay(this, new TTSEventListener() {
                @Override
                public void onTTSError() {
                    Log.d("max", "tts is not availabel");
                }

                @Override
                public void onSpeakStart() {
                    Log.d("max", "tts is not availabel");
                }

                @Override
                public void onSpeakDone() {
                    Log.d("max", "tts is not availabel");
                }
            });
        }
        FrameLayout frameLayout = findViewById(R.id.root);
        gamePlayHandler.initGamePlay(this, frameLayout, new GameEventListener() {
            @Override
            public void onGameStateChanged(String gameState) {
                log("GameState", gameState);
            }

            @Override
            public void onRetryClicked() {
                log("retry", "Clicked");
            }

            @Override
            public void onPlayAgainClicked() {

                log("play", "Clicked");
            }

            @Override
            public void onAddMoneyCLicked() {

                log("addmoney", "Clicked");
            }

            @Override
            public void onError(WebResourceError error) {
                log("error ", "error");
            }

            @Override
            public void onConsoleLog(String message) {
                log("console log", message);
            }
        });

    }


    public void loadUrl(View view) {
        if (view.getId() == R.id.button) {
            gamePlayHandler.loadUrl(url1);
        } else {
            gamePlayHandler.loadUrl(url2);
        }
    }

    private void log(String s1, String s2) {
        Log.d("max", s1 + " is " + s2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gamePlayHandler.onPauseGame();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gamePlayHandler.sendResultToTTS(this, requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        gamePlayHandler.onDestroyGame();
    }
}
