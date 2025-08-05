package mineplex.hub;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class Dragon extends MiniPlugin
{
	HubManager Manager;

	public EnderDragon Dragon = null;  
	
	public Entity TargetEntity = null;
  
	public Location Target = null;
	public Location Location = null;

	public float Pitch = 0;
	public Vector Velocity = new Vector(0,0,0);
	
	public double RangeBest = 1000;
	public long RangeTime = 0;
	
	public int textA = 0;
	public int textB = 0;
	
	public Dragon(HubManager manager) 
	{
		super("Dragon Manager", manager.GetPlugin());

		Manager = manager;
	}
	
	public void Spawn()
	{
		if (Dragon != null)
			Dragon.remove();
		
		if (UtilServer.getPlayers().length == 0)
			return;
		
		if (!Manager.GetSpawn().getWorld().isChunkLoaded(Manager.GetSpawn().getChunk()))
			return;
		
		for (Entity ent : Manager.GetSpawn().getWorld().getEntities())
		{
			if (ent instanceof EnderDragon)
				ent.remove();
		}
		
		Dragon = Manager.GetSpawn().getWorld().spawn(Manager.GetSpawn().add(0, 50, 0), EnderDragon.class);
		UtilEnt.Vegetate(Dragon);
		
		Dragon.setCustomName(C.cGreen + C.Bold + "The Mineplex Dragon");
		
		Velocity = Dragon.getLocation().getDirection().setY(0).normalize();
		Pitch = UtilAlg.GetPitch(Dragon.getLocation().getDirection());

		Location = Dragon.getLocation();
		
		TargetSky();
	}
	
	@EventHandler
	public void MoveUpdate(UpdateEvent event)
	{
		if (event.getType() != UpdateType.TICK)
			return;
		
		if (Dragon == null || !Dragon.isValid())
		{
			//Spawn();
			return;
		}
		
		Turn();

		Location.add(Velocity);
		Location.add(0, -Pitch, 0);

		Location.setPitch(-1 * Pitch);
		Location.setYaw(180 + UtilAlg.GetYaw(Velocity));

		Dragon.teleport(Location);
	}
	
	@EventHandler
	public void ColorEvent(UpdateEvent event)
	{
		if (event.getType() != UpdateType.FASTEST)
			return;
		
		if (Dragon == null || !Dragon.isValid())
			return;
		
		ChatColor 				aCol = ChatColor.RED;
		if (textA == 1)			aCol = ChatColor.GOLD;
		else if (textA == 2)	aCol = ChatColor.YELLOW;
		else if (textA == 3)	aCol = ChatColor.GREEN;
		else if (textA == 4)	aCol = ChatColor.AQUA;
		else if (textA == 5)	aCol = ChatColor.LIGHT_PURPLE;
		
		textA = (textA+1)%6;
		
		ChatColor 				bCol = ChatColor.GREEN;
		if (textB > 6)			bCol = ChatColor.WHITE;

		textB = (textB+1)%14;
		
		Dragon.setCustomName(aCol + C.Bold + C.Line + Manager.DragonTextA +ChatColor.RESET + " - " + bCol + C.Bold + C.Line + Manager.DragonTextB);
	}

	private void Turn() 
	{
		//Pitch
		float desiredPitch = UtilAlg.GetPitch(UtilAlg.getTrajectory(Location, Target));
		if (desiredPitch < Pitch)	Pitch = (float)(Pitch - 0.05);
		if (desiredPitch > Pitch)	Pitch = (float)(Pitch + 0.05);
		if (Pitch > 0.5)	Pitch = 0.5f;
		if (Pitch < -0.5)	Pitch = -0.5f;

		//Flat
		Vector desired = UtilAlg.getTrajectory2d(Location, Target);
		desired.subtract(UtilAlg.Normalize(new Vector(Velocity.getX(), 0, Velocity.getZ())));
		desired.multiply(0.075);

		Velocity.add(desired);

		//Speed
		UtilAlg.Normalize(Velocity);			
	}

	public void Target() 
	{		
		if (TargetEntity != null)
		{
			if (!TargetEntity.isValid())
			{
				TargetEntity = null;
			}
			else
			{
				Target = TargetEntity.getLocation().subtract(0, 8, 0);
			}
	
			return;
		}
		
		if (Target == null)
		{
			TargetSky();
		}
		
		if (UtilMath.offset(Location, Target) < 10)
		{
			TargetSky();
		}
		
		TargetTimeout();
	}
	
	public void TargetTimeout()
	{
		if (UtilMath.offset(Location, Target)+1 < RangeBest)
		{
			RangeTime = System.currentTimeMillis();
			RangeBest = UtilMath.offset(Location, Target);
		}
		else
		{
			if (UtilTime.elapsed(RangeTime, 10000))
			{
				TargetSky();
			}
		}
	}
	
	public void TargetSky()
	{
		RangeBest = 9000;
		RangeTime = System.currentTimeMillis();
		
		Target = Manager.GetSpawn().add(40 - UtilMath.r(80), 50 + UtilMath.r(30), 40 - UtilMath.r(80));
		
		System.out.println("Dragon flying to: " + UtilWorld.locToStrClean(Target));
	}
}
