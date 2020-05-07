package co.quanmi.qunamibattlemodesdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
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
                Log.d("max", "TTS ERROR");
                gamePlayHandler.installTTSData(MainActivity.this);
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
        AppCompatCheckBox checkBox = findViewById(R.id.lang_check);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("is_tts", checkBox.isChecked());
        startActivity(intent);
    }

    public void checkHindi(View view) {
        boolean isAvailable = gamePlayHandler.isHindiLanguageAvailableInTTS();
        showTTSmsg(isAvailable);
    }

    public void checkEnglish(View view) {
        boolean isAvailable = gamePlayHandler.isEnglishLanguageAvailableInTTS();
        showTTSmsg(isAvailable);
    }

    private void showTTSmsg(boolean isAvailable) {
        if (isAvailable) {
            Toast.makeText(getApplicationContext(), "Language available", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(getApplicationContext(), "Language not available, install language", Toast.LENGTH_SHORT).show();
        }
    }

    public void installTTSData(View view) {
        gamePlayHandler.installTTSData(MainActivity.this);
    }

}
