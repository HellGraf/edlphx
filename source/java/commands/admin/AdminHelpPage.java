package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.NpcHtmlMessage;
import l2p.gameserver.scripts.ScriptFile;

public class AdminHelpPage implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_showhtml"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().Menu)
			return false;

		if(command.equals("admin_showhtml"))
		{
			if(args.length() == 0)
			{
				activeChar.sendMessage("Usage: //showhtml <file>");
				return false;
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/" + args));
		}

		return true;
	}

	public static void showHelpHtml(L2Player targetChar, String content)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setHtml(content);
		targetChar.sendPacket(adminReply);
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
