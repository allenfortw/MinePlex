package mineplex.core.disguise.disguises;

public class DisguiseChicken extends DisguiseAnimal
{
	public DisguiseChicken(org.bukkit.entity.Entity entity)
	{
		super(entity);
	}

	@Override
	protected int GetEntityTypeId()
	{
		return 93;
	}
	
	public String getHurtSound()
	{
		return "mob.chicken.hurt";
	}
}
