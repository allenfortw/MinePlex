package nautilus.game.core.notifier;

import java.util.Collection;

import nautilus.game.core.player.IGamePlayer;

public interface IPlayerNotifier
{
    void BroadcastMessageToPlayers(String message, Collection<? extends IGamePlayer> players);
}
