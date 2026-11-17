package commands.admin;

import l2p.commons.util.Util;
import l2p.gameserver.Config;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.NpcHtmlMessage;
import l2p.gameserver.network.serverpackets.PlaySound;
import l2p.gameserver.scripts.ScriptFile;

public class AdminAdmin implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_admin",
		"admin_play_sounds",
		"admin_play_sound",
		"admin_silence",
		"admin_tradeoff",
		"admin_cfg",
		"admin_config",
		"admin_show_html"
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
		
		if(activeChar.getPlayerAccess().Menu)
		{
			if(command.equals("admin_admin"))
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/admin.htm"));
			else if(command.equals("admin_play_sounds"))
			{
				if(wordList.length == 1)
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/songs/songs.htm"));
				else
					try
					{
						activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/songs/songs" + wordList[1] + ".htm"));
					}
					catch(Exception e) {}
			}
			else if(command.equals("admin_play_sound"))
			{
					try
					{
						playAdminSound(activeChar, wordList[1]);
					}
					catch(StringIndexOutOfBoundsException e)
					{}
			}
			else if(command.equals("admin_silence"))
			{
					if(activeChar.getMessageRefusal()) // already in message refusal
					// mode
					{
						activeChar.unsetVar("gm_silence");
						activeChar.setMessageRefusal(false);
						activeChar.sendPacket(Msg.MESSAGE_ACCEPTANCE_MODE);
					}
					else
					{
						if(Config.SAVE_GM_EFFECTS)
							activeChar.setVar("gm_silence", "true");
						activeChar.setMessageRefusal(true);
						activeChar.sendPacket(Msg.MESSAGE_REFUSAL_MODE);
					}
			}
			else if(command.equals("admin_tradeoff"))
			{
					try
					{
						if(wordList[1].equalsIgnoreCase("on"))
						{
							activeChar.setTradeRefusal(true);
							activeChar.sendMessage("tradeoff enabled");
						}
						else if(wordList[1].equalsIgnoreCase("off"))
						{
							activeChar.setTradeRefusal(false);
							activeChar.sendMessage("tradeoff disabled");
						}
					}
					catch(Exception ex)
					{
						if(activeChar.getTradeRefusal())
							activeChar.sendMessage("tradeoff currently enabled");
						else
							activeChar.sendMessage("tradeoff currently disabled");
					}
			}
			else if(command.equals("admin_cfg") || command.equals("admin_config"))
			{
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //config parameter[=value]");
					return false;
				}
				activeChar.sendMessage(Config.HandleConfig(activeChar, Util.joinStrings(" ", wordList, 1)));
			}
			else if(command.equals("admin_show_html"))
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/" + args));
			return true;
		}

		if(activeChar.getPlayerAccess().CanTeleport)
		{
			if(command.equals("admin_show_html"))
			{
				if(args.startsWith("tele"))
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/" + args));
				else
					activeChar.sendMessage("Access denied");
			}
			return true;
		}

		return false;
	}

	public void playAdminSound(L2Player activeChar, String sound)
	{
		activeChar.broadcastPacket(new PlaySound(sound));
		activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/admin.htm"));
		activeChar.sendMessage("Playing " + sound + ".");
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}
	
	public void onReload(){}
	
	public void onShutdown(){}
}