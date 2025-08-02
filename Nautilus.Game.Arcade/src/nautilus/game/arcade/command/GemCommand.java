package nautilus.game.arcade.command;

import org.bukkit.entity.Player;

import nautilus.game.arcade.ArcadeManager;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;

public class GemCommand extends CommandBase<ArcadeManager>
{
	public GemCommand(ArcadeManager plugin)
	{
		super(plugin, Rank.OWNER, "gem");
	}

	@Override
	public void Execute(Player caller, String[] args)
	{
		if (!caller.getName().equals("Chiss"))
			return;
		
		if (args.length < 2)
		{
			UtilPlayer.message(caller, F.main("Gem", "Missing Args"));
			return;
		}
		
		Player target = UtilPlayer.searchOnline(caller, args[0], true);
		if (target == null)	return;
		
		try
		{
			int gems = Integer.parseInt(args[1]);
			Plugin.GetDonation().RewardGems(target.getName(), gems);
			
			UtilPlayer.message(caller, F.main("Gem", "You gave " + F.elem(gems + " Blue/Green Gems") + " to " + F.name(target.getName()) + "."));
			UtilPlayer.message(target, F.main("Gem", F.name(caller.getName()) + " gave you " + F.elem(gems + " Blue/Green Gems") + "."));
		}
		catch (Exception e)
		{
			UtilPlayer.message(caller, F.main("Gem", "Invalid Gem Amount"));
		}
	}
}
