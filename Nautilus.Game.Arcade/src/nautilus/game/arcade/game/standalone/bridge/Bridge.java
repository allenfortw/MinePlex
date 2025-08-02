package nautilus.game.arcade.game.standalone.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.explosion.ExplosionEvent;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerDeathOutEvent;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.standalone.bridge.kits.*;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.ore.OreHider;
import nautilus.game.arcade.ore.OreObsfucation;

public class Bridge extends TeamGame implements OreObsfucation
{
	//Bridge Timer
	private int _bridgeTime = 600000;
	private boolean _bridgesDown = false;

	//Wood Bridge
	private ArrayList<Location> _woodBridge = new ArrayList<Location>();
	private HashMap<Location, Integer> _woodBridgeBlocks = null;

	//Lava Bridge
	private ArrayList<Location> _lavaBridge = new ArrayList<Location>();
	private ArrayList<Location> _lavaSource = new ArrayList<Location>();

	//Bridge Parts
	private HashSet<BridgePart> _bridgeParts = new HashSet<BridgePart>();

	//Animals
	private long _lastAnimal = System.currentTimeMillis();
	private HashMap<GameTeam, HashSet<Entity>> _animalSet = new HashMap<GameTeam, HashSet<Entity>>();

	//Mushroom
	private long _lastMushroom = System.currentTimeMillis();
	
	//Chset Loot
	private ArrayList<ItemStack> _chestLoot = new ArrayList<ItemStack>();

	//Ore
	private OreHider _ore;
	private double _oreDensity = 2.2;


	//Map Flags
	private int _buildHeight = -1;
	private int _iceForm = -1;

	//Player Respawn
	private HashSet<String> _usedLife = new HashSet<String>();

	public Bridge(ArcadeManager manager)
	{
		super(manager, GameType.Bridge,

				new Kit[] 
						{ 
				new KitApple(manager),
				new KitBeserker(manager),
				new KitMammoth(manager),
				new KitMiner(manager),
				new KitArcher(manager),
				new KitBomber(manager),
						},

						new String[] { 
				"Gather resources and prepare for combat.",
				"After 10 minutes, The Bridges will emerge.",
				"Special loot is located in the center.",
				"The last team alive wins!" 
		});

		_ore = new OreHider();

		// Flags
		this.DamageSelf = true;
		
		this.ItemDrop = true;
		this.ItemPickup = true;
		
		this.PrivateBlocks = true;
		this.BlockBreak = true;
		this.BlockPlace = true;
		
		this.InventoryOpen = true;

		this.WorldTimeSet = 2000;
		
		this.WorldWaterDamage = 4;
		
		this.CompassEnabled = true;
	}

	@EventHandler
	public void PlayerOut(PlayerDeathOutEvent event)
	{
		if (_bridgesDown)
			return;

		Player player = event.GetPlayer();
		
		if (Manager.GetClients().Get(player).GetRank().Has(Rank.ULTRA) || Manager.GetDonation().Get(player.getName()).OwnsUnknownPackage(GetName() + " ULTRA"))
		{
			if (!_usedLife.contains(player.getName()))
			{
				_usedLife.add(player.getName());

				UtilPlayer.message(player, F.main("Game", "You used your " + F.elem(C.cAqua + "Ultra Rank Revive") + "."));

				event.setCancelled(true);
			}
		}
		else
		{
			UtilPlayer.message(player, F.main("Game", "Purchase " + F.elem(C.cAqua + "Ultra Rank") + " to revive during pre-game!"));
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void GameStateChange(GameStateChangeEvent event)
	{
		if (event.GetState() != GameState.Live)
			return;
		
		if (WorldWaterDamage > 0)
			Announce(F.main(C.cWhite + C.Bold + "WARNING", C.cRed + C.Bold + "Water is very hot/cold and will hurt you!"));
	}
	
	@Override
	public void ParseData() 
	{
		if (!WorldData.GetDataLocs("WHITE").isEmpty())
			WorldWaterDamage = 4;

		ParseLavaBridge();
		ParseWoodBridge();
		ParseIceBridge();

		ParseChests();

		ParseOre(WorldData.GetCustomLocs("73")); // Red
		ParseOre(WorldData.GetCustomLocs("14")); // Yellow
		ParseOre(WorldData.GetCustomLocs("129")); // Green
		ParseOre(WorldData.GetCustomLocs("56")); // Blue
	}

	private void ParseChests()
	{
		for (Location loc : WorldData.GetCustomLocs("54")) 
		{
			if (loc.getBlock().getType() != Material.CHEST)
				loc.getBlock().setType(Material.CHEST);

			Chest chest = (Chest) loc.getBlock().getState();

			chest.getBlockInventory().clear();

			int count = 2 + UtilMath.r(5);
			for (int i = 0; i < count; i++) 
			{
				chest.getBlockInventory().addItem(GetChestItem());
			}
		}
	}

	private ItemStack GetChestItem()
	{
		if (_chestLoot.isEmpty()) 
		{
			for (int i = 0; i < 1; i++)
				_chestLoot.add(new ItemStack(Material.DIAMOND_HELMET));
			for (int i = 0; i < 1; i++)
				_chestLoot.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
			for (int i = 0; i < 1; i++)
				_chestLoot.add(new ItemStack(Material.DIAMOND_LEGGINGS));
			for (int i = 0; i < 1; i++)
				_chestLoot.add(new ItemStack(Material.DIAMOND_BOOTS));
			for (int i = 0; i < 1; i++)
				_chestLoot.add(new ItemStack(Material.DIAMOND_SWORD));
			for (int i = 0; i < 1; i++)
				_chestLoot.add(new ItemStack(Material.DIAMOND_AXE));
			for (int i = 0; i < 1; i++)
				_chestLoot.add(new ItemStack(Material.DIAMOND_PICKAXE));

			for (int i = 0; i < 6; i++)
				_chestLoot.add(new ItemStack(Material.IRON_HELMET));
			for (int i = 0; i < 6; i++)
				_chestLoot.add(new ItemStack(Material.IRON_CHESTPLATE));
			for (int i = 0; i < 6; i++)
				_chestLoot.add(new ItemStack(Material.IRON_LEGGINGS));
			for (int i = 0; i < 6; i++)
				_chestLoot.add(new ItemStack(Material.IRON_BOOTS));
			for (int i = 0; i < 6; i++)
				_chestLoot.add(new ItemStack(Material.IRON_SWORD));
			for (int i = 0; i < 6; i++)
				_chestLoot.add(new ItemStack(Material.IRON_AXE));
			for (int i = 0; i < 6; i++)
				_chestLoot.add(new ItemStack(Material.IRON_PICKAXE));

			for (int i = 0; i < 18; i++)
				_chestLoot.add(new ItemStack(Material.BOW));
			for (int i = 0; i < 24; i++)
				_chestLoot.add(new ItemStack(Material.ARROW, 8));

			for (int i = 0; i < 48; i++)
				_chestLoot.add(new ItemStack(Material.MUSHROOM_SOUP));
			for (int i = 0; i < 24; i++)
				_chestLoot.add(new ItemStack(Material.COOKED_CHICKEN, 2));
		}

		ItemStack stack = _chestLoot.get(UtilMath.r(_chestLoot.size()));

		int amount = 1;

		if (stack.getType().getMaxStackSize() > 1)
			amount = stack.getAmount() + UtilMath.r(stack.getAmount());

		return ItemStackFactory.Instance.CreateStack(stack.getTypeId(), amount);
	}

	private void ParseOre(ArrayList<Location> teamOre)
	{
		int coal = (int) ((teamOre.size() / 32d) * _oreDensity);
		int iron = (int) ((teamOre.size() / 24d) * _oreDensity);
		int gold = (int) ((teamOre.size() / 64d) * _oreDensity);
		int diamond = 1 + (int) ((teamOre.size() / 128d) * _oreDensity);

		int gravel = (int) ((teamOre.size() / 64d) * _oreDensity);

		int lowY = 256;
		int highY = 0;

		for (Location loc : teamOre)
		{
			if (loc.getBlockY() < lowY)
				lowY = loc.getBlockY();

			if (loc.getBlockY() > highY)
				highY = loc.getBlockY();

			loc.getBlock().setTypeId(1);
		}

		int varY = highY - lowY;

		//Gravel
		for (int i = 0; i < gravel && !teamOre.isEmpty(); i++) 
		{
			int attempts = 20;
			int id = 0;

			while (attempts > 0) 
			{
				id = UtilMath.r(teamOre.size());

				double height = (double) (teamOre.get(id).getBlockY() - lowY) / (double) varY;

				if (height > 0.8)
					break;

				else if (height > 0.6 && Math.random() > 0.4)
					break;

				else if (height > 0.4 && Math.random() > 0.6)
					break;

				else if (height > 0.2 && Math.random() > 0.8)
					break;
			}

			CreateOre(teamOre.remove(id), Material.GRAVEL, 6);
		}

		//Coal
		for (int i = 0; i < coal && !teamOre.isEmpty(); i++) 
		{
			int attempts = 20;
			int id = 0;

			while (attempts > 0) 
			{
				id = UtilMath.r(teamOre.size());

				double height = (double) (teamOre.get(id).getBlockY() - lowY) / (double) varY;

				if (height > 0.8)
					break;

				else if (height > 0.6 && Math.random() > 0.4)
					break;

				else if (height > 0.4 && Math.random() > 0.6)
					break;

				else if (height > 0.2 && Math.random() > 0.8)
					break;
			}

			CreateOre(teamOre.remove(id), Material.COAL_ORE, 6);
		}

		//Iron
		for (int i = 0; i < iron && !teamOre.isEmpty(); i++) 
		{
			int id = UtilMath.r(teamOre.size());

			CreateOre(teamOre.remove(id), Material.IRON_ORE, 3);
		}

		//Gold
		for (int i = 0; i < gold && !teamOre.isEmpty(); i++) 
		{
			int attempts = 20;
			int id = 0;

			while (attempts > 0) 
			{
				id = UtilMath.r(teamOre.size());

				double height = (double) (teamOre.get(id).getBlockY() - lowY)
						/ (double) varY;

				if (height > 0.8 && Math.random() > 0.8)
					break;

				else if (height > 0.6 && Math.random() > 0.7)
					break;

				else if (height > 0.4 && Math.random() > 0.6)
					break;

				else if (height > 0.2 && Math.random() > 0.4)
					break;

				else if (Math.random() > 0.2)
					break;
			}

			CreateOre(teamOre.remove(id), Material.GOLD_ORE, 3);
		}

		//Diamond
		for (int i = 0; i < diamond && !teamOre.isEmpty(); i++)
		{
			int attempts = 20;
			int id = 0;

			while (attempts > 0)
			{
				id = UtilMath.r(teamOre.size());

				double height = (double) (teamOre.get(id).getBlockY() - lowY)
						/ (double) varY;

				if (height > 0.8)
					continue;

				else if (height > 0.6 && Math.random() > 0.9)
					break;

				else if (height > 0.4 && Math.random() > 0.7)
					break;

				else if (height > 0.2 && Math.random() > 0.5)
					break;

				else
					break;
			}

			CreateOre(teamOre.remove(id), Material.DIAMOND_ORE, 2);
		}
	}

	private void CreateOre(Location loc, Material type, int amount) 
	{
		double bonus = Math.random() + 1;

		amount = (int) (amount * bonus);

		int attempts = 100;
		while (amount > 0 && attempts > 0) 
		{
			attempts--;

			loc.add(1 - UtilMath.r(3), 1 - UtilMath.r(3), 1 - UtilMath.r(3));

			if (loc.getBlock().getTypeId() != 1)
				continue;

			_ore.AddOre(loc, type);

			amount--;
		}
	}

	private void ParseWoodBridge() {
		_woodBridge = new ArrayList<Location>();

		// Load Wood In
		for (Location loc : WorldData.GetDataLocs("BROWN")) {
			_woodBridge.add(loc.getBlock().getLocation());
		}

		for (Location loc : WorldData.GetDataLocs("GRAY")) {
			_woodBridge.add(loc.getBlock().getLocation());
			_woodBridge.add(loc.getBlock().getRelative(BlockFace.UP)
					.getLocation());
		}

		// Determine Wood Block
		_woodBridgeBlocks = new HashMap<Location, Integer>();

		for (Location loc : _woodBridge) {
			if (_woodBridge.contains(loc.getBlock().getRelative(BlockFace.DOWN)
					.getLocation())) {
				_woodBridgeBlocks.put(loc, 85);
			}

			if (_woodBridge.contains(loc.getBlock().getRelative(BlockFace.UP)
					.getLocation())) {
				_woodBridgeBlocks.put(loc, 17);
			}

			if (!_woodBridgeBlocks.containsKey(loc)) {
				_woodBridgeBlocks.put(loc, 126);
			}
		}
	}

	private void ParseLavaBridge() {
		for (Location loc : WorldData.GetDataLocs("RED")) {
			_lavaBridge.add(loc.getBlock().getLocation());
		}

		for (Location loc : WorldData.GetDataLocs("ORANGE")) {
			_lavaBridge.add(loc.getBlock().getLocation());
			_lavaBridge.add(loc.getBlock().getRelative(BlockFace.UP)
					.getLocation());
		}

		_lavaSource = WorldData.GetDataLocs("BLACK");
	}

	private void ParseIceBridge() 
	{
		if (WorldData.GetCustomLocs("WATER_HEIGHT").isEmpty())
			return;

		_iceForm = WorldData.GetCustomLocs("WATER_HEIGHT").get(0).getBlockY();
	}

	@EventHandler
	public void BridgeBuild(UpdateEvent event) 
	{
		if (!IsLive())
			return;

		if (event.getType() != UpdateType.FASTEST)
			return;

		if (!UtilTime.elapsed(this.GetStateTime(), _bridgeTime))
			return;

		_bridgesDown = true;

		BuildWood();
		BuildLava();
		BuildIce();
	}

	private void BuildIce() 
	{
		if (_iceForm <= 0)
			return;

		if (UtilTime.elapsed(this.GetStateTime(), _bridgeTime + 120000))
		{
			WorldData.World.setStorm(false);
			return;
		}

		WorldData.World.setStorm(true);

		//Short Delay (so snow properly starts)
		if (!UtilTime.elapsed(this.GetStateTime(), _bridgeTime + 6000))
			return;

		int xVar = WorldData.MaxX - WorldData.MinX;
		int zVar = WorldData.MaxZ - WorldData.MinZ;

		//do area
		BuildIceArea(WorldData.MinX, WorldData.MinX + xVar/2, WorldData.MinZ, WorldData.MinZ + zVar/2, Material.REDSTONE_BLOCK);
		BuildIceArea(WorldData.MinX + xVar/2, WorldData.MaxX, WorldData.MinZ, WorldData.MinZ + zVar/2, Material.GOLD_BLOCK);
		BuildIceArea(WorldData.MinX, WorldData.MinX + xVar/2, WorldData.MinZ + zVar/2, WorldData.MaxZ, Material.EMERALD_BLOCK);
		BuildIceArea(WorldData.MinX + xVar/2, WorldData.MaxX, WorldData.MinZ + zVar/2, WorldData.MaxZ, Material.DIAMOND_BLOCK);
	}

	private void BuildIceArea(int xLow, int xHigh, int zLow, int zHigh, Material mat)
	{
		int attempts = 1000;
		int complete = 10;

		//Team A
		while (attempts > 0 && complete > 0)
		{
			attempts--;

			int x = xLow + UtilMath.r(xHigh - xLow);
			int z = zLow + UtilMath.r(zHigh - zLow);

			Block block = WorldData.World.getBlockAt(x, _iceForm, z);

			if (!block.isLiquid())
				continue;

			if (block.getRelative(BlockFace.NORTH).isLiquid() &&
					block.getRelative(BlockFace.EAST).isLiquid() &&
					block.getRelative(BlockFace.SOUTH).isLiquid() &&
					block.getRelative(BlockFace.WEST).isLiquid())			
				continue;

			block.setType(Material.ICE);

			complete--;
		}
	}

	private void BuildLava() 
	{
		for (int i = 0; i < 3; i++)
			if (_lavaBridge != null && _lavaSource != null
			&& !_lavaBridge.isEmpty() && !_lavaSource.isEmpty()) {
				// Random Block
				Location bestLoc = _lavaBridge.get(UtilMath.r(_lavaBridge
						.size()));

				if (bestLoc.getBlock().getRelative(BlockFace.DOWN)
						.isLiquid())
					continue;

				_lavaBridge.remove(bestLoc);

				Location source = _lavaSource.get(UtilMath.r(_lavaSource
						.size()));

				// Create Part
				FallingBlock block = bestLoc.getWorld().spawnFallingBlock(
						source, 10, (byte) 0);
				BridgePart part = new BridgePart(block, bestLoc, true);
				_bridgeParts.add(part);

				// Sound
				source.getWorld().playSound(source, Sound.EXPLODE,
						5f * (float) Math.random(),
						0.5f + (float) Math.random());
			}
	}

	private void BuildWood() 
	{
		if (_woodBridgeBlocks != null && !_woodBridgeBlocks.isEmpty()) 
		{
			ArrayList<Location> toDo = new ArrayList<Location>();

			BlockFace[] faces = new BlockFace[] { BlockFace.NORTH,
					BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

			for (Location loc : _woodBridgeBlocks.keySet()) 
			{
				if (_woodBridgeBlocks.get(loc) == 17) 
				{
					int adjacent = 0;

					for (BlockFace face : faces)
						if (loc.getBlock().getRelative(face).getTypeId() != 0)
							adjacent++;

					if (adjacent > 0)
						toDo.add(loc);

				} else if (_woodBridgeBlocks.get(loc) == 85) 
				{
					if (loc.getBlock().getRelative(BlockFace.DOWN).getTypeId() == 0)
						continue;

					toDo.add(loc);
				} else if (_woodBridgeBlocks.get(loc) == 126)
				{
					int adjacent = 0;

					for (BlockFace face : faces)
						if (loc.getBlock().getRelative(face).getTypeId() != 0)
							adjacent++;

					if (adjacent > 0)
						toDo.add(loc);
				}
			}

			if (toDo.size() == 0)
				return;

			for (Location loc : toDo)
			{
				int id = _woodBridgeBlocks.remove(loc);

				Location source = loc.clone().add(0, 30, 0);

				// Create Part
				FallingBlock block = loc.getWorld().spawnFallingBlock(source,
						id, (byte) 0);
				block.setVelocity(new Vector(0, -1, 0));
				BridgePart part = new BridgePart(block, loc, false);
				_bridgeParts.add(part);
			}
		}
	}

	@EventHandler
	public void BridgeUpdate(UpdateEvent event)
	{
		if (!IsLive())
			return;

		if (event.getType() != UpdateType.TICK)
			return;

		Iterator<BridgePart> partIterator = _bridgeParts.iterator();

		while (partIterator.hasNext()) 
		{
			BridgePart part = partIterator.next();

			if (part.Update())
				partIterator.remove();
		}
	}

	@EventHandler
	public void BridgeForm(EntityChangeBlockEvent event)
	{
		for (BridgePart part : _bridgeParts)
			if (part.Entity.equals(event.getEntity())) 
				event.setCancelled(true);
	}

	@EventHandler
	public void BridgeItem(ItemSpawnEvent event) 
	{
		for (BridgePart part : _bridgeParts)
			if (part.ItemSpawn(event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler
	public void IceForm(BlockFormEvent event) 
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void AnimalSpawn(UpdateEvent event)
	{
		if (!IsLive())
			return;

		if (event.getType() != UpdateType.SEC)
			return;

		if (!UtilTime.elapsed(_lastAnimal, 30000))
			return;

		_lastAnimal = System.currentTimeMillis();

		for (GameTeam team : GetTeamList()) 
		{
			if (_animalSet.get(team) == null)
				_animalSet.put(team, new HashSet<Entity>());

			// Clean
			Iterator<Entity> entIterator = _animalSet.get(team).iterator();

			while (entIterator.hasNext())
			{
				Entity ent = entIterator.next();

				if (ent.isDead() || !ent.isValid())
					entIterator.remove();
			}

			// Too Many
			if (_animalSet.get(team).size() > 4)
				continue;

			// Spawn
			double rand = Math.random();

			Entity ent;

			CreatureAllowOverride = true;
			if (rand > 0.66)
				ent = team.GetSpawn().getWorld().spawn(team.GetSpawn(), Cow.class);
			else if (rand > 0.33)
				ent = team.GetSpawn().getWorld().spawn(team.GetSpawn(), Pig.class);
			else
				ent = team.GetSpawn().getWorld().spawn(team.GetSpawn(), Chicken.class);
			CreatureAllowOverride = false;

			_animalSet.get(team).add(ent);
		}
	}
	
	@EventHandler
	public void MushroomSpawn(UpdateEvent event)
	{
		if (!IsLive())
			return;

		if (event.getType() != UpdateType.SEC)
			return;

		if (!UtilTime.elapsed(_lastMushroom, 20000))
			return;

		_lastMushroom = System.currentTimeMillis();

		for (GameTeam team : GetTeamList()) 
		{
			Block block = team.GetSpawn().getBlock();
			
			while (!UtilBlock.airFoliage(block))
			{
				block = block.getRelative(BlockFace.UP);
			}
			
			while (UtilBlock.airFoliage(block))
			{
				block = block.getRelative(BlockFace.DOWN);
			}
			
			block = block.getRelative(BlockFace.UP);
			
			if (block.getTypeId() == 0)
			{
				if (Math.random() > 0.5)
					block.setTypeId(39);
				else
					block.setTypeId(40);
			}
		}
	}

	@EventHandler
	public void OreReveal(ExplosionEvent event) 
	{
		_ore.Explosion(event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void OreReveal(BlockBreakEvent event) 
	{
		if (event.isCancelled())
			return;

		_ore.BlockBreak(event);
	}

	@EventHandler
	public void OreRevealToggle(AsyncPlayerChatEvent event) 
	{
		if (!Manager.GetClients().Get(event.getPlayer()).GetRank().Has(Rank.OWNER))
			return;

		if (event.getMessage().contains("toggleorevisibility"))
			_ore.ToggleVisibility();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void BlockPlace(BlockPlaceEvent event) 
	{
		if (event.isCancelled())
			return;

		if (!IsAlive(event.getPlayer()))
			return;

		//Too High
		if (event.getBlock().getLocation().getBlockY() > GetHeightLimit())
		{
			UtilPlayer.message(event.getPlayer(), F.main("Game",
					"Cannot place blocks this high up."));
			event.setCancelled(true);
			return;
		}

		if (_bridgesDown)
			return;

		//In Liquid
		if (event.getBlockReplacedState().getTypeId() == 8 ||
				event.getBlockReplacedState().getTypeId() == 9 ||
				event.getBlockReplacedState().getTypeId() == 10 ||
				event.getBlockReplacedState().getTypeId() == 11)
		{
			UtilPlayer.message(event.getPlayer(), F.main("Game",
					"Cannot place blocks in liquids until Bridge is down."));
			event.setCancelled(true);
			return;
		}

		//Above Water/Void
		for (int i = 1; i <= event.getBlock().getLocation().getY(); i++) 
		{
			Block below = event.getBlock().getRelative(BlockFace.DOWN, i);

			if (below.isLiquid())
			{
				UtilPlayer
				.message(
						event.getPlayer(),
						F.main("Game",
								"Cannot place blocks above water until Bridge is down."));
				event.setCancelled(true);
				return;
			}

			if (event.getBlock().getLocation().getY() - i <= 0) 
			{
				UtilPlayer
				.message(
						event.getPlayer(),
						F.main("Game",
								"Cannot place blocks above void until Bridge is down."));
				event.setCancelled(true);
				return;
			}

			if (below.getTypeId() != 0)
				break;
		}
	}

	@EventHandler
	public void BridgeTimer(UpdateEvent event) 
	{
		if (GetState() != GameState.Live)
			return;

		if (event.getType() != UpdateType.TICK)
			return;

		long time = _bridgeTime
				- (System.currentTimeMillis() - this.GetStateTime());
		
		if (time > 0)
			GetObjectiveSide().setDisplayName(
					ChatColor.WHITE + "§lBridges in " + C.cGreen + "§l"
							+ UtilTime.MakeStr(time));
		else
			GetObjectiveSide().setDisplayName(
					ChatColor.WHITE + "§lBridges are down!");
	}

	@EventHandler(priority = EventPriority.LOW)
	public void ChestProtect(EntityExplodeEvent event) 
	{
		Iterator<Block> blockIterator = event.blockList().iterator();

		while (blockIterator.hasNext()) 
		{
			Block block = blockIterator.next();

			if (block.getType() == Material.CHEST
					|| block.getType() == Material.FURNACE
					|| block.getType() == Material.BURNING_FURNACE
					|| block.getType() == Material.WORKBENCH)
				blockIterator.remove();
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void BucketEmpty(PlayerBucketEmptyEvent event) 
	{
		if (WorldWaterDamage <= 0)
			return;

		if (event.getBucket() == Material.WATER_BUCKET)
		{
			UtilPlayer.message(
					event.getPlayer(),
					F.main("Game", "Cannot use " + F.elem("Water Bucket") + " on this map."));
			event.setCancelled(true);
		}
	}

	public int GetHeightLimit()
	{
		if (_buildHeight == -1)
		{
			_buildHeight = 0;
			int amount = 0;

			for (GameTeam team : GetTeamList())
				for (Location loc : team.GetSpawns())
				{
					_buildHeight += loc.getBlockY();
					amount++;
				}


			_buildHeight = _buildHeight / amount;
		}

		return _buildHeight + 24;
	}

	@Override
	public OreHider GetOreHider() 
	{
		return _ore;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void CraftingDeny(PrepareItemCraftEvent event)
	{
		if (event.getRecipe().getResult() == null)
			return;

		Material type = event.getRecipe().getResult().getType();

		if (type != Material.GOLDEN_APPLE &&
				type != Material.GOLDEN_CARROT && 
				type != Material.FLINT_AND_STEEL)
			return;

		if (!(event.getInventory() instanceof CraftingInventory))
			return;

		CraftingInventory inv = (CraftingInventory)event.getInventory();
		inv.setResult(null);
	}
}
