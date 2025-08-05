package nautilus.game.tutorial;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import mineplex.core.Rank;
import mineplex.core.itemstack.ItemStackFactory;
import me.chiss.Core.Module.AModule;
import mineplex.core.server.IRepository;
import mineplex.core.server.util.Callback;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilWorld;
import mineplex.minecraft.account.CoreClient;
import mineplex.minecraft.donation.repository.token.PlayerUpdateToken;
import mineplex.minecraft.game.classcombat.SkillTriggerEvent;
import mineplex.minecraft.game.classcombat.events.ClassSetupEvent;
import nautilus.game.tutorial.part.Part;
import nautilus.game.tutorial.part.a_welcome.A01_Welcome;
import nautilus.game.tutorial.part.b_class_basics.*;
import nautilus.game.tutorial.part.c_locked_skills.C01_Brute;
import nautilus.game.tutorial.part.d_class_builds.D01_CustomBuild;
import nautilus.game.tutorial.part.m_icons.M01_Icons;
import nautilus.game.tutorial.part.q_secret.Q01_Secret;
import nautilus.game.tutorial.part.z_domination.Z01_Introduction;

public class TutorialManager extends AModule
{
	public enum TutorialType
	{
		A("Welcome"),
		B("Class Basics"),
		C("Locked Skills"),
		D("Custom Builds"),
		M("Condition Icons"),
		Q("???"),
		Z("Domination");	

		private String _name;

		TutorialType(String name)
		{
			_name = name;
		}

		public String GetName()
		{
			return _name;
		}
	}

	private HashMap<Player, TutorialData> _data = new HashMap<Player, TutorialData>();

	//Objects
	public Entity domItemEmerald = null;
	public Entity domItemChest = null;
	
	//Condition Icons
	public HashSet<IconData> icons;

	//Spawns
	public Location spawnA = new Location(UtilWorld.getWorldType(Environment.NORMAL), 0.5, 13, -4.5);
	public Location spawnB = new Location(UtilWorld.getWorldType(Environment.NORMAL), 5.5, 33, -14.5);
	public Location spawnC = new Location(UtilWorld.getWorldType(Environment.NORMAL), 3.5, 61, -54.5);

	//Wool
	public Location redWool = new Location(UtilWorld.getWorldType(Environment.NORMAL), -1, 14.5, 5);
	public Location greenWool = new Location(UtilWorld.getWorldType(Environment.NORMAL), 2, 14.5, 5);

	//NPC
	public Location tutorials = new Location(UtilWorld.getWorldType(Environment.NORMAL), -9.5, 57.5, 18.5);

	//Tables
	public Location classShop = new Location(UtilWorld.getWorldType(Environment.NORMAL), 8.5, 57.5, 17.5);
	public Location classSetup = new Location(UtilWorld.getWorldType(Environment.NORMAL), -4.5, 57.5, 30.5);

	//Animals
	public Location sheepPit = new Location(UtilWorld.getWorldType(Environment.NORMAL), 63, 57, 3);

	//Secret
	public Location secPig = new Location(UtilWorld.getWorldType(Environment.NORMAL), -39, 85, 49);
	public Location secChick = new Location(UtilWorld.getWorldType(Environment.NORMAL), 55, 76, 36);
	public Location secSpider = new Location(UtilWorld.getWorldType(Environment.NORMAL), 46, 48, -18);
	public Location secPortal = new Location(UtilWorld.getWorldType(Environment.NORMAL), 30.5, 54, 56.5);
	public Location secCat = new Location(UtilWorld.getWorldType(Environment.NORMAL), -13.5, 25, -0.5);
	
	//Domination
	public Location domIsland = new Location(UtilWorld.getWorldType(Environment.NORMAL), -45.5, 58.5, -43.5);
	public Location domCP = new Location(UtilWorld.getWorldType(Environment.NORMAL), -45.5, 59.5, -50.5);
	public Location domResupply = new Location(UtilWorld.getWorldType(Environment.NORMAL), -64.5, 57.5, -39.5);
	public Location domEmerald = new Location(UtilWorld.getWorldType(Environment.NORMAL), -34.5, 60.5, -50.5);

	public TutorialManager(JavaPlugin plugin, IRepository repository) 
	{
		super("Tutorial Manager", plugin, repository);
		
		AddIndicators();
	}

	private void AddIndicators() 
	{
		icons = new HashSet<IconData>();
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -21.5, 58.5, 17.5),
				Material.IRON_CHESTPLATE, (byte)0));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -21.5, 58.5, 19.5),
				Material.INK_SACK, (byte)1));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -21.5, 58.5, 21.5),
				Material.WEB, (byte)0));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -21.5, 58.5, 23.5),
				Material.FEATHER, (byte)0));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -21.5, 58.5, 25.5),
				Material.IRON_SWORD, (byte)0));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -21.5, 58.5, 27.5),
				Material.CARROT_ITEM, (byte)0));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -21.5, 58.5, 29.5),
				Material.BLAZE_POWDER, (byte)0));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -19.5, 58.5, 31.5),
				Material.DEAD_BUSH, (byte)0));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -17.5, 58.5, 31.5),
				Material.SLIME_BALL, (byte)0));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -15.5, 58.5, 31.5),
				Material.ENDER_PEARL, (byte)0));
		
		icons.add(new IconData(new Location(UtilWorld.getWorldType(Environment.NORMAL), -13.5, 58.5, 31.5),
				Material.WATCH, (byte)0));
	}

	@Override
	public void enable() 
	{

	}

	@Override
	public void config() 
	{

	}

	@Override
	public void disable()
	{
		if (domItemChest != null)
			domItemChest.remove();

		if (domItemEmerald != null)
			domItemEmerald.remove();
		
		for (IconData cur : icons)
		{
			if (cur.GetEntity() != null)
				cur.GetEntity().remove();
		}
	}

	@Override
	public void commands() 
	{
		AddCommand("tut");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!Clients().Get(caller).Rank().Has(Rank.ADMIN, true))
			return;

		if (args.length == 0)				return;
		else if (args[0].equals("a"))		StartTutorial(caller, TutorialType.A);
		else if (args[0].equals("b"))		StartTutorial(caller, TutorialType.B);
		else if (args[0].equals("c"))		StartTutorial(caller, TutorialType.C);
		else if (args[0].equals("d"))		StartTutorial(caller, TutorialType.D);
		else if (args[0].equals("z"))		StartTutorial(caller, TutorialType.Z);

		else if (args[0].equals("stop"))	EndTutorial(caller, false);
		else if (args[0].equals("finish"))	EndTutorial(caller, true);
	}

	@EventHandler 
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : UtilServer.getPlayers())
		{
			Part part = _data.get(cur).GetPart();

			if (part == null)
				continue;

			//Food Update
			if (!part.IsCompleted())
			{
				part.FoodUpdate(cur);
			}

			//Next Action
			if (part.Progress() && !part.IsCompleted())
			{
				part.GetAction().DoAction(cur);
			}

			//Next Part
			if (part.IsCompleted())
			{
				//Deregister Last
				HandlerList.unregisterAll(part);

				//Register Next
				Part nextPart = part.GetNext();
				UtilServer.getServer().getPluginManager().registerEvents(nextPart, Plugin());
				_data.get(cur).SetPart(nextPart);
			}
		}
	}

	@EventHandler
	public void Join(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		//Strip
		UtilInv.Clear(player);

		//Insert Player
		GetData(player);

		//Start welcome if not done
		if (!GetData(player).HasCompleted(TutorialType.A))
			StartTutorial(player, TutorialType.A);
		else
			Teleport().TP(player, spawnC);
	}

	@EventHandler
	public void Quit(PlayerQuitEvent event)
	{
		EndTutorial(event.getPlayer(), false);

		//Remove Data
		_data.remove(event.getPlayer());
	}

	@EventHandler
	public void NPCInteract(PlayerInteractEntityEvent event)
	{		
		if (event.getRightClicked() == null)
			return;

		if (!(event.getRightClicked() instanceof CraftLivingEntity))
			return;
		
		CraftLivingEntity ent = (CraftLivingEntity)event.getRightClicked();
		String name = ent.getCustomName();
		
		if (name == null)
			return;

		if (name.contains("Class Basics"))			StartTutorial(event.getPlayer(), TutorialType.B);
		else if (name.contains("Locked Skills"))	StartTutorial(event.getPlayer(), TutorialType.C);
		else if (name.contains("Custom Build"))		StartTutorial(event.getPlayer(), TutorialType.D);
		
		else if (name.contains("Condition Icon"))	StartTutorial(event.getPlayer(), TutorialType.M);	
		
		else if (name.contains("Barry"))			StartTutorial(event.getPlayer(), TutorialType.Q);	
		
		else if (name.contains("Domination"))		StartTutorial(event.getPlayer(), TutorialType.Z);		
	}

	public TutorialData GetData(Player player)
	{
		if (!_data.containsKey(player))
			_data.put(player, new TutorialData(player.getName()));

		return _data.get(player);
	}

	public void StartTutorial(Player player, TutorialType type)
	{
		System.out.println("Starting: " + type.GetName() + " for " + player.getName());
		//Start New
		TutorialData data = GetData(player);

		//Limits
		if (player.getGameMode() != GameMode.CREATIVE)
		{
			if (type == TutorialType.B)
			{
				if (!data.HasCompleted(TutorialType.A))
				{
					UtilPlayer.message(player, F.main("Tutorial", "You must complete " + F.elem(TutorialType.A.GetName()) + " first."));		
					return;
				}
			}

			else if (type == TutorialType.C)
			{
				if (!data.HasCompleted(TutorialType.A))
				{
					UtilPlayer.message(player, F.main("Tutorial", "You must complete " + F.elem(TutorialType.A.GetName()) + " first."));		
					return;
				}
				if (!data.HasCompleted(TutorialType.B))
				{
					UtilPlayer.message(player, F.main("Tutorial", "You must complete " + F.elem(TutorialType.B.GetName()) + " first."));		
					return;
				}

			}

			else if (type == TutorialType.D)
			{
				if (!data.HasCompleted(TutorialType.A))
				{
					UtilPlayer.message(player, F.main("Tutorial", "You must complete " + F.elem(TutorialType.A.GetName()) + " first."));		
					return;
				}
				if (!data.HasCompleted(TutorialType.B))
				{
					UtilPlayer.message(player, F.main("Tutorial", "You must complete " + F.elem(TutorialType.B.GetName()) + " first."));		
					return;
				}
				if (!data.HasCompleted(TutorialType.C))
				{
					UtilPlayer.message(player, F.main("Tutorial", "You must complete " + F.elem(TutorialType.C.GetName()) + " first."));		
					return;
				}	
			}

			else if (type == TutorialType.Z)
			{
				if (!data.HasCompleted(TutorialType.A))
				{
					UtilPlayer.message(player, F.main("Tutorial", "You must complete " + F.elem(TutorialType.A.GetName()) + " first."));		
					return;
				}
			}
		}

		//End Old
		EndTutorial(player, false);

		Part nextPart = null;
		if (type == TutorialType.A)			nextPart = new A01_Welcome(this, data, player);
		else if (type == TutorialType.B)	nextPart = new B01_ClassSelection(this, data, player);
		else if (type == TutorialType.C)	nextPart = new C01_Brute(this, data, player);
		else if (type == TutorialType.D)	nextPart = new D01_CustomBuild(this, data, player);
		else if (type == TutorialType.M)	nextPart = new M01_Icons(this, data, player);
		else if (type == TutorialType.Q)	nextPart = new Q01_Secret(this, data, player);
		else if (type == TutorialType.Z)	nextPart = new Z01_Introduction(this, data, player);


		if (nextPart == null)
		{
			UtilPlayer.message(player, F.main("Tutorial", "Error: Invalid Tutorial"));
			return;
		}
			

		data.SetPart(nextPart);
		data.SetCurrent(type);

		UtilServer.getServer().getPluginManager().registerEvents(nextPart, Plugin());

		//Inform
		UtilPlayer.message(player, F.main("Tutorial", "Starting Tutorial: " + F.elem(type.GetName())));
		player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 2f);
	}

	public void EndTutorial(Player player, boolean award)
	{
		TutorialData data = GetData(player);

		if (data.GetCurrent() == null)
			return;

		if (data.GetPart() == null)
			return;

		//Unregister
		HandlerList.unregisterAll(data.GetPart());

		//Remove Points
		if (data.GetPart().GetPointRemove())
			GivePoints(data.GetPart().GetPlayerName(), -4000);

		//Award Points
		if (award && !data.HasCompleted(data.GetCurrent()))
			AwardPoints(player);

		//Strip
		UtilInv.Clear(player);

		//Set Completed
		if (award)
			data.SetCompleted(data.GetCurrent());

		//Remove Part
		data.SetPart(null);

		//Inform
		if (award)
			UtilPlayer.message(player, F.main("Tutorial", "Completed Tutorial: " + F.elem(data.GetCurrent().GetName())));
		else
			UtilPlayer.message(player, F.main("Tutorial", "Ended Tutorial: " + F.elem(data.GetCurrent().GetName())));

		player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 2f);
	}

	public void AwardPoints(Player player)
	{
		GiveCredits(player.getName(), 400);
		UtilPlayer.message(player, F.main("Tutorial", "You received " + F.elem("400 Credits") + "!"));
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
	}
	
	@EventHandler
	public void IconItems(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;
		
		for (IconData cur : icons)
		{
			if (cur.GetEntity() == null || !cur.GetEntity().isValid())
			{
				if (cur.GetEntity() != null)
					cur.GetEntity().remove();

				cur.SetEntity(cur.GetLocation().getWorld().dropItem(cur.GetLocation(), ItemStackFactory.Instance.CreateStack(cur.GetMaterial(), cur.GetData())));
				cur.GetEntity().setVelocity(new Vector(0,0,0));
			}
		}	
	}

	@EventHandler
	public void DomItems(UpdateEvent event)
	{
		if (event.getType() != UpdateType.SLOW)
			return;

		if (domItemEmerald == null || !domItemEmerald.isValid())
		{
			if (domItemEmerald != null)
				domItemEmerald.remove();

			domItemEmerald = domEmerald.getWorld().dropItem(domEmerald, new ItemStack(Material.EMERALD));
			domItemEmerald.setVelocity(new Vector(0,0,0));
		}

		if (domItemChest == null || !domItemChest.isValid())
		{
			if (domItemChest != null)
				domItemChest.remove();

			domItemChest = domResupply.getWorld().dropItem(domResupply, new ItemStack(Material.CHEST));
			domItemChest.setVelocity(new Vector(0,0,0));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void DeathItems(EntityDeathEvent event)
	{
		event.getDrops().clear();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Drop(PlayerDropItemEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Pickup(PlayerPickupItemEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Pickup(AsyncPlayerChatEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockBreak(BlockBreakEvent event)
	{
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockPlace(BlockPlaceEvent event)
	{
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void QuitMessage(PlayerQuitEvent event)
	{
		event.setQuitMessage(null);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void JoinMessage(PlayerJoinEvent event)
	{
		event.setJoinMessage(null);
	}

	/*
	@EventHandler(priority = EventPriority.LOWEST)
	public void ChunkLoad(ChunkLoadEvent event)
	{
		if (event.getChunk().getX() >= 4 || event.getChunk().getX() <= -5 || event.getChunk().getZ() >= 4 || event.getChunk().getZ() <= -5)
			event.getChunk().unload(false, false);
	}
	*/

	@EventHandler(priority = EventPriority.LOWEST)
	public void CancelClassBlock(PlayerInteractEvent event)
	{
		if (event.getClickedBlock() == null)
			return;
		
		if (!UtilBlock.usable(event.getClickedBlock()))
			return;
		
		if (GetData(event.getPlayer()).AllowClassShop())
			if (event.getClickedBlock().getType() == Material.ENDER_CHEST)
				return;
		
		if (GetData(event.getPlayer()).AllowClassSetup())
			if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE)
				return;
			
		event.setCancelled(true);
	}
	
	@EventHandler
	public void CancelSkillTrigger(SkillTriggerEvent event)
	{
		if (GetData(event.GetPlayer()).AllowAction())
			return;
		
		event.SetCancelled(true);
		
		//Inform
		UtilPlayer.message(event.GetPlayer(), F.main("Tutorial", "You cannot use " + F.skill(event.GetSkillName()) + " at the moment."));
		UtilPlayer.message(event.GetPlayer(), F.main("Tutorial", "Please wait until instructed to do so..."));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void VoidReturn(EntityDamageEvent event)
	{
		if (event.getCause() != DamageCause.VOID)
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player)event.getEntity();
		
		event.setCancelled(true);
		
		
		//Teleport
		Teleport().TP(player, spawnC);
		player.playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2f, 2f);
		
		//Reset Part
		if (GetData(player).GetPart() != null)
		{
			GetData(player).GetPart().Dialogue(player, "Careful now! Let's try that again...");
			GetData(player).GetPart().SetIndex(0);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void DamageRemove(EntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;

		event.setCancelled(true);	
	}


	@EventHandler
	public void Print(ClassSetupEvent event)
	{
		/*
		System.out.println("type: " + event.GetType());
		System.out.println("class: " + event.GetClassType());
		System.out.println("id: " + event.GetPosition());

		CustomBuildToken bt = event.GetBuild();
		if (bt == null)
			System.out.println("build: " + "null");
		else
		{
			System.out.println("build id: " + bt.CustomBuildNumber);
			System.out.println("build name: " + bt.Name);
			System.out.println("build item tok: " + bt.ItemTokensBalance);
			System.out.println("build skill tok: " + bt.SkillTokensBalance);			
		}
		 */
	}

	public void GivePoints(String name, int amount)
	{
		PlayerUpdateToken token = new PlayerUpdateToken();
		token.Name = name;
		final CoreClient client = Clients().Get(name);
		if (client == null)
		{
			System.out.println("Tutorial Point Set: NULL Client");
			return;
		}

		token.Credits = 0;
		token.Points = amount;
		token.FilterChat = client.Game().GetFilterChat();     

		GetRepository().PlayerUpdate(new Callback<PlayerUpdateToken> ()
				{
			public void run(PlayerUpdateToken token)
			{
				client.Donor().SetPoints(client.Donor().GetGreenGems() + token.Points);
			}
				}, token);
	}
	
	public void GiveCredits(String name, int amount)
	{
		PlayerUpdateToken token = new PlayerUpdateToken();
		token.Name = name;
		final CoreClient client = Clients().Get(name);
		if (client == null)
		{
			System.out.println("Tutorial Credit Set: NULL Client");
			return;
		}

		token.Credits = amount;
		token.Points = 0;
		token.FilterChat = client.Game().GetFilterChat();     

		GetRepository().PlayerUpdate(new Callback<PlayerUpdateToken> ()
				{
			public void run(PlayerUpdateToken token)
			{
				client.Donor().SetCredits(client.Donor().GetBlueGems() + token.Credits);
			}
				}, token);
	}
}
