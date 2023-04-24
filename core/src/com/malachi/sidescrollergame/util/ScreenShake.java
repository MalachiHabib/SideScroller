package com.malachi.sidescrollergame.util;

public class ScreenShake {
    private float shakeDuration;
    private float shakeElapsedTime;
    private float shakeIntensity;

    public ScreenShake(float shakeDuration, float shakeIntensity) {
        this.shakeDuration = shakeDuration;
        this.shakeIntensity = shakeIntensity;
        this.shakeElapsedTime = 0;
    }

    public void update(float delta) {
        shakeElapsedTime += delta;
    }

    public boolean isFinished() {
        return shakeElapsedTime >= shakeDuration;
    }

    public float getShakeIntensity() {
        return shakeIntensity * (1 - shakeElapsedTime / shakeDuration);
    }

    public void reset() {
        shakeElapsedTime = 0;
    }
}
