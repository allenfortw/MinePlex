package me.chiss.Core.Field.repository;

public class FieldRepository
{
	public List<FieldBlockToken> GetFieldBlocks(String server)
	{
		return new JsonWebCall(WebServerAddress + "Fields/GetFieldBlocks").Execute(new TypeToken<List<FieldBlockToken>>(){}.getType(), server);
	}

	@Override
	public void AddFieldBlock(FieldBlockToken token)
	{
		new AsyncJsonWebCall(WebServerAddress + "Fields/AddFieldBlock").Execute(token);
	}

	@Override
	public void DeleteFieldBlock(String server, String location)
	{
		FieldBlockToken token = new FieldBlockToken();
		token.Server = server;
		token.Location = location;
		
		new AsyncJsonWebCall(WebServerAddress + "Fields/DeleteFieldBlock").Execute(token);
	}

	//Field Ore
	@Override
	public List<FieldOreToken> GetFieldOres(String server)
	{
		return new JsonWebCall(WebServerAddress + "Fields/GetFieldOres").Execute(new TypeToken<List<FieldOreToken>>(){}.getType(), server);
	}

	@Override
	public void AddFieldOre(FieldOreToken token)
	{
		new AsyncJsonWebCall(WebServerAddress + "Fields/AddFieldOre").Execute(token);
	}

	@Override
	public void DeleteFieldOre(String server, String location)
	{
		FieldOreToken token = new FieldOreToken();
		token.Server = server;
		token.Location = location;
		
		new AsyncJsonWebCall(WebServerAddress + "Fields/DeleteFieldOre").Execute(token);
	}

	//Field Monster
	@Override
	public List<FieldMonsterToken> GetFieldMonsters(String server)
	{
		return new JsonWebCall(WebServerAddress + "Fields/GetFieldMonsters").Execute(new TypeToken<List<FieldMonsterToken>>(){}.getType(), server);
	}

	@Override
	public void AddFieldMonster(FieldMonsterToken token)
	{
		new AsyncJsonWebCall(WebServerAddress + "Fields/AddFieldMonster").Execute(token);
	}

	@Override
	public void DeleteFieldMonster(String server, String name)
	{
		FieldMonsterToken token = new FieldMonsterToken();
		token.Server = server;
		token.Name = name;
		
		new AsyncJsonWebCall(WebServerAddress + "Fields/DeleteFieldMonster").Execute(token);
	}

}
