package net.fameless.core.event;

import net.fameless.core.util.EventLogger;

public class TimerStartEvent implements CancellableEvent {

    private boolean cancelled;
    private int startTime;

    public TimerStartEvent(int startTime) {
        this.startTime = startTime;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void setStartTime(int startTime) {
        if (startTime != this.startTime) {
            EventLogger.LOGGER.info("TimerStartEvent: value startTime has been changed to {}.", startTime);
            this.startTime = startTime;
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
