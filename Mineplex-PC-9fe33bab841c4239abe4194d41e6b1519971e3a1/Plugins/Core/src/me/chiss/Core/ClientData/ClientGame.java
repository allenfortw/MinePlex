package me.chiss.Core.ClientData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import mineplex.core.account.CoreClient;

public class ClientGame extends ClientDataBase<Object>
{
	private String _lastTo = null;
	private String _lastAdminTo = null;
	private String _lastSentMessage = "";
	private String _lastReceivedMessage = "";

	private String _lastChat = "";
	private boolean _unfilteredChat;
	
	private boolean _cloaked = false;
	private long _cloakEnd = 0;
 
	private HashMap<String, Long> _recharge = new HashMap<String, Long>();

	private double _energy = 0;
	
	private int _economyBalance;
	
	private long _lastPPH = 0;

	public ClientGame(CoreClient client) 
	{
		super(client, "Game", null);
	}

	@Override
	public void Load() 
	{
		_economyBalance = 0;
		LoadEconomyBalance();
	}

    @Override 
    public void LoadToken(Object token)
    {
        
    }

	public String GetLastTo() {
		return _lastTo;
	}

	public void SetLastTo(String _lastTo) {
		this._lastTo = _lastTo;
	}

	public String GetLastAdminTo() {
		return _lastAdminTo;
	}

	public void SetLastAdminTo(String _lastAdminTo) {
		this._lastAdminTo = _lastAdminTo;
	}

	public String GetLastSentMessage() {
		return _lastSentMessage;
	}

	public void SetLastSentMessage(String _lastSentMessage) {
		this._lastSentMessage = _lastSentMessage;
	}

	public String GetLastReceivedMessage() {
		return _lastReceivedMessage;
	}

	public void SetLastReceivedMessage(String _lastReceivedMessage) {
		this._lastReceivedMessage = _lastReceivedMessage;
	}

	public boolean IsCloaked() {
		return _cloaked;
	}

	public void SetCloaked(boolean _cloaked) {
		this._cloaked = _cloaked;
	}

	public long GetCloakEnd() {
		return _cloakEnd;
	}

	public void SetCloakEnd(long _cloakEnd) {
		this._cloakEnd = _cloakEnd;
	}

	public HashMap<String, Long> GetRecharge() {
		return _recharge;
	}

	public void SetRecharge(HashMap<String, Long> _recharge) {
		this._recharge = _recharge;
	}

	public double GetEnergy() {
		return _energy;
	}

	public void SetEnergy(double _energy) {
		this._energy = _energy;
	}

	public String GetLastChat() {
		return _lastChat;
	}

	public void SetLastChat(String _lastChat) {
		this._lastChat = _lastChat;
	}
	
	public int GetEconomyBalance()
	{
		return _economyBalance;
	}

	public void SetEconomyBalance(int economyBalance) 
	{
		_economyBalance = economyBalance;
		SaveEconomyBalance();
	}
	
	public void ModifyEconomyBalance(int mod) 
	{
		_economyBalance += mod;
		SaveEconomyBalance();
	}
	
	public void LoadEconomyBalance()
	{
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		
		try
		{
			fstream = new FileInputStream("economy/" + Client.GetPlayerName() + ".dat");
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
				
			try
			{
				_economyBalance = Integer.parseInt(strLine);
			}
			catch (Exception e)
			{
				System.out.println("Invalid Balance: " + Client.GetPlayerName());
				_economyBalance = 0;
				SaveEconomyBalance();
			}

			in.close();
		}
		catch (Exception e)
		{
			_economyBalance = 0;
			SaveEconomyBalance();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (in != null)
			{
				try
				{
					in.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (fstream != null)
			{
				try
				{
					fstream.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void SaveEconomyBalance() 
	{
		FileWriter fstream = null;
		BufferedWriter out = null;
		
		try
		{
			File file = new File("economy/");
			file.mkdirs();
			
			fstream = new FileWriter("economy/" + Client.GetPlayerName() + ".dat");
			out = new BufferedWriter(fstream);
			
			out.write("" + _economyBalance);

			out.close();
		}
		catch (Exception e)
		{
			System.err.println("Balance Save Error: " + e.getMessage());
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (fstream != null)
			{
				try
				{
					fstream.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
		
	public void SetFilterChat(boolean filterChat)
	{
		_unfilteredChat = filterChat;
	}
	
	public boolean GetFilterChat()
	{
		return _unfilteredChat;
	}
	
	public long GetLastPPH() 
	{
		return _lastPPH;
	}

	public void SetLastPPH(long _lastPPH) 
	{
		this._lastPPH = _lastPPH;
	}
}
