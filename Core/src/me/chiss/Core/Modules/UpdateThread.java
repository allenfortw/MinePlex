package me.chiss.Core.Modules;

import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import mineplex.core.common.util.UtilAlg;
import mineplex.core.fakeEntity.FakeEntity;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.Packet28EntityVelocity;

public class UpdateThread extends Thread
{
	private EntityPlayer _player;
	private List<FakeEntity> _entities;
	
	private int counter = 0;
	
	public UpdateThread(EntityPlayer mcPlayer, FakeEntity...entities)
	{
		_player = mcPlayer;
		_entities = Arrays.asList(entities);
	}	
	
	public void run()
	{
		int incrementer = 1;
		while (counter >= 0)
		{
			int i = 0;
			for (FakeEntity item : _entities)
			{
				double radialLead = i * ((2d * Math.PI)/3);
				i++;
	
				Vector desiredA = GetTarget(_player.locX, _player.locY, _player.locZ, counter, radialLead);
				//Vector desiredB = GetTarget(_player.locX, _player.locY, _player.locZ, counter, radialLead + 1);
	
				//double distA = UtilMath.offset(item.GetLocation().toVector(), desiredA);
				//double distB = UtilMath.offset(item.GetLocation().toVector(), desiredB);
				//double distAB = UtilMath.offset(desiredA, desiredB);
	
				//if (distA > distB)
				//	continue;
	
				//if (distA < distAB / 2)
				//	continue;
	
				Vector vel = UtilAlg.getTrajectory(item.GetLocation().toVector(), desiredA);
	
				vel = vel.normalize();
				
				// Player
				//vel = vel.multiply(2);
				//_player.playerConnection.sendPacket(new Packet31RelEntityMove(item.GetEntityId(), (byte)(incrementer + vel.getX()), (byte)vel.getY(), (byte)(incrementer + vel.getZ())));
				
				// Ghast
				vel = vel.multiply(.08);
				//vel = vel.add(FakeEntityManager.Instance.GetKartVelocity());
				_player.playerConnection.sendPacket(new Packet28EntityVelocity(item.GetEntityId(), vel.getX(), vel.getY(),  vel.getZ()));
				item.SetLocation(item.GetLocation().add(vel));
				
				if (counter % 20 == 0)
				{
					//_player.playerConnection.sendPacket(new Packet34EntityTeleport(item.GetEntityId(), (int)(desiredA.getX() / 1.6), (int)desiredA.getY(), (int)(desiredA.getZ() / 1.6), (byte)0, (byte)0));
				}
			}
			
			if (counter == 200)
				incrementer = -1;
				
			counter += incrementer;
			
			try
			{
				Thread.sleep(50);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static Vector GetTarget(double originX, double originY, double originZ, int tick, double radialLead)
	{
		//Orbit
		double speed = 10d;

		double oX = Math.sin(tick/speed + radialLead) * 1.5;
		double oY = 0.5;
		double oZ = Math.cos(tick/speed + radialLead) * 1.5;

		return new Vector(originX + oX, originY + oY, originZ + oZ);
	}
}
