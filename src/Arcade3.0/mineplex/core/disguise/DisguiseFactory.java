package mineplex.core.disguise;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import mineplex.core.disguise.disguises.DisguiseBase;
import mineplex.core.disguise.disguises.DisguiseBat;
import mineplex.core.disguise.disguises.DisguiseBlaze;
import mineplex.core.disguise.disguises.DisguiseCat;
import mineplex.core.disguise.disguises.DisguiseChicken;
import mineplex.core.disguise.disguises.DisguiseCow;
import mineplex.core.disguise.disguises.DisguiseCreeper;
import mineplex.core.disguise.disguises.DisguiseEnderman;
import mineplex.core.disguise.disguises.DisguiseHorse;
import mineplex.core.disguise.disguises.DisguiseIronGolem;
import mineplex.core.disguise.disguises.DisguiseMagmaCube;
import mineplex.core.disguise.disguises.DisguisePig;
import mineplex.core.disguise.disguises.DisguisePigZombie;
import mineplex.core.disguise.disguises.DisguisePlayer;
import mineplex.core.disguise.disguises.DisguiseSheep;
import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.core.disguise.disguises.DisguiseSlime;
import mineplex.core.disguise.disguises.DisguiseSnowman;
import mineplex.core.disguise.disguises.DisguiseSpider;
import mineplex.core.disguise.disguises.DisguiseSquid;
import mineplex.core.disguise.disguises.DisguiseVillager;
import mineplex.core.disguise.disguises.DisguiseWitch;
import mineplex.core.disguise.disguises.DisguiseWolf;
import mineplex.core.disguise.disguises.DisguiseZombie;

public class DisguiseFactory
{
	public static DisguiseBase createDisguise(Entity disguised, EntityType disguiseType)
	{
		switch (disguiseType)
		{
			case BAT:
				return new DisguiseBat(disguised);
			case BLAZE:
				return new DisguiseBlaze(disguised);
			case OCELOT:
				return new DisguiseCat(disguised);
			case CHICKEN:
				return new DisguiseChicken(disguised);
			case COW:
				return new DisguiseCow(disguised);
			case CREEPER:
				return new DisguiseCreeper(disguised);
			case ENDERMAN:
				return new DisguiseEnderman(disguised);
			case HORSE:
				return new DisguiseHorse(disguised);
			case IRON_GOLEM:
				return new DisguiseIronGolem(disguised);
			case MAGMA_CUBE:
				return new DisguiseMagmaCube(disguised);
			case PIG:
				return new DisguisePig(disguised);
			case PIG_ZOMBIE:
				return new DisguisePigZombie(disguised);
			case PLAYER:
				return new DisguisePlayer(disguised);
			case SHEEP:
				return new DisguiseSheep(disguised);
			case SKELETON:
				return new DisguiseSkeleton(disguised);
			case SLIME:
				return new DisguiseSlime(disguised);
			case SNOWMAN:
				return new DisguiseSnowman(disguised);
			case SPIDER:
				return new DisguiseSpider(disguised);
			case SQUID:
				return new DisguiseSquid(disguised);
			case VILLAGER:
				return new DisguiseVillager(disguised);
			case WITCH:
				return new DisguiseWitch(disguised);
			case WOLF:
				return new DisguiseWolf(disguised);
			case ZOMBIE:
				return new DisguiseZombie(disguised);
			default:
				return null;
		}
	}
}
