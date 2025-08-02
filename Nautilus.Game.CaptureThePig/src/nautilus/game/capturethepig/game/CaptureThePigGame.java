package nautilus.game.capturethepig.game;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map.Entry;

import me.chiss.Core.Condition.Condition.ConditionType;
import me.chiss.Core.PlayerTagNamer.PacketHandler;
import me.chiss.Core.Plugin.IPlugin;
import mineplex.core.common.util.NautHashMap;
import nautilus.game.capturethepig.arena.ICaptureThePigArena;
import nautilus.game.capturethepig.event.PigCapturedEvent;
import nautilus.game.capturethepig.player.CaptureThePigPlayer;
import nautilus.game.capturethepig.player.ICaptureThePigPlayer;
import nautilus.game.capturethepig.scoreboard.CaptureThePigTabScoreboard;
import nautilus.game.core.engine.TeamType;
import nautilus.game.core.events.team.TeamGameFinishedEvent;
import nautilus.game.core.game.TeamGame;
import net.minecraft.server.v1_6_R2.EntityCreature;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.Packet201PlayerInfo;
import net.minecraft.server.v1_6_R2.RandomPositionGenerator;
import net.minecraft.server.v1_6_R2.Vec3D;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPig;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

public class CaptureThePigGame extends TeamGame<ICaptureThePigPlayer, ICaptureThePigTeam, ICaptureThePigArena> implements ICaptureThePigGame 
{
    private CaptureThePigTabScoreboard _scoreboard;
    
    private int _lastUpdate;
    
    private NautHashMap<Pig, Integer> _crazyPigs;
    private NautHashMap<Pig, Entry<Pig, Integer>> _capturedPigs;
	
	public CaptureThePigGame(IPlugin plugin, PacketHandler packetHandler) 
	{
		super(plugin);
		
        ScoreLimit = 5;
        _scoreboard = new CaptureThePigTabScoreboard(plugin, packetHandler, this);
        _crazyPigs = new NautHashMap<Pig, Integer>();
        _capturedPigs = new NautHashMap<Pig, Entry<Pig, Integer>>();
	}

	@Override
	protected ICaptureThePigTeam CreateTeam(TeamType teamType) 
	{
		return new CaptureThePigTeam(teamType);
	}

	@Override
	protected ICaptureThePigPlayer CreateGamePlayer(Player player, int playerLives) 
	{
		return new CaptureThePigPlayer(Plugin.GetPlugin(), player);
	}
	
	@Override
	public void Activate(ICaptureThePigArena arena)
	{
		super.Activate(arena);
		
		RedTeam.SetPigPen(arena.GetRedPigPen());
		BlueTeam.SetPigPen(arena.GetBluePigPen());
		
        
        RedTeam.SpawnPig((Pig)Plugin.GetCreature().SpawnEntity(RedTeam.GetPigPen().GetMidPoint().toLocation(arena.GetWorld()), EntityType.PIG));
        BlueTeam.SpawnPig((Pig)Plugin.GetCreature().SpawnEntity(BlueTeam.GetPigPen().GetMidPoint().toLocation(arena.GetWorld()), EntityType.PIG));
		
		_scoreboard.Update();
	}
	
	@Override
	public void Deactivate()
	{
		_scoreboard.Stop();

		for (ICaptureThePigPlayer gamePlayer : Players.values())
		{
			EntityPlayer entityPlayer = ((CraftPlayer)gamePlayer.GetPlayer()).getHandle();

			for (Player player : Bukkit.getServer().getOnlinePlayers())
			{
				entityPlayer.playerConnection.sendPacket(new Packet201PlayerInfo(player.getName(), true, 0));
			}

			if (!gamePlayer.isOnline())
			{
				for (ICaptureThePigPlayer otherPlayer : Players.values())
				{
					EntityPlayer otherEntityPlayer = ((CraftPlayer)otherPlayer.GetPlayer()).getHandle();
					otherEntityPlayer.playerConnection.sendPacket(new Packet201PlayerInfo(otherPlayer.getName(), false, 0));
				}
			}
		}

		for (ICaptureThePigPlayer gamePlayer : Spectators.values())
		{
			EntityPlayer entityPlayer = ((CraftPlayer)gamePlayer.GetPlayer()).getHandle();

			for (Player player : Bukkit.getServer().getOnlinePlayers())
			{
				entityPlayer.playerConnection.sendPacket(new Packet201PlayerInfo(player.getName(), true, 0));
			}
		}

		_scoreboard = null;
		
		super.Deactivate();
	}
	
	@Override
	public void Update()
	{
		super.Update();
		
		for (ICaptureThePigPlayer player : GetPlayers())
		{
			if (player.GetPlayer().getPassenger() instanceof Pig)
			{
				if (player.GetTeam().GetPigPen().Contains(player.getLocation().toVector()))
				{
					if (player.GetTeam().HasPig())
					{
						Pig pig = (Pig)player.GetPlayer().getPassenger();
						Plugin.GetPlugin().getServer().getPluginManager().callEvent(new PigCapturedEvent(this, player));
						_capturedPigs.put(pig, new AbstractMap.SimpleEntry<Pig, Integer>(player.GetTeam().GetPig(), 0));
						player.GetPlayer().eject();
						player.GetTeam().CapturePig(pig);
						
						Plugin.GetCondition().EndCondition(player.GetPlayer(), ConditionType.SLOW, "Pig");
						Plugin.GetCondition().Factory().Vulnerable("Pig", player.GetPlayer(), player.GetPlayer(), 2, 0, false, false);
					}
				}
			}
		}
				
		UpdateCapturedPigs();
		UpdateCrazyPigs();
		
		if (_lastUpdate % 10 == 0)
		{            
			_scoreboard.Update();
		}

		_lastUpdate++;
		
		if (RedTeam.GetScore() >= ScoreLimit)
		{
			RedTeam.SetScore(ScoreLimit);
			StopGame();
			Plugin.GetPlugin().getServer().getPluginManager().callEvent(new TeamGameFinishedEvent<ICaptureThePigGame, ICaptureThePigTeam, ICaptureThePigPlayer>(this, RedTeam));   
			_scoreboard.Update();
		}
		else if (BlueTeam.GetScore() >= ScoreLimit)
		{
			BlueTeam.SetScore(ScoreLimit);
			StopGame();
			Plugin.GetPlugin().getServer().getPluginManager().callEvent(new TeamGameFinishedEvent<ICaptureThePigGame, ICaptureThePigTeam, ICaptureThePigPlayer>(this, BlueTeam));
			_scoreboard.Update();
		}
	}
	
	public void AddCrazyPig(Pig pig)
	{
		_crazyPigs.put(pig, _lastUpdate);
	}
	
	private void UpdateCapturedPigs()
	{
		Iterator<Entry<Pig, Entry<Pig, Integer>>> capturedPigIterator = _capturedPigs.entrySet().iterator();
		
		while (capturedPigIterator.hasNext())
		{
			Entry<Pig, Entry<Pig, Integer>> entry = capturedPigIterator.next();
			Pig pig = entry.getKey();
			Pig mate = entry.getValue().getKey();
			int capturedTicks = entry.getValue().getValue();
			
			if (capturedTicks > 9)
			{
				capturedPigIterator.remove();
				((Pig)Plugin.GetCreature().SpawnEntity(pig.getLocation(), EntityType.PIG)).setBaby();

				if (RedTeam.HasPig())
					BlueTeam.SpawnPig((Pig)Plugin.GetCreature().SpawnEntity(pig.getLocation(), EntityType.PIG));
				else
					RedTeam.SpawnPig((Pig)Plugin.GetCreature().SpawnEntity(pig.getLocation(), EntityType.PIG));
				
				pig.remove();
				System.out.println("Had baby and spawned new pig.");
			}	
			else
			{
				double aX = ((CraftPig)pig).getHandle().locX;
				double aY = ((CraftPig)pig).getHandle().locY;
				double aZ = ((CraftPig)pig).getHandle().locZ;
				
				double bX = ((CraftPig)mate).getHandle().locX;
				double bY = ((CraftPig)mate).getHandle().locY;
				double bZ = ((CraftPig)mate).getHandle().locZ;
				
				EntityCreature ec = ((CraftCreature)pig).getHandle();
		        ec.getNavigation().a(bX + .5, bY, bZ + .5, .38f);
		        
				ec = ((CraftCreature)mate).getHandle();
		        ec.getNavigation().a(aX - .5, aY, aZ - .5, .38f);
		        
		        System.out.println("Spinning");
			}
		}
		
		for (Pig key : _capturedPigs.keySet())
		{
			_capturedPigs.get(key).setValue(_capturedPigs.get(key).getValue() + 1);
		}
	}

	private void UpdateCrazyPigs()
	{
		Iterator<Entry<Pig, Integer>> crazyPigIterator = _crazyPigs.entrySet().iterator();
		
		while (crazyPigIterator.hasNext())
		{
			Entry<Pig, Integer> entry = crazyPigIterator.next();
			Pig pig = entry.getKey();
			int crazyTicks = entry.getValue();
			EntityCreature pigCreature = ((CraftPig)pig).getHandle();
			
			if (crazyTicks > 4)
				crazyPigIterator.remove();
			else
			{
	            Vec3D vec3d = RandomPositionGenerator.a(pigCreature, 5, 4);

	            if (vec3d != null)
	            {
	            	pigCreature.getNavigation().a(vec3d.c, vec3d.d, vec3d.e, 0.5F);
	            }
			}
		}
		
		for (Pig key : _crazyPigs.keySet())
		{
			_crazyPigs.put(key, _crazyPigs.get(key) + 1);
		}
	}
}
