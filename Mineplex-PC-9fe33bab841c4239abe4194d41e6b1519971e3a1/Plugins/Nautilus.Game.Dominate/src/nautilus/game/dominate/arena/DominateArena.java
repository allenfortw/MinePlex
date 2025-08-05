package nautilus.game.dominate.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.Vector;

import nautilus.game.core.arena.Region;
import nautilus.game.core.arena.TeamArena;

public class DominateArena extends TeamArena implements IDominateArena
{
    private List<Region> _controlPointAreas;
    private List<Vector> _pointPowerUpPoints;
    private List<Vector> _resupplyPowerUpPoints;
    
    public DominateArena(String name)
    {
        super(name);
        
        _controlPointAreas = new ArrayList<Region>();
        _pointPowerUpPoints = new ArrayList<Vector>();
        _resupplyPowerUpPoints = new ArrayList<Vector>();
    }

	@Override
	public void Deactivate()
	{
		super.Deactivate();
		
		_controlPointAreas.clear();
		_controlPointAreas = null;
		
		_pointPowerUpPoints.clear();
		_pointPowerUpPoints = null;
		
		_resupplyPowerUpPoints.clear();
		_resupplyPowerUpPoints = null;
	}
    
    @Override
    public void AddControlPointArea(Region region)
    {
        Regions.add(region);
        _controlPointAreas.add(region);
    }

    @Override
    public List<Region> GetControlPointAreas()
    {
        return _controlPointAreas;
    }

    @Override
    public void AddResupplyPowerUp(Vector parseVector)
    {
        _resupplyPowerUpPoints.add(parseVector);
    }

    @Override
    public List<Vector> GetResupplyPowerUpPoints()
    {
        return _resupplyPowerUpPoints;
    }
    
    @Override
    public void AddPointPowerUp(Vector parseVector)
    {
        _pointPowerUpPoints.add(parseVector);
    }
    
    @Override
    public List<Vector> GetPointPowerUpPoints()
    {
        return _pointPowerUpPoints;
    }
}
