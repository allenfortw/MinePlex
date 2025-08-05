package mineplex.core.task;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import mineplex.core.MiniClientPlugin;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.task.repository.TaskRepository;
import mineplex.core.task.repository.TaskToken;

public class TaskManager extends MiniClientPlugin<TaskClient>
{
	private TaskRepository _repository;
	
	public TaskManager(JavaPlugin plugin, String webServerAddress)
	{
		super("Task Manager", plugin);
		
		_repository = new TaskRepository(webServerAddress);
	}

	@Override
	protected TaskClient AddPlayer(String playerName)
	{
		return new TaskClient(playerName);
	}
	
	@EventHandler
	public void OnClientWebResponse(ClientWebResponseEvent event)
	{
		TaskToken token = new Gson().fromJson(event.GetResponse(), TaskToken.class);
		TaskClient client = new TaskClient(token.Name);
		
		if (token.TasksCompleted != null)
			client.TasksCompleted = token.TasksCompleted;
		
		Set(token.Name, client);
	}
	
	public boolean hasCompletedTask(Player player, String taskName)
	{
		return Get(player.getName()).TasksCompleted.contains(taskName);
	}
	
	public void completedTask(Player player, String taskName)
	{
		TaskClient client = Get(player.getName());
		client.TasksCompleted.add(taskName);
		
		_repository.AddTask(client.Name, taskName);
	}
}
