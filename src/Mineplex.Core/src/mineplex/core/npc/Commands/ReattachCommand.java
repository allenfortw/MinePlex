package mineplex.core.npc.Commands;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.npc.NpcManager;

import org.bukkit.entity.Player;

public class ReattachCommand extends CommandBase<NpcManager>
{
	public ReattachCommand(NpcManager plugin)
	{
		super(plugin, Rank.OWNER, "reattach");
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
			Plugin.ReattachNpcs();
			UtilPlayer.message(caller, F.main(Plugin.GetName(), "Npcs reattached."));
		}
	}
}