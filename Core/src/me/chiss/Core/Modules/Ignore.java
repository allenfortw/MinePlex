package me.chiss.Core.Modules;

import java.util.List;


import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Module.AModule;
import mineplex.core.server.IRepository;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;

public class Ignore extends AModule
{
	public Ignore(JavaPlugin plugin, IRepository repository) 
	{
		super("Ignore", plugin, repository);
	}

	//Module Functions
	@Override
	public void enable() 
	{

	}

	@Override
	public void disable() 
	{

	}

	@Override
	public void config() 
	{

	}

	@Override
	public void commands() 
	{
		AddCommand("ignore");
		AddCommand("i");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (args.length == 0)
			return; //UtilPlayer.message(caller, Clients().Get(caller).Ignore().Display());

		else
			ignore(caller, args[0]);
	}

	public void ignore(final Player caller, final String arg)
	{
		if (!Recharge().use(caller, "Ignore Command", 2000, true))
			return;

		GetRepository().MatchPlayerName(new Callback<List<String>>()
		{
			public void run(List<String> matches)
			{
				UtilPlayer.searchOffline(matches, new Callback<String>()
				{
					public void run(String target)
					{
						if (target == null)
							return;

						if (!caller.isOnline())
							return;

						boolean muted = Clients().Get(caller).Ignore().ToggleIgnore(target);

						if (muted)		UtilPlayer.message(caller, F.main(_moduleName, "You ignored " + F.name(target) + "."));
						else			UtilPlayer.message(caller, F.main(_moduleName, "You unignored " + F.name(target) + "."));
					}
				}, caller, arg, true);
			}
		}, arg);

	} 
}
