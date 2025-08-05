package mineplex.core.task.repository;

import mineplex.core.server.remotecall.AsyncJsonWebCall;

public class TaskRepository
{
	private String _webAddress;
	
	public TaskRepository(String webServerAddress)
	{
		_webAddress = webServerAddress;
	}

	public void AddTask(String name, String newTask)
	{
		UpdateTaskToken token = new UpdateTaskToken();
		token.Name = name;
		token.NewTaskCompleted = newTask;
		
		new AsyncJsonWebCall(_webAddress + "PlayerAccount/AddTask").Execute(token);
	}
}
