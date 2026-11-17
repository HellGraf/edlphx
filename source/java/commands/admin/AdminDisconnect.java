package commands.admin;

import l2p.commons.threading.ThreadPoolManager;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.extensions.multilang.CustomMessage;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;
import l2p.gameserver.scripts.ScriptFile;

public class AdminDisconnect implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_kick"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().CanKick)
			return false;

		if(command.equals("admin_kick"))
		{
			final L2Player player;
			if(args.length() == 0)
			{
				// Обработка по таргету
				L2Object target = activeChar.getTarget();
				if(target == null)
				{
					activeChar.sendMessage("Select character or specify player name.");
					return false;
				}
				if(!target.isPlayer())
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				player = (L2Player) target;
			}
			else
			{
				// Обработка по нику
				player = L2World.getPlayer(args);
				if(player == null)
				{
					activeChar.sendMessage("Character " + args + " not found in game.");
					return false;
				}
			}

			activeChar.sendMessage("Character " + player.getName() + " disconnected from server.");

			if(player.isInOfflineMode())
			{
				player.setOfflineMode(false);
				player.logout(false, false, true, true);
				if(player.getNetConnection() != null)
					player.getNetConnection().disconnectOffline();
				return true;
			}

			player.sendMessage(new CustomMessage("scripts.commands.admin.AdminDisconnect.YoureKickedByGM", player));
			player.sendPacket(Msg.YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_PLEASE_LOGIN_AGAIN);
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable(){
				public void run()
				{
					player.logout(false, false, true, true);
				}
			}, 500);
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
