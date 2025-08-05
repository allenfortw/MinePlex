package nautilus.game.tutorial;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class IconData 
{	
	private Location _loc;
	private Material _mat;
	private byte _data;
	private Entity _ent = null;
	
	public IconData(Location loc, Material mat)
	{
		_loc = loc;
		_mat = mat;
		_data = 0;
	}
	
	public IconData(Location loc, Material mat, byte data)
	{
		_loc = loc;
		_mat = mat;
		_data = data;
	}
	
	public Entity GetEntity()
	{
		return _ent;
	}
	
	public void SetEntity(Entity ent)
	{
		_ent = ent;
	}
	
	public Location GetLocation()
	{
		return _loc;
	}
	
	public Material GetMaterial()
	{
		return _mat;
	}
	
	public byte GetData()
	{
		return _data;
	}
}
