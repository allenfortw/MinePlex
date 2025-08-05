package me.chiss.Core.Modules;

import java.util.HashMap;
import java.util.HashSet;

import me.chiss.Core.Module.AModule;
import mineplex.core.common.Rank;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.fakeEntity.FakeEntity;
import mineplex.core.fakeEntity.FakeEntityManager;
import mineplex.core.fakeEntity.FakePlayer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import net.minecraft.server.v1_6_R2.EntityInsentient;
import net.minecraft.server.v1_6_R2.EntityLiving;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.MathHelper;
import net.minecraft.server.v1_6_R2.Packet28EntityVelocity;
import net.minecraft.server.v1_6_R2.Packet31RelEntityMove;
import net.minecraft.server.v1_6_R2.Packet34EntityTeleport;
import net.minecraft.server.v1_6_R2.PathfinderGoalSelector;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftAgeable;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Tester extends AModule
{
	private HashMap<Player, HashSet<String>> _test = new HashMap<Player, HashSet<String>>();

	private HashMap<Player, Vector> _speed = new HashMap<Player, Vector>();
	private HashMap<Player, Double> _speedVert = new HashMap<Player, Double>();

	public Tester(JavaPlugin plugin) 
	{
		super("Tester", plugin);
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
		AddCommand("e1");
		AddCommand("spleef");
		AddCommand("coinset");
		AddCommand("sin");
		AddCommand("wi");
		AddCommand("arraylist");
		AddCommand("spinme");
		AddCommand("karts");
		AddCommand("blocks");
		AddCommand("testi");
		AddCommand("flag");
	}

	@Override
	public void command(Player caller, String cmd, String[] args) 
	{
		if (!Clients().Get(caller).Rank().Has(Rank.ADMIN, true))
			return;

		if (cmd.equals("coinset"))
		{
			Clients().Get(caller).Game().SetEconomyBalance(100000);
		}

		if (cmd.equals("sin"))
		{
			try
			{
				double a = Double.parseDouble(args[0]);
				caller.sendMessage("sin(" + a + ") = " + Math.sin(a));
			}
			catch (Exception e)
			{

			}
		}

		if (!_test.containsKey(caller))
			_test.put(caller, new HashSet<String>());

		if (cmd.equals("e1"))
		{
			if (!_test.get(caller).remove("e1"))
				_test.get(caller).add("e1");

			caller.sendMessage("Toggled E1");
		}

		if (cmd.equals("wi"))
		{
			caller.sendMessage("Block: " + caller.getLocation().getBlock().getTypeId() + ":" + caller.getLocation().getBlock().getData());
		}

		if (cmd.equals("arraylist"))
		{

		}
		
		if (cmd.equals("spinme"))
		{
			SpinHim(caller);
		}
		
		if (cmd.equals("karts"))
		{
			ShowPlayersInKarts();
		}
		
		if (cmd.equals("blocks"))
		{
			ShowBlocks(caller);
		}
		
		if (cmd.equals("testi"))
		{
			ShowBobInvis(caller);
		}
		
		if (cmd.equals("flag"))
		{
			TestFlag(caller);
		}
		
		if (cmd.equals("fakepig"))
		{
			ShowFakePig(caller);
		}
		
		if (cmd.equals("fakeperson"))
		{
			ShowRealPig(caller);
		}
	}

	@EventHandler
	public void Update(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;

		for (Player cur : _test.keySet())
			if (_test.get(cur).contains("e1"))
				cur.getWorld().playEffect(cur.getLocation().add(0, 2, 0), Effect.ENDER_SIGNAL, 0);
	}

	@EventHandler
	public void Spleef(PlayerInteractEvent event)
	{
		if (!event.getPlayer().getName().equals("Chiss"))
			return;

		if (event.getClickedBlock() == null)
			return;

		if (event.getPlayer().getItemInHand() == null)
			return;

		if (event.getPlayer().getItemInHand().getType() != Material.BOWL)
			return;

		event.getPlayer().sendMessage("Light Level: " + event.getClickedBlock().getLightFromSky());
	}

	public Vector GetSpeed(Player player)
	{
		if (!_speed.containsKey(player))
			_speed.put(player, new Vector(0,0,0));

		return _speed.get(player);
	}

	public double GetSpeedVert(Player player)
	{
		if (!_speedVert.containsKey(player))
			_speedVert.put(player, 0d);

		return _speedVert.get(player);
	}
	
	public void ShowBobInvis(Player player)
	{
		final EntityPlayer mcPlayer = ((CraftPlayer)player).getHandle();
		final FakePlayer fakePlayer = new FakePlayer("BOB", player.getEyeLocation().add(1, 0, -3));
		
		mcPlayer.playerConnection.sendPacket(fakePlayer.Spawn());
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugins()[0], new Runnable()
		{
			public void run()
			{
				mcPlayer.playerConnection.sendPacket(fakePlayer.Hide());
				System.out.println("Sent meta packet");
			}
		}, 20L);
	}
	
	public void ShowBlocks(Player player)
	{
		EntityPlayer mcPlayer = ((CraftPlayer)player).getHandle();
		/*
		final FakePlayer fakePlayer = new FakePlayer("BOB", player.getEyeLocation().add(1, 0, 0));
		final FakePlayer fakePlayer2 = new FakePlayer("BOB2", player.getEyeLocation().add(0, 0, 1));
		final FakePlayer fakePlayer3 = new FakePlayer("BOB3", player.getEyeLocation().add(-1, 0, 0));
		
		final FakeEntity fakePlayer = new FakeEntity(EntityType.GHAST, player.getLocation().add(1, -3, 0));
		final FakeEntity fakePlayer2 = new FakeEntity(EntityType.GHAST, player.getLocation().add(0, -3, 1));
		final FakeEntity fakePlayer3 = new FakeEntity(EntityType.GHAST, player.getLocation().add(-1, -3, 0));
		*/
		// Falling blocks
		/*
		FakeEntity entity = new FakeFallingBlock(Material.LAVA.getId(), (byte)0, player.getLocation().add(1, 0, 0));
		FakeEntity entity2 = new FakeFallingBlock(Material.LAVA.getId(), (byte)0, player.getLocation().add(1, 0, 0));
		FakeEntity entity3 = new FakeFallingBlock(Material.LAVA.getId(), (byte)0, player.getLocation().add(1, 0, 0));
		*/
		
		final FakeEntity entity = new FakeEntity(EntityType.SLIME, player.getLocation().add(1.5, 0, 0));
		final FakeEntity entity2 = new FakeEntity(EntityType.SLIME, player.getLocation().add(0, 0, 1.5));
		final FakeEntity entity3 = new FakeEntity(EntityType.SLIME, player.getLocation().add(-1.5, 0, 0));
		
		//FakeEntityManager.Instance.AddFakeEntity(fakePlayer, player.getName());
		FakeEntityManager.Instance.AddFakeEntity(entity, player.getName());
		//FakeEntityManager.Instance.AddFakeEntity(fakePlayer2, player.getName());
		FakeEntityManager.Instance.AddFakeEntity(entity2, player.getName());
		//FakeEntityManager.Instance.AddFakeEntity(fakePlayer3, player.getName());
		FakeEntityManager.Instance.AddFakeEntity(entity3, player.getName());
		
		//mcPlayer.playerConnection.sendPacket(fakePlayer.Spawn());
		mcPlayer.playerConnection.sendPacket(entity.Spawn());
		//mcPlayer.playerConnection.sendPacket(fakePlayer.SetPassenger(entity.GetEntityId()));
		//mcPlayer.playerConnection.sendPacket(fakePlayer.Hide());
		
		//mcPlayer.playerConnection.sendPacket(fakePlayer2.Spawn());
		mcPlayer.playerConnection.sendPacket(entity2.Spawn());
		//mcPlayer.playerConnection.sendPacket(fakePlayer2.SetPassenger(entity2.GetEntityId()));
		//mcPlayer.playerConnection.sendPacket(fakePlayer2.Hide());
		
		//mcPlayer.playerConnection.sendPacket(fakePlayer3.Spawn());
		mcPlayer.playerConnection.sendPacket(entity3.Spawn());
		//mcPlayer.playerConnection.sendPacket(fakePlayer3.SetPassenger(entity3.GetEntityId()));
		//mcPlayer.playerConnection.sendPacket(fakePlayer3.Hide());

		/*
		for (int i=0; i < 21; i++)
		{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugins()[0], new Runnable()
			{
				public void run()
				{
					mcPlayer.playerConnection.sendPacket(new Packet28EntityVelocity(fakePlayer.GetEntityId(), .1, 0, 0));
					mcPlayer.playerConnection.sendPacket(new Packet31RelEntityMove(fakePlayer.GetEntityId(), (byte)(1), (byte)0, (byte)0));
				}
			}, i);
		}
		*/
		
		new UpdateThread(mcPlayer, entity, entity2, entity3).start();
	}
	
	public void ShowFakePig(Player player)
	{
		FakeEntity entity = new FakeEntity(EntityType.PIG, player.getLocation());
		
		final EntityPlayer mcPlayer = ((CraftPlayer)player).getHandle();
		
		mcPlayer.playerConnection.sendPacket(entity.Spawn());
		mcPlayer.playerConnection.sendPacket(new Packet28EntityVelocity(entity.GetEntityId(), 100, 0, 0));
	}
	
	public void ShowRealPig(Player player)
	{
		FakePlayer fakePlayer = new FakePlayer(player.getName() + "1", player.getLocation());
		
		final EntityPlayer mcPlayer = ((CraftPlayer)player).getHandle();
		
		mcPlayer.playerConnection.sendPacket(fakePlayer.Spawn());
		mcPlayer.playerConnection.sendPacket(new Packet28EntityVelocity(fakePlayer.GetEntityId(), 10000, 0, 0));
	}

	public void ShowPlayersInKarts()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			FakeEntity entity = new FakeEntity(EntityType.PIG, player.getLocation());

			for (Player otherPlayer : Bukkit.getOnlinePlayers())
			{
				if (player == otherPlayer)
					continue;
				
				final EntityPlayer mcPlayer = ((CraftPlayer)otherPlayer).getHandle();
				
				mcPlayer.playerConnection.sendPacket(entity.Spawn());
				mcPlayer.playerConnection.sendPacket(entity.SetPassenger(player.getEntityId()));
				FakeEntityManager.Instance.ForwardMovement(otherPlayer, player, entity.GetEntityId());
			}
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugins()[0], new Runnable()
		{
			public void run()
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					Location location = player.getLocation();
					Packet34EntityTeleport teleportPacket = new Packet34EntityTeleport(player.getEntityId(), MathHelper.floor(location.getX() * 32.0D), MathHelper.floor(location.getY() * 32.0D), MathHelper.floor(location.getZ() * 32.0D), (byte) ((int) (MathHelper.d(location.getYaw() * 256.0F / 360.0F))), (byte) ((int) (MathHelper.d(location.getPitch() * 256.0F / 360.0F))));
					
					for (Player otherPlayer : Bukkit.getOnlinePlayers())
					{
						final EntityPlayer mcPlayer = ((CraftPlayer)otherPlayer).getHandle();
						
						if (player == otherPlayer)
							continue;
						
						mcPlayer.playerConnection.sendPacket(teleportPacket);
					}
				}
			}
		}, 0L, 40L);
		
		/*
		for (Player player : Bukkit.getOnlinePlayers())
		{
			FakeEntity entity = new FakeEntity(EntityType.PIG, player.getLocation());
			FakePlayer fakePlayer = new FakePlayer(player.getName(), player.getLocation().subtract(new Vector(0, 1, 0)));
			//Entity kart = player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
			
			for (Player otherPlayer : Bukkit.getOnlinePlayers())
			{
				final EntityPlayer mcPlayer = ((CraftPlayer)otherPlayer).getHandle();
				
				if (player == otherPlayer)
				{
					// mcPlayer.playerConnection.sendPacket(fakePlayer.Hide(kart.getEntityId()));
					continue;
				}
				
				mcPlayer.playerConnection.sendPacket(fakePlayer.Spawn());
				mcPlayer.playerConnection.sendPacket(entity.Spawn());
				mcPlayer.playerConnection.sendPacket(fakePlayer.SetPassenger(entity.GetEntityId()));
				mcPlayer.playerConnection.sendPacket(entity.SetPassenger(player.getEntityId()));
				mcPlayer.playerConnection.sendPacket(fakePlayer.Hide());
				
				FakeEntityManager.Instance.ForwardMovement(otherPlayer, player, fakePlayer.GetEntityId());
				//FakeEntityManager.Instance.BlockMovement(otherPlayer, kart.getEntityId());
			}
		}
		*/
	}
	
	public void TestFlag(final Player player)
	{
		final EntityPlayer mcPlayer = ((CraftPlayer)player).getHandle();
		
		for (final Player onlinePlayer : Bukkit.getOnlinePlayers())
		{
			if (onlinePlayer == player)
				continue;
			
			
			
			final Item anchor = player.getWorld().dropItem(onlinePlayer.getEyeLocation(), new ItemStack(Material.WOOL.getId(), 1, (byte)11));
			Item flag12 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(0, 1, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)12));
			Item flag13 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(0, 2, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)13));
			Item flag14 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(0, 2, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)14));
			Item flag15 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(0, 4, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)15));
			
			anchor.setPassenger(flag12);
			flag12.setPassenger(flag13);
			flag13.setPassenger(flag14);
			flag14.setPassenger(flag15);
			
			anchor.setPickupDelay(600);
			flag12.setPickupDelay(600);
			flag13.setPickupDelay(600);
			flag14.setPickupDelay(600);
			flag15.setPickupDelay(600);
			
			final Item anchor2 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(1, 1, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)0));
			Item flag22 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(1, 2, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)1));
			Item flag23 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(1, 3, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)2));
			Item flag24 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(1, 4, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)3));
			
			anchor2.setPassenger(flag22);
			flag22.setPassenger(flag23);
			flag23.setPassenger(flag24);
			
			anchor2.setPickupDelay(600);
			flag22.setPickupDelay(600);
			flag23.setPickupDelay(600);
			flag24.setPickupDelay(600);
			
			final Item anchor3 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(2, 1, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)4));
			Item flag32 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(2, 2, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)5));
			Item flag33 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(2, 3, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)6));
			Item flag34 = player.getWorld().dropItem(onlinePlayer.getEyeLocation().add(2, 4, 0), new ItemStack(Material.WOOL.getId(), 1, (byte)7));
			
			anchor3.setPassenger(flag32);
			flag32.setPassenger(flag33);
			flag33.setPassenger(flag34);
			
			anchor3.setPickupDelay(600);
			flag32.setPickupDelay(600);
			flag33.setPickupDelay(600);
			flag34.setPickupDelay(600);

			onlinePlayer.setPassenger(anchor);
			
			final FakePlayer fakePlayer = new FakePlayer("test", onlinePlayer.getLocation().add(1, 0, 0));
			final FakePlayer fakePlayer2 = new FakePlayer("test2", onlinePlayer.getLocation().add(2, 0, 0));

			mcPlayer.playerConnection.sendPacket(fakePlayer.Spawn());
			mcPlayer.playerConnection.sendPacket(fakePlayer.Hide());
			
			mcPlayer.playerConnection.sendPacket(fakePlayer2.Spawn());
			mcPlayer.playerConnection.sendPacket(fakePlayer2.Hide());
			
			mcPlayer.playerConnection.sendPacket(fakePlayer.SetPassenger(anchor2.getEntityId()));
			mcPlayer.playerConnection.sendPacket(fakePlayer2.SetPassenger(anchor3.getEntityId()));
			
			for (int i=0; i < 101; i++)
			{
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugins()[0], new Runnable()
				{
					public void run()
					{
						Vector player1 = UtilAlg.getTrajectory(fakePlayer.GetLocation().toVector(), onlinePlayer.getLocation().toVector().subtract(new Vector(1, 0, 0)));
						Vector player2 = UtilAlg.getTrajectory(fakePlayer2.GetLocation().toVector(), onlinePlayer.getLocation().toVector().subtract(new Vector(2, 0, 0)));
						
						player1.multiply(20);
						player2.multiply(20);
						
						mcPlayer.playerConnection.sendPacket(new Packet31RelEntityMove(fakePlayer.GetEntityId(), (byte)player1.getX(), (byte)player1.getY(), (byte)player1.getZ()));
						mcPlayer.playerConnection.sendPacket(new Packet31RelEntityMove(fakePlayer2.GetEntityId(), (byte)player2.getX(), (byte)player2.getY(), (byte)player2.getZ()));
						
						fakePlayer.SetLocation(onlinePlayer.getLocation().subtract(new Vector(1, 0, 0)));
						fakePlayer2.SetLocation(onlinePlayer.getLocation().subtract(new Vector(2, 0, 0)));
					}
				}, i + 1);
			}
		}
	}
	
	public void SpinHim(final Player player)
	{		
		Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.OCELOT);
		((CraftAgeable)entity).setBaby();
		((CraftAgeable)entity).setAgeLock(true);
		
    	try
		{
			java.lang.reflect.Field _goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
			_goalSelector.setAccessible(true);
			java.lang.reflect.Field _targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");
			_targetSelector.setAccessible(true);

	    	_goalSelector.set(((CraftLivingEntity)entity).getHandle(), new PathfinderGoalSelector(((CraftWorld)entity.getWorld()).getHandle().methodProfiler));
	    	_targetSelector.set(((CraftLivingEntity)entity).getHandle(), new PathfinderGoalSelector(((CraftWorld)entity.getWorld()).getHandle().methodProfiler));
		} 
    	catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} 
    	catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} 
    	catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		} 
    	catch (SecurityException e)
		{
			e.printStackTrace();
		}
		
		entity.setPassenger(player);
		
		final Entity newEntity = entity;
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugins()[0], new Runnable()
		{
			public void run()
			{
				((CraftEntity)newEntity).getHandle().yaw = ((CraftEntity)newEntity).getHandle().yaw + 179;
			}
		}, 20L);

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugins()[0], new Runnable()
		{
			public void run()
			{
				player.leaveVehicle();
				newEntity.remove();
			}
		}, 110L);
	}
}
