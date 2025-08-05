package mineplex.core.projectile;

import mineplex.core.updater.event.UpdateEvent;
import mineplex.core.updater.UpdateType;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ProjectileUser 
{
	public ProjectileManager Throw;
	
	private Entity _thrown;
	private LivingEntity _thrower;
	private IThrown _callback;
	
	private long _expireTime;
	private boolean _hitPlayer = false;
	private boolean _hitBlock = false;
	private boolean _idle = false;
	private boolean _pickup = false;
	
	private Sound _sound = null;
	private float _soundVolume = 1f;
	private float _soundPitch = 1f;
	private Effect _effect = null;
	private int _effectData = 0;
	private UpdateType _effectRate = UpdateType.TICK;
	
	private double _hitboxMult = 1;

	public ProjectileUser(ProjectileManager throwInput, Entity thrown, LivingEntity thrower, IThrown callback, 
			long expireTime, boolean hitPlayer, boolean hitBlock, boolean idle, boolean pickup,
			Sound sound, float soundVolume, float soundPitch, Effect effect, int effectData, UpdateType effectRate,
			double hitboxMult) 
	{
		Throw = throwInput;

		_thrown = thrown;
		_thrower = thrower;
		_callback = callback;
		
		_expireTime = expireTime;
		_hitPlayer = hitPlayer;
		_hitBlock = hitBlock;
		_idle = idle;
		_pickup = pickup;
		
		_sound = sound;
		_soundVolume = soundVolume;
		_soundPitch = soundPitch;
		_effect = effect;
		_effectData = effectData;
		_effectRate = effectRate;
		
		_hitboxMult = hitboxMult;
	}

	public void Effect(UpdateEvent event)
	{
		if (event.getType() != _effectRate)
			return;
		
		if (_sound != null)
			_thrown.getWorld().playSound(_thrown.getLocation(), _sound, _soundVolume, _soundPitch);
		
		if (_effect != null)
			_thrown.getWorld().playEffect(_thrown.getLocation(), _effect, _effectData);

	}

	public boolean Collision() 
	{
		if (_expireTime != -1 && System.currentTimeMillis() > _expireTime)
		{
			_callback.Expire(this);
			return true;
		}
			

		//Check Hit Player
		if (_hitPlayer)
		{
			for (Object entity : ((CraftWorld)_thrown.getWorld()).getHandle().entityList)
			{
	            if (entity instanceof net.minecraft.server.v1_6_R2.Entity) 
	            {
	                Entity bukkitEntity = ((net.minecraft.server.v1_6_R2.Entity) entity).getBukkitEntity();
	                
					if (bukkitEntity instanceof LivingEntity)
					{
						LivingEntity ent = (LivingEntity)bukkitEntity;
						
						//Avoid Self
						if (ent.equals(_thrower))
							continue;
						
						if (ent instanceof Player)
							if (((Player)ent).getGameMode() == GameMode.CREATIVE)
								continue;
		
						//Hit Player
						if (UtilEnt.hitBox(_thrown.getLocation(), ent, _hitboxMult))
						{
							_callback.Collide(ent, null, this);
							return true;
						}
					}
	            }
			}
		}
		
		try
		{
		//Check Hit Block
		if (_hitBlock)
		{
			Block block = _thrown.getLocation().add(_thrown.getVelocity().normalize().multiply(0.6)).getBlock();
			if (!UtilBlock.airFoliage(block))
			{
				_callback.Collide(null, block, this);
				return true;
			}
		}
		

		//Idle
		if (_idle)
		{
			if (_thrown.getVelocity().length() < 0.2 &&
					!UtilBlock.airFoliage(_thrown.getLocation().getBlock().getRelative(BlockFace.DOWN)))
			{
				_callback.Idle(this);
				return true;
			}
		}
		}
		catch (Exception ex)
		{
			if (_hitBlock)
			{
				return true;
			}
			
			if (_idle)
			{
				return true;
			}
		}
		
		return false;
	}

	public LivingEntity GetThrower() 
	{
		return _thrower;
	}

	public Entity GetThrown() 
	{
		return _thrown;
	}

	public boolean CanPickup(LivingEntity thrower)
	{
		if (!thrower.equals(_thrower))
			return false;
		
		return _pickup;
	}
}
