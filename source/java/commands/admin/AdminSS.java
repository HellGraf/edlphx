package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.entity.SevenSigns;
import l2p.gameserver.scripts.ScriptFile;

public class AdminSS implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_ssq_change",
		"admin_ssq_time",
		"admin_ssq_cabal"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	@Override
	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		String[] wordList = args.split("\\s+");

		if(!activeChar.getPlayerAccess().Menu)
			return false;

		if(command.equals("admin_ssq_change"))
		{
			if(wordList.length > 1)
			{
				int period = Integer.parseInt(wordList[0]);
				int minutes = Integer.parseInt(wordList[1]);
				SevenSigns.getInstance().changePeriod(period, minutes * 60);
			}
			else if(wordList.length == 1)
			{
				int period = Integer.parseInt(wordList[0]);
				SevenSigns.getInstance().changePeriod(period);
			}
			else
				SevenSigns.getInstance().changePeriod();
		}
		else if(command.equals("admin_ssq_time"))
		{
			try
			{
				int time = Integer.parseInt(args);
				SevenSigns.getInstance().setTimeToNextPeriodChange(time);
			}
			catch(Exception e)
			{
				
			}
		}
		else if(command.equals("admin_ssq_cabal"))
		{
			if(wordList.length > 2)
			{
				int player = Integer.parseInt(wordList[0]); // player objectid
				int cabal = Integer.parseInt(wordList[1]); // null dusk dawn
				int seal = Integer.parseInt(wordList[2]); // null avarice gnosis strife
				SevenSigns.getInstance().setPlayerInfo(player, cabal, seal);
			}
		}
		return true;
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
