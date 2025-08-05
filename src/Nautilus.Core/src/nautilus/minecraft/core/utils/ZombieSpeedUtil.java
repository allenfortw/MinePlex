package nautilus.minecraft.core.utils;

import java.lang.reflect.Field;

import net.minecraft.server.v1_6_R2.*;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class ZombieSpeedUtil 
{
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (event.getEntity() instanceof CraftZombie)
        {
            EntityZombie zombie = ((CraftZombie)event.getEntity()).getHandle();
            
            try 
            {
                Field metadataField = EntityInsentient.class.getDeclaredField("goalSelector");
                metadataField.setAccessible(true);

                // Original speed is .23F
                float modifier = 1.5F;
                float modifiedSpeed = 0.23F * modifier;
                
                PathfinderGoalSelector goalSelector = new PathfinderGoalSelector(zombie.world != null && zombie.world.methodProfiler != null ? zombie.world.methodProfiler : null);
                goalSelector.a(0, new PathfinderGoalFloat(zombie));
                goalSelector.a(1, new PathfinderGoalBreakDoor(zombie));
                goalSelector.a(2, new PathfinderGoalMeleeAttack(zombie, EntityHuman.class, modifiedSpeed, false));
                goalSelector.a(3, new PathfinderGoalMeleeAttack(zombie, EntityVillager.class, modifiedSpeed, true));
                goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(zombie, 0.23F));
                goalSelector.a(5, new PathfinderGoalMoveThroughVillage(zombie, 0.23F, false));
                goalSelector.a(6, new PathfinderGoalRandomStroll(zombie, 0.23F));
                goalSelector.a(7, new PathfinderGoalLookAtPlayer(zombie, EntityHuman.class, 8.0F));
                goalSelector.a(7, new PathfinderGoalRandomLookaround(zombie));
                
                metadataField.set(zombie, goalSelector);
            } 
            catch (Exception e) 
            {
                System.out.println("Unable to modify goal selector");
                e.printStackTrace();
            }
        }
    }
}
