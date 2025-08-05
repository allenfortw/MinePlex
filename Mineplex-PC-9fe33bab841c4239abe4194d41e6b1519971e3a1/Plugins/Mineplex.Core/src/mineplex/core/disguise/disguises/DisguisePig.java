package mineplex.core.disguise.disguises;

public class DisguisePig extends DisguiseAnimal
{
	public DisguisePig(org.bukkit.entity.Entity entity)
	{
		super(entity);
	}

	@Override
	protected int GetEntityTypeId()
	{
		return 90;
	}
	
	public String getHurtSound()
	{
		return "mob.pig.hurt";
	}
}
