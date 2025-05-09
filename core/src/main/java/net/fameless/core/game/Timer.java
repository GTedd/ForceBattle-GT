package net.fameless.core.game;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.event.EventDispatcher;
import net.fameless.core.event.TimerPauseEvent;
import net.fameless.core.event.TimerSetTimeEvent;
import net.fameless.core.event.TimerStartEvent;
import net.fameless.core.event.TimerStartTimeChangeEvent;
import net.fameless.core.player.BattlePlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class Timer {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + Timer.class.getSimpleName());

    private int startTime;
    private int time;
    private boolean running;
    private boolean hitZero;

    public Timer(int time, boolean running) {
        this.startTime = time;
        this.time = time;
        this.running = running;
        runTimerTask();
        runActionbarTask();
    }

    private void runActionbarTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                BattlePlayer.getOnlinePlayers().forEach(
                        forceBattlePlayer -> forceBattlePlayer.sendActionbar(Caption.of(running ? "timer.running" : "timer.paused")));
            }
        };

        java.util.Timer timer = new java.util.Timer("forcebattle/actionbar");
        timer.scheduleAtFixedRate(task, 0L, 150L);
    }

    private void runTimerTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (running) {
                    time--;
                    if (time < 11) {
                        BattlePlayer.BATTLE_PLAYERS.forEach(battlePlayer -> battlePlayer.playSound(
                                Sound.sound(
                                        Key.key("block.note_block.bass"),
                                        Sound.Source.MASTER,
                                        20,
                                        20
                                )
                        ));
                    }
                }
                if (time == 0) {
                    running = false;
                    time = startTime;
                    hitZero = true;
                    ForceBattle.platform().broadcast(Caption.of("notification.battle_over"));
                }
            }
        };

        java.util.Timer timer = new java.util.Timer("forcebattle/timer");
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int newStartTime) {
        TimerStartTimeChangeEvent startTimeChangeEvent = new TimerStartTimeChangeEvent(newStartTime);
        EventDispatcher.post(startTimeChangeEvent);
        if (startTimeChangeEvent.isCancelled()) {
            logger.info("TimerStartTimeChangeEvent has been denied by an external plugin.");
            return;
        }
        this.startTime = startTimeChangeEvent.getNewStartTime();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int newTime) {
        TimerSetTimeEvent setTimeEvent = new TimerSetTimeEvent(newTime);
        EventDispatcher.post(setTimeEvent);
        if (setTimeEvent.isCancelled()) {
            logger.info("TimerSetTimeEvent has been denied by an external plugin.");
            return;
        }
        this.time = setTimeEvent.getNewTime();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if (running) {
            TimerStartEvent startEvent = new TimerStartEvent(this.time);
            EventDispatcher.post(startEvent);
            if (startEvent.isCancelled()) {
                logger.info("TimerStartEvent has been denied by an external plugin.");
                return;
            }
            this.time = startEvent.getStartTime();
            this.hitZero = false;
            this.running = true;
        } else {
            TimerPauseEvent pauseEvent = new TimerPauseEvent(this.time);
            EventDispatcher.post(pauseEvent);
            if (pauseEvent.isCancelled()) {
                logger.info("TimerPauseEvent has been denied by an external plugin.");
                return;
            }
            this.time = pauseEvent.getPauseTime();
            this.running = false;
        }
    }

    public boolean hasHitZero() {
        return hitZero;
    }

    public void start() {
        running = true;
    }

    public void pause() {
        running = false;
    }

}
