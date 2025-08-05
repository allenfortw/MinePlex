package nautilus.minecraft.core.utils;

import net.minecraft.server.v1_6_R2.MathHelper;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TntUtil 
{
    public static void BypassWaterInExplosionEvent(EntityExplodeEvent event)
    {
        double d0;
        double d1;
        double d2;
        
        event.blockList().clear();

        World world = event.getLocation().getWorld();
        
        for (int i = 0; i < 16; ++i) 
        {
            for (int j = 0; j < 16; ++j) 
            {
                for (int k = 0; k < 16; ++k) 
                {
                    if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) 
                    {
                        double d3 = (double) ((float) i / ((float) 16 - 1.0F) * 2.0F - 1.0F);
                        double d4 = (double) ((float) j / ((float) 16 - 1.0F) * 2.0F - 1.0F);
                        double d5 = (double) ((float) k / ((float) 16 - 1.0F) * 2.0F - 1.0F);
                        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

                        d3 /= d6;
                        d4 /= d6;
                        d5 /= d6;
                        float f1 = 4 * (0.7F + ((CraftWorld)world).getHandle().random.nextFloat() * 0.6F);
                        
                        d0 = event.getLocation().getX();
                        d1 = event.getLocation().getY();
                        d2 = event.getLocation().getZ();

                        for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F) 
                        {
                            int l = MathHelper.floor(d0);
                            int i1 = MathHelper.floor(d1);
                            int j1 = MathHelper.floor(d2);
                            int k1 = world.getBlockTypeIdAt(l, i1, j1);
                            
                            if (k1 > 0 && k1 != 8 && k1 != 9 && k1 != 10 && k1 != 11) 
                            {
                                f1 -= (net.minecraft.server.v1_6_R2.Block.byId[k1].a(((CraftEntity)event.getEntity()).getHandle()) + 0.3F) * f2;
                            }

                            if (f1 > 0.0F && i1 < 256 && i1 >= 0 && k1 != 8 && k1 != 9 && k1 != 10 && k1 != 11)
                            { 
                                org.bukkit.block.Block block = world.getBlockAt(l, i1, j1);
                                
                                if (block.getType() != org.bukkit.Material.AIR && !event.blockList().contains(block)) 
                                {
                                    event.blockList().add(block);
                                }
                            }

                            d0 += d3 * (double) f2;
                            d1 += d4 * (double) f2;
                            d2 += d5 * (double) f2;
                        }
                    }
                }
            }
        }
    }
}
