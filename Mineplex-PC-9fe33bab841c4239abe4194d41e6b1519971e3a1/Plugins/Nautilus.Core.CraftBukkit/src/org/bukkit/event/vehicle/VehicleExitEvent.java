package org.bukkit.event.vehicle;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Raised when a living entity exits a vehicle.
 */
public class VehicleExitEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Entity vehicle;
    private final LivingEntity exited;

    public VehicleExitEvent(final Entity vehicle, final LivingEntity exited) {
        this.vehicle = vehicle;
        this.exited = exited;
    }

    /**
     * Get the vehicle.
     *
     * @return the vehicle
     */
    public final Entity getVehicle() {
        return vehicle;
    }
    
    /**
     * Get the living entity that exited the vehicle.
     *
     * @return The entity.
     */
    public LivingEntity getExited() {
        return exited;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
