package mineplex.core.disguise.disguises;

public class DisguisePigZombie extends DisguiseZombie
{
	public DisguisePigZombie(org.bukkit.entity.Entity entity)
	{
		super(entity);
	}
	
	@Override
	public int GetEntityTypeId()
	{
		return 57;
	}
	
    protected String getHurtSound()
    {
        return "mob.zombiepig.zpighurt";
    }
}
