package mineplex.core.npc.Commands;

import org.bukkit.entity.Player;

import mineplex.core.command.MultiCommandBase;
import mineplex.core.common.Rank;
import mineplex.core.npc.NpcManager;

public class NpcCommand extends MultiCommandBase<NpcManager>
{
	public NpcCommand(NpcManager plugin)
	{
		super(plugin, Rank.OWNER, "npc");

		AddCommand(new AddCommand(plugin));
		AddCommand(new DeleteCommand(plugin));
		AddCommand(new ClearCommand(plugin));
		AddCommand(new HomeCommand(plugin));
		AddCommand(new ReattachCommand(plugin));
	}

	@Override
	protected void Help(Player caller, String args[])
	{
		Plugin.Help(caller);
	}
}
