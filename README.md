# Qunami Battle Mode Web SDK
An android sdk to integrate Qunami's quiz game.

# Version

0.1.2

# Installation
Add to your app's build.gradle file.
````java
implementation 'co.qunami.battle_mode_web:battle-mode-web:0.1.2'
````

# Usage

### TextToSpeech implementation
To use text to speech functionality, Add following code in MainActivity class
````java
private GamePlayHandler gamePlayHandler;
````
inside onCreate() add following code-
````java
gamePlayHandler = new GamePlayHandler();
gamePlayHandler.setUpTextToSpeechEngine(this, new TTSEventListener() {
    @Override
    public void onTTSError() {
        //TTS not installed
        gamePlayHandler.installTTSData(MainActivity.this);
    }

    @Override
    public void onSpeakStart() {

    }

    @Override
    public void onSpeakDone() {

    }
});
````
Override onActivityResult() and send result to TextToSpeechEngine-
````java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    gamePlayHandler.sendResultToTTS(this, requestCode, resultCode, data);
}
````
And override onDestory to stop TTSEngine
````java
@Override
    protected void onDestroy() {
        super.onDestroy();
        gamePlayHandler.stopTTS();
}
````

### Optional Language check
To check if language is available or not use-
````java
boolean isHindiAvailable = gamePlayHandler.isHindiLanguageAvailableInTTS();
boolean isEnglishAvailable = gamePlayHandler.isEnglishLanguageAvailableInTTS();
````

if not available then call-
````java
gamePlayHandler.installTTSData(MainActivity.this);
````
## Implementation For game play
Create a new GameActivity.java class

and add following framelayout in your activity_game.xml
````java
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/root"/>
````
create an GamePlayHandler object in GameActivity.java
```java
private GamePlayHandler gamePlayHandler;
````
Inside onCreate() initialize the GamePlayHandler object-
````java
gamePlayHandler = new GamePlayHandler();
````

#### To implement inGame TextToSpeech init textToSpeech for gameplay
````java
gamePlayHandler.initTextToSpeechForGamePlay(this, new TTSEventListener() {
    @Override
    public void onTTSError() {

    }
    @Override
    public void onSpeakStart() {

    }
    @Override
    public void onSpeakDone() {

    }
});
````
#### and override onActivityResult()
````java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    gamePlayHandler.sendResultToTTS(this, requestCode, resultCode, data);
}
````

### Init for game play
Call gamePlayHandler.initGamePlay and pass the Context, FrameLayout and GameEventListener->
````java
FrameLayout frameLayout = findViewById(R.id.root);

gamePlayHandler.initGamePlay(this, frameLayout, new GameEventListener() {
    @Override
    public void onGameStateChanged(String gameState) {
        //if(gameState.equals(GameStates.QUESTION))
    }
    @Override
    public void onRetryClicked() {

    }
    @Override
    public void onPlayAgainClicked() {

    }
    @Override
    public void onAddMoneyCLicked() {
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
````
And to start the game call
````java
gamePlayHandler.loadUrl(url1);
````
#### override onPause() and onDestory()

````java
@Override
protected void onPause() {
    super.onPause();
    gamePlayHandler.onPauseGame();
}
    
 @Override
protected void onDestroy() {
    super.onDestroy();
    gamePlayHandler.onDestroyGame();
}
````
