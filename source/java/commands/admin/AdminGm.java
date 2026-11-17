package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.scripts.ScriptFile;

public class AdminGm implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_gm"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	@Override
	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		if(command.equals("admin_gm"))
			handleGm(activeChar);

		return true;
	}

	private void handleGm(L2Player activeChar)
	{
		if(activeChar.isGM())
		{
			activeChar.getPlayerAccess().IsGM = false;
			activeChar.sendMessage("You no longer have GM status.");
		}
		else
		{
			activeChar.getPlayerAccess().IsGM = true;
			activeChar.sendMessage("You have GM status now.");
		}
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
