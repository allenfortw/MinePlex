package mineplex.core.npc.Commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.npc.NpcManager;

public class HomeCommand extends CommandBase<NpcManager>
{
	public HomeCommand(NpcManager plugin)
	{
		super(plugin, Rank.OWNER, "home");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args != null)
		{			
			Plugin.Help(caller);
		}
		else
		{
			Plugin.TeleportNpcsHome();
			UtilPlayer.message(caller, F.main(Plugin.GetName(), "Npcs teleported to home locations."));
		}
	}
}
