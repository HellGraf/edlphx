package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.NpcHtmlMessage;
import l2p.gameserver.scripts.ScriptFile;

public class AdminEvents implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_events"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	@Override
	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().IsEventGm)
			return false;

		if(command.equals("admin_events"))
		{
			if(args.length() == 0)
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/events/events.htm"));
			else
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/events/" + args.trim()));
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
