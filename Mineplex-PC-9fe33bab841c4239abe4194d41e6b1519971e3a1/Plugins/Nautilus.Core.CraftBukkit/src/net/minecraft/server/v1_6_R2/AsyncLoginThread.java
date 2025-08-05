package net.minecraft.server.v1_6_R2;

import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

@SuppressWarnings("deprecation")
public class AsyncLoginThread extends Thread 
{
    final PendingConnection pendingConnection;

    // CraftBukkit start
    CraftServer server;

    AsyncLoginThread(PendingConnection pendingconnection, CraftServer server) 
    {
        this.server = server;
        // CraftBukkit end
        this.pendingConnection = pendingconnection;
    }

    public void run() 
    {
        try 
        {
            // CraftBukkit start
            if (this.pendingConnection.getSocket() == null) {
                return;
            }

            AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(PendingConnection.d(this.pendingConnection), this.pendingConnection.getSocket().getInetAddress());
            this.server.getPluginManager().callEvent(asyncEvent);

            if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) 
            {
                final PlayerPreLoginEvent event = new PlayerPreLoginEvent(PendingConnection.d(this.pendingConnection), this.pendingConnection.getSocket().getInetAddress());
                if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) 
                {
                    event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
                }
                Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() 
                		{
                    @Override
                    protected PlayerPreLoginEvent.Result evaluate() 
                    {
                    	AsyncLoginThread.this.server.getPluginManager().callEvent(event);
                        return event.getResult();
                    }};

                PendingConnection.b(this.pendingConnection).processQueue.add(waitable);
                if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) 
                {
                    this.pendingConnection.disconnect(event.getKickMessage());
                    return;
                }
            } 
            else 
            {
                if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) 
                {
                    this.pendingConnection.disconnect(asyncEvent.getKickMessage());
                    return;
                }
            }
            // CraftBukkit end

            PendingConnection.a(this.pendingConnection, true);
            // CraftBukkit start
        }
        catch (Exception exception) 
        {
            this.pendingConnection.disconnect("Failed to verify username!");
            server.getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + PendingConnection.d(this.pendingConnection), exception);
            // CraftBukkit end
        }
    }
}
