package commands.admin;

import l2p.gameserver.Announcements;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.clientpackets.Say2C;
import l2p.gameserver.network.serverpackets.ExShowScreenMessage;
import l2p.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2p.gameserver.scripts.ScriptFile;

public class AdminAnnouncements implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_list_announcements",
		"admin_announce_announcements",
		"admin_add_announcement",
		"admin_del_announcement",
		"admin_announce",
		"admin_a",
		"admin_announce_menu",
		"admin_crit_announce",
		"admin_c",
		"admin_toscreen",
		"admin_s",
		"admin_reload_announcements"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		String[] wordList = args.split("\\s+");
		
		if(!activeChar.getPlayerAccess().CanAnnounce)
			return false;

		Announcements a = Announcements.getInstance();
		
		if(command.equals("admin_list_announcements"))
		{
			a.listAnnouncements(activeChar);
			}
		else if(command.equals("admin_announce_menu"))
		{
			a.handleAnnounce(args, 20);
			a.listAnnouncements(activeChar);
			}
		else if(command.equals("admin_announce_announcements"))
		{
			for(L2Player player : L2ObjectsStorage.getAllPlayersForIterate())
				a.showAnnouncements(player);
			a.listAnnouncements(activeChar);
			}
		else if(command.equals("admin_add_announcement"))
		{
			if(wordList.length < 2)
				return false;
			try
			{
				String val = args.substring(23);
				a.addAnnouncement(val);
				a.listAnnouncements(activeChar);
			}
			catch(StringIndexOutOfBoundsException e)
			{}
			}
		else if(command.equals("admin_del_announcement"))
		{
			if(wordList.length < 2)
				return false;
			try
			{
				int val = new Integer(args.substring(23));
				a.delAnnouncement(val);
				a.listAnnouncements(activeChar);
			}
			catch(StringIndexOutOfBoundsException e)
			{}
		}
		else if(command.equals("admin_a") || command.equals("admin_announce"))
		{
			a.handleAnnounce(args, 0);
		}
		else if(command.equals("admin_c") || command.equals("admin_crit_announce"))
		{
			if(wordList.length < 2)
				return false;
			Announcements.getInstance().announceToAll(activeChar.getName() + ": " + args.replaceFirst("admin_crit_announce ", "").replaceFirst("admin_c ", ""), Say2C.CRITICAL_ANNOUNCEMENT);
			}
		else if(command.equals("admin_toscreen"))
		{
		}
		else if(command.equals("admin_s"))
		{
			if(wordList.length < 2)
				return false;
			String text = activeChar.getName() + ": " + args.replaceFirst("admin_toscreen ", "").replaceFirst("admin_s ", "");
			int time = 3000 + text.length() * 100; // 3 секунды + 100мс на символ
			ExShowScreenMessage sm = new ExShowScreenMessage(text, time, ScreenMessageAlign.TOP_CENTER, text.length() < 64);
			for(L2Player player : L2ObjectsStorage.getAllPlayersForIterate())
				player.sendPacket(sm);
			}
		else if(command.equals("admin_reload_announcements"))
		{
			Announcements.getInstance().loadAnnouncements();
			Announcements.getInstance().listAnnouncements(activeChar);
			activeChar.sendMessage("Announcements reloaded.");
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