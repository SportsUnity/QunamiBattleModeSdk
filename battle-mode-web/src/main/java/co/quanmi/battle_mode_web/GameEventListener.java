package co.quanmi.battle_mode_web;

import android.webkit.WebResourceError;

public interface GameEventListener {
    void onGameStateChanged(String gameState);

    void onRetryClicked();

    void onPlayAgainClicked();

    void onAddMoneyCLicked();

    void onError(WebResourceError error);

    void onConsoleLog(String message);
}
