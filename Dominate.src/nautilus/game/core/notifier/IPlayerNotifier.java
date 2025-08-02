package nautilus.game.core.notifier;

import java.util.Collection;
import nautilus.game.core.player.IGamePlayer;

public abstract interface IPlayerNotifier
{
  public abstract void BroadcastMessageToPlayers(String paramString, Collection<? extends IGamePlayer> paramCollection);
}
