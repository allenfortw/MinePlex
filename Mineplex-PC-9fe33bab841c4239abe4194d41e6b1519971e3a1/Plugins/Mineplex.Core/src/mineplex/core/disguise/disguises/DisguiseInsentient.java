package mineplex.core.disguise.disguises;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet5EntityEquipment;

public abstract class DisguiseInsentient extends DisguiseLiving
{
	private boolean _showArmor; 
	
	public DisguiseInsentient(org.bukkit.entity.Entity entity)
	{
		super(entity);
		
		DataWatcher.a(11, Byte.valueOf((byte)0));
		DataWatcher.a(10, "");
	}
	
	public void SetName(String name)
	{
		DataWatcher.watch(10, name);
	}
	
	public boolean HasCustomName()
	{
		return DataWatcher.getString(10).length() > 0;
	}
	
	public void SetCustomNameVisible(boolean visible)
	{
		DataWatcher.watch(11, Byte.valueOf((byte)(visible ? 1 : 0)));
	}
	
	public boolean GetCustomNameVisible()
	{
		return DataWatcher.getByte(11) == 1;
	}
	
	public boolean armorVisible()
	{
		return _showArmor;
	}
	
	public void showArmor()
	{
		_showArmor = true;
	}
	
	public void hideArmor()
	{
		_showArmor = false;
	}

	public List<Packet> getArmorPackets()
	{
        List<Packet5EntityEquipment> p5 = new ArrayList<Packet5EntityEquipment>();
        net.minecraft.server.v1_6_R2.ItemStack[] armorContents = Entity.getEquipment();
        
        for (short i=0; i < armorContents.length; i++)
        {
        	net.minecraft.server.v1_6_R2.ItemStack armorSlot =  armorContents[i];
        	 
	        if (armorSlot != null) 
	        {
	        	p5.add(new Packet5EntityEquipment(Entity.id, i, armorSlot));
	        }
    	}
        
		return null;
	}
}
