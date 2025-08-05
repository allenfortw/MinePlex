package nautilus.game.arcade.game.minigames.escape;

import mineplex.core.common.util.UtilAlg;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.util.Vector;

public class DragonEscapeData 
{
	public DragonEscape Host;

	public EnderDragon Dragon;  
  
	public Location Target = null;
	public Location Location = null;

	public float Pitch = 0;
	public Vector Velocity = new Vector(0,0,0);
		
	public DragonEscapeData(DragonEscape host, EnderDragon dragon, Location target) 
	{
		Host = host; 

		Dragon = dragon; 
		
		Location temp = dragon.getLocation();
		temp.setPitch(UtilAlg.GetPitch(UtilAlg.getTrajectory(dragon.getLocation(), target)));
		dragon.teleport(temp);

		Velocity = dragon.getLocation().getDirection().setY(0).normalize();
		Pitch = UtilAlg.GetPitch(dragon.getLocation().getDirection());

		Location = dragon.getLocation();
	}
	
	public void Move()
	{
		Turn();

		double timeSpeed = 0.16 + (System.currentTimeMillis() - Host.GetStateTime())/3000000d;
		System.out.println(timeSpeed);
		
		Location.add(Velocity.clone().multiply(timeSpeed));
		Location.add(0, -Pitch, 0);

		Location.setPitch(-1 * Pitch);
		Location.setYaw(180 + UtilAlg.GetYaw(Velocity));

		Dragon.teleport(Location);
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
		desired.multiply(0.2);

		Velocity.add(desired);

		//Speed
		UtilAlg.Normalize(Velocity);			
	}
}
