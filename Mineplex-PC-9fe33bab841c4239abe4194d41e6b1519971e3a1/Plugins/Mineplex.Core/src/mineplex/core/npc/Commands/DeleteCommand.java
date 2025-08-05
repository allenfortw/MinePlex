package mineplex.core.npc.Commands;

import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.npc.NpcManager;

public class DeleteCommand extends CommandBase<NpcManager>
{
	public DeleteCommand(NpcManager plugin)
	{
		super(plugin, Rank.OWNER, "del");
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
			Plugin.PrepDeleteNpc(caller);
			UtilPlayer.message(caller, F.main(Plugin.GetName(), "Now right click npc."));
		}
	}
}
