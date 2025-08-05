package mineplex.core.disguise.disguises;

import java.util.Arrays;

import net.minecraft.server.v1_6_R2.MobEffect;
import net.minecraft.server.v1_6_R2.MobEffectList;
import net.minecraft.server.v1_6_R2.PotionBrewer;

public class DisguiseEnderman extends DisguiseMonster
{
	public DisguiseEnderman(org.bukkit.entity.Entity entity)
	{
		super(entity);
		
		DataWatcher.a(16, new Byte((byte)0));
		DataWatcher.a(17, new Byte((byte)0));
		DataWatcher.a(18, new Byte((byte)0));
		
        int i = PotionBrewer.a(Arrays.asList(new MobEffect(MobEffectList.FIRE_RESISTANCE.id, 777)));
        DataWatcher.watch(8, Byte.valueOf((byte)(PotionBrewer.b(Arrays.asList(new MobEffect(MobEffectList.FIRE_RESISTANCE.id, 777))) ? 1 : 0)));
        DataWatcher.watch(7, Integer.valueOf(i));
	}
	
	public void SetCarriedId(int i)
	{
		DataWatcher.watch(16, Byte.valueOf((byte)(i & 0xFF)));
	}
	
	public int GetCarriedId()
	{
		return DataWatcher.getByte(16);
	}
	
	public void SetCarriedData(int i)
	{
		DataWatcher.watch(17, Byte.valueOf((byte)(i & 0xFF)));
	}
	
	public int GetCarriedData()
	{
		return DataWatcher.getByte(17);
	}

	public boolean bX()
	{
		return DataWatcher.getByte(18) > 0;
	}
	
	public void a(boolean flag)
	{
		DataWatcher.watch(18, Byte.valueOf((byte)(flag ? 1 : 0)));
	}
	
	@Override
	protected int GetEntityTypeId()
	{
		return 58;
	}
	
    protected String getHurtSound()
    {
        return "mob.endermen.hit";
    }
}
