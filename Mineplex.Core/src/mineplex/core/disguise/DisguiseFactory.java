package mineplex.core.disguise;

import org.bukkit.entity.Entity;

import mineplex.core.disguise.disguises.*;

public class DisguiseFactory
{
	protected DisguiseZombie DisguiseZombie(Entity entity)
	{
		return new DisguiseZombie(entity);
	}
}
