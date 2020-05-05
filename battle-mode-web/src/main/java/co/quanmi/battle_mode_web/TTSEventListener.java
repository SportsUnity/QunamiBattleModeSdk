package co.quanmi.battle_mode_web;

public interface TTSEventListener {
    void onTTSError();

    void onSpeakStart();

    void onSpeakDone();
}
