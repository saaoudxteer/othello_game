package fr.univ_amu.m1info.board_game_library.graphics.javafx.timer;

import fr.univ_amu.m1info.board_game_library.graphics.GameTimer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * JavaFX implementation of GameTimer using Timeline.
 */
public class JavaFXGameTimer implements GameTimer {
    private Timeline timeline;
    private long startTime;
    private long elapsed;
    private Runnable onTick;

    public JavaFXGameTimer() {
        this.elapsed = 0;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                if (onTick != null) {
                    onTick.run();
                }
            })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @Override
    public void stop() {
        if (timeline != null) {
            timeline.stop();
            elapsed = getElapsedMillis();
        }
    }

    @Override
    public void reset() {
        if (timeline != null) {
            timeline.stop();
        }
        startTime = System.currentTimeMillis();
        elapsed = 0;
    }

    @Override
    public long getElapsedMillis() {
        if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
            return elapsed + (System.currentTimeMillis() - startTime);
        }
        return elapsed;
    }

    @Override
    public void setOnTick(Runnable r) {
        this.onTick = r;
    }
}

