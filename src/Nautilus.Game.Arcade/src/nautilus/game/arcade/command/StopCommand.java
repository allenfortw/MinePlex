package nautilus.game.arcade.command;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game.GameState;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;

public class StopCommand extends CommandBase<ArcadeManager>
{
	public StopCommand(ArcadeManager plugin)
	{
		super(plugin, Rank.ADMIN, "stop");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (Plugin.GetGame() == null)
			return;

		HandlerList.unregisterAll(Plugin.GetGame());
		Plugin.GetGame().SetState(GameState.Dead);
		Plugin.GetGame().WorldData.Uninitialize();
		Plugin.SetGame(null);
		
		caller.sendMessage("Stopped Game!");
	}
}
