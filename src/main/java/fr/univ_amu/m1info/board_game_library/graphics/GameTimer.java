package fr.univ_amu.m1info.board_game_library.graphics;

/**
 * Interface for game timer functionality.
 * Allows starting, stopping, resetting and tracking elapsed time.
 */
public interface GameTimer {
    /**
     * Starts the timer.
     */
    void start();

    /**
     * Stops the timer.
     */
    void stop();

    /**
     * Resets the timer to zero.
     */
    void reset();

    /**
     * Gets the elapsed time in milliseconds.
     *
     * @return elapsed time in milliseconds
     */
    long getElapsedMillis();

    /**
     * Sets a callback to be executed on each timer tick.
     *
     * @param r the runnable to execute on each tick
     */
    void setOnTick(Runnable r);
}

