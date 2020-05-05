package co.quanmi.qunamibattlemodesdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import co.quanmi.battle_mode_web.GameEventListener;
import co.quanmi.battle_mode_web.GamePlayHandler;
import co.quanmi.battle_mode_web.TTSEventListener;

public class MainActivity extends AppCompatActivity {
    GamePlayHandler gamePlayHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gamePlayHandler = new GamePlayHandler();
        gamePlayHandler.setUpTextToSpeechEngine(this, new TTSEventListener() {
            @Override
            public void onTTSError() {
                Log.d("max","TTS ERROR");
            }

            @Override
            public void onSpeakStart() {

            }

            @Override
            public void onSpeakDone() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gamePlayHandler.sendResultToTTS(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gamePlayHandler.stopTTS();
    }

    public void startGame(View view) {
        startActivity(new Intent(this, GameActivity.class));
    }
}
