package me.chiss.Core.Field;

import java.util.HashSet;
import java.util.WeakHashMap;

import nautilus.minecraft.core.webserver.token.Server.FieldMonsterToken;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.server.IRepository;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilWorld;
import me.chiss.Core.Field.Monsters.FieldMonsterBase;
import me.chiss.Core.Module.AModule;

public class FieldMonster extends AModule
{
	private HashSet<FieldMonsterBase> _pits;
	private String _serverName;

	private WeakHashMap<Player, FieldMonsterInput> _input = new WeakHashMap<Player, FieldMonsterInput>();

	public FieldMonster(JavaPlugin plugin, IRepository repository, String serverName) 
	{
		super("Field Monster", plugin, repository);
		
		_pits = new HashSet<FieldMonsterBase>();
		_serverName = serverName;
		
		Load();
	}

	@Override
	public void enable() 
	{

	}

	@Override
	public void disable() 
	{
		Clean();
	}

	@Override
	public void config() 
	{

	}

	@Override
	public void commands()
	{
		AddCommand("fm");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!Clients().Get(caller).Rank().Has(Rank.ADMIN, true))
			return;

		if (!_input.containsKey(caller))
			_input.put(caller, new FieldMonsterInput());

		FieldMonsterInput input = _input.get(caller);

		if (args.length == 0)
		{
			_input.get(caller).Display(caller);
			UtilPlayer.message(caller, F.main(_moduleName, "Type " + F.elem("/fm help") + " for commands."));
		}

		else if (args[0].equalsIgnoreCase("help"))
		{
			Help(caller);
		}

		else if (args[0].equalsIgnoreCase("type"))
		{			
			try
			{
				input.type = UtilEnt.searchEntity(caller, args[1], true);
				if (input.type != null)
					input.Display(caller);
			}
			catch (Exception e)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Invalid Monster Type."));
			}
		}

		else if (args[0].equalsIgnoreCase("max"))
		{
			try
			{
				int value = Integer.parseInt(args[1]);
				if (value < 1)	value = 1;
				input.mobMax = value;
				input.Display(caller);
			}
			catch (Exception e)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Invalid Monster Max."));
			}
		}

		else if (args[0].equalsIgnoreCase("rate"))
		{
			try
			{
				double value = Double.parseDouble(args[1]);
				if (value < 0)	value = 0;
				input.mobRate = value;
				input.Display(caller);
			}
			catch (Exception e)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Invalid Monster Rate."));
			}
		}

		else if (args[0].equalsIgnoreCase("radius"))
		{
			try
			{
				int integer = Integer.parseInt(args[1]);
				if (integer < 1)	integer = 1;
				input.radius = integer;
				input.Display(caller);
			}
			catch (Exception e)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Invalid Area Radius."));
			}
		}

		else if (args[0].equalsIgnoreCase("height"))
		{
			try
			{
				int integer = Integer.parseInt(args[1]);
				if (integer < 1)	integer = 1;
				input.height = integer;
				input.Display(caller);
			}
			catch (Exception e)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Invalid Area Height."));
			}
		}

		else if (args[0].equalsIgnoreCase("create"))
		{
			if (args.length < 2)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Missing Monster Field Name."));
			}
			else
			{
				Create(caller, args[1]);
			}
		}

		else if (args[0].equalsIgnoreCase("delete"))
		{
			if (args.length < 2)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Missing Monster Field Name."));
			}
			else
			{
				Delete(caller, args[1]);
			}
		}

		else if (args[0].equalsIgnoreCase("list"))
		{
			UtilPlayer.message(caller, F.main(_moduleName, "Listing Monster Fields;"));

			for (FieldMonsterBase pit : _pits)
				pit.Display(caller);
		}

		else if (args[0].equalsIgnoreCase("info"))
		{
			UtilPlayer.message(caller, F.main(GetName(), "Listing Monster Fields;"));

			for (FieldMonsterBase pit : _pits)
				pit.Display(caller);
		}

		else if (args[0].equalsIgnoreCase("wipe"))
		{
			Wipe(caller, true);
		}

		else if (args[0].equalsIgnoreCase("kill"))
		{
			for (FieldMonsterBase pit : _pits)
				pit.RemoveMonsters();
		}

		else
		{
			UtilPlayer.message(caller, F.main(GetName(), "Invalid Command."));
		}
	}

	private void Help(Player caller) 
	{
		UtilPlayer.message(caller, F.main(GetName(), "Commands List;"));
		UtilPlayer.message(caller, F.help("/fm type <Monster>", "Set Monster Type", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm max <#>", "Set Monster Limit", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm rate <Minutes>", "Set Monster Rate", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm radius <#>", "Set Area Radius", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm height <#>", "Set Area Height", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm create <Name>", "Create at your Location", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm delete <Name>", "Delete Field", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm list", "List Monster Fields", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm info <Name>", "Display Monster Field", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm kill", "Kills all Field Monsters", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fm wipe", "Delete All Monster Field (Database)", Rank.ADMIN));
	}

	private void Create(Player caller, String name) 
	{
		FieldMonsterInput input = _input.get(caller);

		if (input.type == null)
		{
			UtilPlayer.message(caller, F.main(GetName(), "You have not set Monster Type."));
			return;
		}
		
		for (FieldMonsterBase pit : _pits)
		{
			if (name.equalsIgnoreCase(pit.GetName()))
			{
				UtilPlayer.message(caller, F.main(GetName(), "Monster Field with this name already exists."));
				return;
			}
		}

		FieldMonsterBase pit = new FieldMonsterBase(this, name, _serverName, input.type, input.mobMax, input.mobRate, caller.getLocation(), input.radius, input.height);
		Add(pit, true);

		UtilPlayer.message(caller, F.main(GetName(), "You created Monster Field."));
		pit.Display(caller);
	}

	private void Add(FieldMonsterBase pit, boolean repo)
	{
		UtilServer.getServer().getPluginManager().registerEvents(pit, Plugin());
		_pits.add(pit);
		
		if (repo)
			GetRepository().AddFieldMonster(pit.GetToken());
	}

	private void Delete(Player caller, String name)
	{
		HashSet<FieldMonsterBase> remove = new HashSet<FieldMonsterBase>();

		for (FieldMonsterBase pit : _pits)
			if (pit.GetName().equalsIgnoreCase(name))
				remove.add(pit);

		int i = remove.size();

		for (FieldMonsterBase pit : remove)
			Delete(pit, true);

		UtilPlayer.message(caller, F.main(GetName(), "Deleted " + i + " Monster Field(s)."));
	}

	private void Delete(FieldMonsterBase pit, boolean repo)
	{
		_pits.remove(pit);
		pit.RemoveMonsters();
		HandlerList.unregisterAll(pit);
		
		if (repo)
			GetRepository().DeleteFieldMonster(_serverName, pit.GetToken().Name); 
	}

	private void Wipe(Player player, boolean repo)
	{
		HashSet<FieldMonsterBase> remove = new HashSet<FieldMonsterBase>();

		for (FieldMonsterBase pit : _pits)
			remove.add(pit);

		_pits.clear();

		for (FieldMonsterBase pit : remove)
			Delete(pit, repo);

		UtilPlayer.message(player, F.main(_moduleName, "Field Monsters Wiped."));
	}

	private void Load()
	{
		Wipe(null, false);

		for (FieldMonsterToken token : GetRepository().GetFieldMonsters(_serverName))
		{
			EntityType type = UtilEnt.searchEntity(null, token.Type, false);
			if (type == null)	continue;

			Location loc = UtilWorld.strToLoc(token.Centre);
			if (loc == null)	continue;

			FieldMonsterBase pit = new FieldMonsterBase(this, token.Name, _serverName, type, token.MobMax, token.MobRate, loc, token.Radius, token.Height);
			Add(pit, false);
		}
	}

	private void Clean()
	{
		for (FieldMonsterBase pit : _pits)
			pit.RemoveMonsters();
	}
}
