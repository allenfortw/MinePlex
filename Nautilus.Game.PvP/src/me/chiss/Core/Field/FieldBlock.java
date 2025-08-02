package me.chiss.Core.Field;

import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;

import nautilus.minecraft.core.webserver.token.Server.FieldBlockToken;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.chiss.Core.Module.AModule;
import mineplex.core.server.IRepository;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.common.util.UtilEvent.ActionType;

public class FieldBlock extends AModule
{
	private HashMap<String, FieldBlockData> _blocks;

	private HashSet<String> _active = new HashSet<String>();
	
	//Player Info
	private WeakHashMap<Player, String> _title = new WeakHashMap<Player, String>();
	private WeakHashMap<Player, Integer> _stock = new WeakHashMap<Player, Integer>();
	private WeakHashMap<Player, Double> _regen = new WeakHashMap<Player, Double>();
	private WeakHashMap<Player, Integer> _emptyId = new WeakHashMap<Player, Integer>();
	private WeakHashMap<Player, Byte> _emptyData = new WeakHashMap<Player, Byte>();
	private WeakHashMap<Player, String> _lootString = new WeakHashMap<Player, String>();

	private String _serverName;
	
	public FieldBlock(JavaPlugin plugin, IRepository repository, String serverName) 
	{
		super("Field Block", plugin, repository);
		
		_blocks = new HashMap<String, FieldBlockData>();
		
		_serverName = serverName;
		
		load();
	}

	//Module Functions
	@Override
	public void enable() 
	{
		
	}

	@Override
	public void disable() 
	{
		clean();
	}

	@Override
	public void config() 
	{

	}

	@Override
	public void commands() 
	{
		AddCommand("fb");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!Clients().Get(caller).Rank().Has(Rank.ADMIN, true))
			return;

		if (args.length == 0)
		{
			showSettings(caller);
			return;
		}
		
		if (args[0].equalsIgnoreCase("toggle"))
		{
			if (!_active.remove(caller.getName()))
				_active.add(caller.getName());
			
			UtilPlayer.message(caller, F.main(GetName(), "Interact Active: " + F.tf(_active.contains(caller.getName()))));
		}

		else if (args[0].equalsIgnoreCase("load"))
		{
			load();
			UtilPlayer.message(caller, F.main(_moduleName, "Reloaded Field Blocks from Database."));
		}

		else if (args[0].equalsIgnoreCase("wipe"))
		{
			wipe(caller);
		}

		else if (args[0].equalsIgnoreCase("help"))
		{
			help(caller);
		}

		else if (args.length <= 1)
		{
			help(caller);
		}

		else if (args[0].equalsIgnoreCase("title"))
		{
			_title.put(caller, args[1]);
			showSettings(caller);
		}

		else if (args[0].equalsIgnoreCase("stock"))
		{
			try
			{
				int count = Integer.parseInt(args[1]);
				if (count < 1)	count = 1;
				_stock.put(caller, count);
				showSettings(caller);
			}
			catch (Exception e)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Invalid Stock Max."));
			}
		}

		else if (args[0].equalsIgnoreCase("regen"))
		{
			try
			{
				double regen = Double.parseDouble(args[1]);
				if (regen < 0)	regen = 0;
				_regen.put(caller, UtilMath.trim(1, regen));
				showSettings(caller);
			}
			catch (Exception e)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Invalid Stock Regeneration Time."));
			}
		}

		else if (args[0].equalsIgnoreCase("empty"))
		{
			try
			{
				String[] toks = args[1].split(":");

				int id = Integer.parseInt(toks[0]);
				byte data = Byte.parseByte(toks[1]);

				_emptyId.put(caller, id);
				_emptyData.put(caller, data);
				showSettings(caller);
			}
			catch (Exception e)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Invalid Empty Block."));
			}
		}


		else if (args[0].equalsIgnoreCase("loot"))
		{
			boolean error = false;
			for (String cur : args[1].split(","))
			{
				String[] loot = cur.split(":");

				if (loot.length != 5)
				{
					error = true;
					break;
				}

				try
				{
					Integer.parseInt(loot[0]);
					Byte.parseByte(loot[1]);
					Integer.parseInt(loot[2]);
					Integer.parseInt(loot[3]);
					Integer.parseInt(loot[4]);	
				}
				catch (Exception e)
				{
					error = true;
					break;
				}
			}

			if (error)
			{
				UtilPlayer.message(caller, F.main(_moduleName, "Invalid Loot String."));
				return;
			}

			_lootString.put(caller, args[1]);
			showSettings(caller);
		}
	}

	private void showSettings(Player caller) 
	{
		populateSettings(caller);

		UtilPlayer.message(caller, F.main(_moduleName, "Field Addition Settings;"));
		UtilPlayer.message(caller, F.desc("Title", _title.get(caller)));
		UtilPlayer.message(caller, F.desc("Stock", _stock.get(caller)+""));
		UtilPlayer.message(caller, F.desc("Regen", _regen.get(caller)+""));
		UtilPlayer.message(caller, F.desc("Empty", _emptyId.get(caller) + ":" + _emptyData.get(caller)));
		UtilPlayer.message(caller, F.desc("Loot", _lootString.get(caller)));
	}

	private void populateSettings(Player caller) 
	{
		if (!_title.containsKey(caller))		_title.put(caller, "Default");
		if (!_stock.containsKey(caller))		_stock.put(caller, 1);
		if (!_regen.containsKey(caller))		_regen.put(caller, 3d);
		if (!_emptyId.containsKey(caller))		_emptyId.put(caller, 1);
		if (!_emptyData.containsKey(caller))	_emptyData.put(caller, (byte)0);
		if (!_lootString.containsKey(caller))	_lootString.put(caller, "263:0:2:1:50");
	}

	public void help(Player caller) 
	{
		UtilPlayer.message(caller, F.main(_moduleName, "Commands List;"));
		UtilPlayer.message(caller, F.help("/fo toggle", "Toggle Tools", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fb loot <ID:Data:Base:Bonus:Chance,etc>", "Set Loot", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fb empty <ID:Data>", "Set Empty Block", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fb stock <#>", "Set Stock Max", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fb regen <Minutes>", "Set Stock Regen", Rank.ADMIN));
		UtilPlayer.message(caller, F.help("/fb wipe", "Delete All Block Fields (Database)", Rank.ADMIN));
	}

	@EventHandler
	public void handleInteract(PlayerInteractEvent event)
	{
		if (!_active.contains(event.getPlayer().getName()))
			return;
		
		if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().toString().contains("PICKAXE"))
		{
			if (Util().Event().isAction(event, ActionType.R_BLOCK))
				showBlock(event.getPlayer(), event);
		}

		else if (Util().Gear().isMat(event.getPlayer().getItemInHand(), Material.GLOWSTONE_DUST))
		{
			if (Util().Event().isAction(event, ActionType.L_BLOCK))
				addBlock(event.getPlayer(), event);

			else if (Util().Event().isAction(event, ActionType.R))
				glowBlocks(event.getPlayer(), event);
		}

		else if (Util().Gear().isMat(event.getPlayer().getItemInHand(), Material.REDSTONE))
		{
			if (Util().Event().isAction(event, ActionType.L_BLOCK))
				delBlock(event.getPlayer(), event);

			else if (Util().Event().isAction(event, ActionType.R))
				glowBlocks(event.getPlayer(), event);
		}
	}

	private void glowBlocks(Player player, PlayerInteractEvent event) 
	{
		if (!Clients().Get(player).Rank().Has(Rank.ADMIN, false))
			return;

		load();

		for (FieldBlockData cur : _blocks.values())
			cur.getBlock().setTypeId(89);

		event.setCancelled(true);
	}

	private void wipe(Player player)
	{
		for (FieldBlockData cur : _blocks.values())
		{
			cur.setEmpty();
			GetRepository().DeleteFieldBlock(_serverName, UtilWorld.locToStr(cur.getLocation()));
		}

		_blocks.clear();

		UtilPlayer.message(player, F.main(_moduleName, "Full Field Wipe Completed."));
	}

	private void delBlock(Player player, PlayerInteractEvent event) 
	{
		if (!Clients().Get(player).Rank().Has(Rank.ADMIN, false))
			return;

		GetRepository().DeleteFieldBlock(_serverName, UtilWorld.locToStr(event.getClickedBlock().getLocation()));

		//Inform
		event.getClickedBlock().getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, 42);
		UtilPlayer.message(player, F.main(_moduleName, "Field Block removed."));

		event.setCancelled(true);
	}

	private void addBlock(Player player, PlayerInteractEvent event) 
	{
		if (!Clients().Get(player).Rank().Has(Rank.ADMIN, false))
			return;

		populateSettings(player);
		showSettings(player);

		FieldBlockToken token = new FieldBlockToken();

		token.Server = _serverName;
		token.Location = UtilWorld.locToStr(event.getClickedBlock().getLocation());
		token.BlockId = event.getClickedBlock().getTypeId();
		token.BlockData = event.getClickedBlock().getData();
		token.EmptyId = _emptyId.get(player);
		token.EmptyData = _emptyData.get(player);
		token.StockMax = _stock.get(player);
		token.StockRegenTime = _regen.get(player);
		token.Loot = _lootString.get(player);	

		GetRepository().AddFieldBlock(token);

		//Inform
		event.getClickedBlock().getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, 41);
		UtilPlayer.message(player, F.main(_moduleName, "Field Block added."));

		event.setCancelled(true);
	}

	private void showBlock(Player player, PlayerInteractEvent event) 
	{
		FieldBlockData fieldBlock = getFieldBlock(event.getClickedBlock());
		if (fieldBlock == null)		return;

		fieldBlock.showInfo(player);

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled())
			return;
			
		FieldBlockData fieldBlock = getFieldBlock(event.getBlock());
		if (fieldBlock == null)		return;

		fieldBlock.handleMined(event.getPlayer());
		event.setCancelled(true);
	}

	@EventHandler
	public void update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SEC)
			for (FieldBlockData cur : _blocks.values())
				cur.regen();

		if (event.getType() == UpdateType.FAST)
			for (FieldBlockData cur : _blocks.values())
				cur.check();
	}

	public void load()
	{
		clean();

		for (FieldBlockToken token : GetRepository().GetFieldBlocks(_serverName))
		{
			Location loc = UtilWorld.strToLoc(token.Location);
			_blocks.put(token.Location, new FieldBlockData(this, loc, token.BlockId, token.BlockData, token.EmptyId, token.EmptyData, token.StockMax, token.StockRegenTime, token.Loot));
		}
	}

	public void clean()
	{
		for (FieldBlockData cur : _blocks.values())
			cur.clean();

		_blocks.clear();
	}

	public FieldBlockData getFieldBlock(Block block) 
	{
		if (_blocks.containsKey(UtilWorld.locToStr(block.getLocation())))
			return _blocks.get(UtilWorld.locToStr(block.getLocation()));

		return null;
	}	
}
