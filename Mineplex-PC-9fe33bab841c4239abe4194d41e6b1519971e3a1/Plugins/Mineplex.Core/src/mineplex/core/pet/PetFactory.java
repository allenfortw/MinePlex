package mineplex.core.pet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mineplex.core.common.util.NautHashMap;
import mineplex.core.pet.repository.PetRepository;
import mineplex.core.pet.repository.token.PetExtraToken;
import mineplex.core.pet.repository.token.PetSalesToken;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class PetFactory
{
	private PetRepository _repository;
	private NautHashMap<EntityType, Pet> _pets;
	private NautHashMap<Material, PetExtra> _petExtras;
	
	public PetFactory(PetRepository repository)
	{
		_repository = repository;
		_pets = new NautHashMap<EntityType, Pet>();
		_petExtras = new NautHashMap<Material, PetExtra>();
		
		CreatePets();
		CreatePetExtras();
	}
	
	private void CreatePets()
	{
		_pets.put(EntityType.PIG, new Pet("Pig", EntityType.PIG, 3000));
		_pets.put(EntityType.SHEEP, new Pet("Sheep", EntityType.SHEEP, 3000));
		_pets.put(EntityType.COW, new Pet("Cow", EntityType.COW, 3000));
		_pets.put(EntityType.CHICKEN, new Pet("Chicken", EntityType.CHICKEN, 4000));
		_pets.put(EntityType.WOLF, new Pet("Dog", EntityType.WOLF, 5000));
		_pets.put(EntityType.OCELOT, new Pet("Cat", EntityType.OCELOT, 5000));
		_pets.put(EntityType.MUSHROOM_COW, new Pet("Mooshroom", EntityType.MUSHROOM_COW, 3000));

		List<PetSalesToken> petTokens = new ArrayList<PetSalesToken>();
		
		for (Pet pet : _pets.values())
		{
			PetSalesToken petToken = new PetSalesToken();
			petToken.Name = pet.GetPetName();
			petToken.PetType = pet.GetPetType().toString();			
			
			petTokens.add(petToken);
		}
		
		for (PetSalesToken petToken : _repository.GetPets(petTokens))
		{
			if (_pets.containsKey(EntityType.valueOf(petToken.PetType)))
			{
				_pets.get(EntityType.valueOf(petToken.PetType)).Update(petToken);
			}
		}
	}

	private void CreatePetExtras()
	{
		_petExtras.put(Material.SIGN, new PetExtra("Name Tag", Material.NAME_TAG, 1000));

		List<PetExtraToken> petExtraTokens = new ArrayList<PetExtraToken>();
		
		for (PetExtra petExtra : _petExtras.values())
		{
			PetExtraToken petToken = new PetExtraToken();
			petToken.Name = petExtra.GetName();
			petToken.Material = petExtra.GetMaterial().toString();			
				
			petExtraTokens.add(petToken);
		}
		
		for (PetExtraToken token : _repository.GetPetExtras(petExtraTokens))
		{
			if (_petExtras.containsKey(Material.valueOf(token.Material)))
			{
				_petExtras.get(Material.valueOf(token.Material)).Update(token);
			}
		}
	}
	
	public Collection<Pet> GetPets()
	{
		return _pets.values();
	}
	
	public Collection<PetExtra> GetPetExtras()
	{
		return _petExtras.values();
	}
	
	public Collection<PetExtra> GetPetExtraBySalesId(int salesId)
	{
		return _petExtras.values();
	}
}
