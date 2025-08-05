package mineplex.core.npc.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnum;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.npc.NpcManager;

public class AddCommand extends CommandBase<NpcManager>
{
	public AddCommand(NpcManager plugin)
	{
		super(plugin, Rank.OWNER, "add");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (args == null)
		{			
			Plugin.Help(caller);
		}
		else
		{
			try
			{
				int radius = Integer.parseInt(args[0]);
				String mobName = null;
				
				if (args.length > 1)
				{
					mobName = args[1];
				
					if (args.length > 2)
					{
						for (int i = 2; i < args.length; i++)
						{
							mobName += " " + args[i];
						}
					}
					
					while (mobName.indexOf('(') != -1)
					{
						int startIndex = mobName.indexOf('(');
						
						if (mobName.indexOf(')') == -1)
							break;
						
						int endIndex = mobName.indexOf(')');
						
						if (endIndex < startIndex)
							break;
						
						String originalText = mobName.substring(startIndex, endIndex + 1);
						String colorString = mobName.substring(startIndex + 1, endIndex);
						
						ChatColor color = UtilEnum.fromString(ChatColor.class, colorString);
						
						mobName = mobName.replace(originalText, color + "");
					}
				}
				
				/*
				if (mobName.indexOf('_') != -1)
				{
					String[] mobParts = mobName.split("_");
					mobName = mobParts[0];
					mobSecondLine = mobParts[1];
				}
				*/
				
				Plugin.SetNpcInfo(caller, radius, mobName, caller.getLocation());
				UtilPlayer.message(caller, F.main(Plugin.GetName(), "Location set, now right click entity."));
			}
			catch(NumberFormatException exception)
			{
				Plugin.Help(caller, "Invalid radius.");
			}
			catch(IllegalArgumentException exception)
			{
				Plugin.Help(caller, "Invalid color.");
			}
		}
	}
}
