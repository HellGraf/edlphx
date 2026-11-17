package commands.admin;

import java.util.Collection;

import l2p.commons.threading.ThreadPoolManager;
import l2p.commons.util.Log;
import l2p.gameserver.Announcements;
import l2p.gameserver.Config;
import l2p.gameserver.extensions.multilang.CustomMessage;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.loginservercon.LSConnection;
import l2p.gameserver.loginservercon.gspackets.ChangeAccessLevel;
import l2p.gameserver.model.L2ManufactureItem;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;
import l2p.gameserver.model.TradeItem;
import l2p.gameserver.model.L2ObjectTasks.TeleportTask;
import l2p.gameserver.network.clientpackets.Say2C;
import l2p.gameserver.network.serverpackets.NpcHtmlMessage;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.util.AutoBan;

public class AdminBan implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_ban",
		"admin_unban",
		"admin_chatban",
		"admin_ckarma",
		"admin_cban",
		"admin_chatunban",
		"admin_acc_ban",
		"admin_acc_unban",
		"admin_trade_ban",
		"admin_trade_unban",
		"admin_jail",
		"admin_unjail"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		String[] wordList = args.split("\\s+");

		if(activeChar.getPlayerAccess().CanTradeBanUnban)
		{
			if(command.equals("admin_trade_ban"))
				return tradeBan(wordList, activeChar);
			else if(command.equals("admin_trade_unban"))
				return tradeUnban(wordList, activeChar);
		}

		if(activeChar.getPlayerAccess().CanBan)
		{
			if(command.equals("admin_ban"))
				ban(wordList, activeChar);
			else if(command.equals("admin_acc_ban"))
			{
				if(wordList.length > 0)
				{
					int time = 0;
					String reason = "command by " + activeChar.getName();
					String account = wordList[0];
					if(account.equals("$target"))
					{
						if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
							return false;
						account = ((L2Player) activeChar.getTarget()).getAccountName();
					}
					if(wordList.length > 1)
						time = Integer.parseInt(wordList[1]);
					if(wordList.length > 2)
						reason = activeChar.getName() + ": " + wordList[2];
					LSConnection.getInstance().sendPacket(new ChangeAccessLevel(account, -100, reason, time));
					activeChar.sendMessage("You banned " + account + ", reason: " + reason);
					L2Player tokick = null;
					for(L2Player p : L2ObjectsStorage.getAllPlayersForIterate())
						if(p.getAccountName().equalsIgnoreCase(account))
						{
							tokick = p;
						}
					if(tokick != null)
					{
						tokick.logout(false, false, true, true);
						activeChar.sendMessage("Player " + tokick.getName() + " kicked.");
					}
				}
			}
			else if(command.equals("admin_trade_ban"))
				return tradeBan(wordList, activeChar);
			else if(command.equals("admin_trade_unban"))
				return tradeUnban(wordList, activeChar);
			else if(command.equals("admin_chatban"))
			{
				try
				{
					String player = wordList[0];
					String srok = wordList[1];
					String bmsg = "admin_chatban " + player + " " + srok + " ";
					String msg = args.substring(bmsg.length(), args.length());

					if(AutoBan.ChatBan(player, Integer.parseInt(srok), msg, activeChar.getName()))
						activeChar.sendMessage("You ban chat for " + player + ".");
					else
						activeChar.sendMessage("Can't find char " + player + ".");
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Command syntax: //chatban char_name period reason");
					e.printStackTrace();
				}
			}
			else if(command.equals("admin_chatunban"))
			{
				try
				{
					String player = wordList[0];

					if(AutoBan.ChatUnBan(player, activeChar.getName()))
						activeChar.sendMessage("You unban chat for " + player + ".");
					else
						activeChar.sendMessage("Can't find char " + player + ".");
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Command syntax: //chatunban char_name");
					e.printStackTrace();
				}
			}
			else if(command.equals("admin_jail"))
			{
				try
				{
					String player = wordList[0];
					String srok = wordList[1];

					L2Player target = L2World.getPlayer(player);

					if(target != null)
					{
						target.setVar("jailedFrom", target.getX() + ";" + target.getY() + ";" + target.getZ() + ";" + target.getReflection());
						target._unjailTask = ThreadPoolManager.getInstance().scheduleGeneral(new TeleportTask(target, target.getLoc(), -3), Integer.parseInt(srok) * 60000);
						target.setVar("jailed", srok);
						target.teleToLocation(-114648, -249384, -2984);
						activeChar.sendMessage("You jailed " + player + ".");
					}
					else
						activeChar.sendMessage("Can't find char " + player + ".");
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Command syntax: //jail char_name period");
					e.printStackTrace();
				}
			}
			else if(command.equals("admin_unjail"))
			{
				try
				{
					String player = wordList[0];

					L2Player target = L2World.getPlayer(player);

					if(target != null && target.getVar("jailed") != null)
					{
						String[] re = target.getVar("jailedFrom").split(";");
						target.teleToLocation(Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));
						target.setReflection(re.length > 3 ? Integer.parseInt(re[3]) : 0);
						target._unjailTask.cancel(true);
						target.unsetVar("jailedFrom");
						target.unsetVar("jailed");
						activeChar.sendMessage("You unjailed " + player + ".");
					}
					else
						activeChar.sendMessage("Can't find char " + player + ".");
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Command syntax: //unjail char_name");
					e.printStackTrace();
				}
			}
			else if(command.equals("admin_ckarma"))
			{
				try
				{
					String player = wordList[0];
					String srok = wordList[1];
					String bmsg = "admin_ckarma " + player + " " + srok + " ";
					String msg = args.substring(bmsg.length(), args.length());

					L2Player plyr = L2World.getPlayer(player);
					if(plyr != null)
					{
						int newKarma = Integer.parseInt(srok) + plyr.getKarma();

						// update karma
						plyr.setKarma(newKarma);

						plyr.sendMessage("You get karma(" + srok + ") by GM " + activeChar.getName());
						AutoBan.Karma(plyr, Integer.parseInt(srok), msg, activeChar.getName());
						activeChar.sendMessage("You set karma(" + srok + ") " + plyr.getName());
					}
					else if(AutoBan.Karma(player, Integer.parseInt(srok), msg, activeChar.getName()))
						activeChar.sendMessage("You set karma(" + srok + ") " + player);
					else
						activeChar.sendMessage("Can't find char: " + player);
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Command syntax: //ckarma char_name karma reason");
				}
			}
			else if(command.equals("admin_cban"))
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/cban.htm"));
		}

		return true;
	}

	private boolean tradeBan(String[] args, L2Player activeChar)
	{
		if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			return false;
		
		L2Player targ = (L2Player) activeChar.getTarget();

		long days = Long.parseLong(args[0]);
		long time = days * 24 * 60 * 60 * 1000L + System.currentTimeMillis();
		
		targ.setVar("tradeBan", String.valueOf(time));

		String msg = activeChar.getName() + " заблокировал торговлю персонажу " + targ.getName() + (days == -1 ? " на бессрочный период." : " на " + days + " дней.");

		Log.add(targ.getName() + ":" + days + tradeToString(targ, targ.getPrivateStoreType()), "tradeBan", activeChar);

		if(targ.isInOfflineMode())
		{
			targ.setOfflineMode(false);
			targ.logout(false, false, true, true);
			if(targ.getNetConnection() != null)
				targ.getNetConnection().disconnectOffline();
		}
		else if(targ.isInStoreMode())
		{
			targ.setPrivateStoreType(L2Player.STORE_PRIVATE_NONE);
			targ.broadcastUserInfo(true);
			targ.getBuyList().clear();
		}

		if(Config.MAT_ANNOUNCE_FOR_ALL_WORLD)
			Announcements.getInstance().announceToAll(msg);
		else
			Announcements.shout(activeChar, msg, Say2C.CRITICAL_ANNOUNCEMENT);
		return true;
	}

	@SuppressWarnings("unchecked")
	private static String tradeToString(L2Player targ, int trade)
	{
		String ret;
		Collection<?> list = null;
		switch(trade)
		{
			case L2Player.STORE_PRIVATE_BUY:
				list = targ.getBuyList();
				if(list == null || list.isEmpty())
					return "";
				ret = ":buy:";
				for(TradeItem i : (Collection<TradeItem>) list)
					ret += i.getItemId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":";
				return ret;
			case L2Player.STORE_PRIVATE_SELL:
			case L2Player.STORE_PRIVATE_SELL_PACKAGE:
				list = targ.getSellList();
				if(list == null || list.isEmpty())
					return "";
				ret = ":sell:";
				for(TradeItem i : (Collection<TradeItem>) list)
					ret += i.getItemId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":";
				return ret;
			case L2Player.STORE_PRIVATE_MANUFACTURE:
				list = targ.getCreateList().getList();
				if(list == null || list.isEmpty())
					return "";
				ret = ":mf:";
				for(L2ManufactureItem i : (Collection<L2ManufactureItem>) list)
					ret += i.getRecipeId() + ";" + i.getCost() + ":";
				return ret;
			default:
				return "";
		}
	}

	private boolean tradeUnban(String[] args, L2Player activeChar)
	{
		if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			return false;
		L2Player targ = (L2Player) activeChar.getTarget();

		targ.unsetVar("tradeBan");

		if(Config.MAT_ANNOUNCE_FOR_ALL_WORLD)
			Announcements.getInstance().announceToAll(activeChar + " разблокировал торговлю персонажу " + targ + ".");
		else
			Announcements.shout(activeChar, activeChar + " разблокировал торговлю персонажу " + targ + ".", Say2C.CRITICAL_ANNOUNCEMENT);

		Log.add(activeChar + " разблокировал торговлю персонажу " + targ + ".", "tradeBan", activeChar);
		return true;
	}

	private boolean ban(String[] args, L2Player activeChar)
	{
		try
		{
			String player = args[0];

			int time = 0;
			String msg = "";

			if(args.length > 1)
				time = Integer.parseInt(args[1]);

			if(args.length > 2)
			{
				msg = "admin_ban " + player + " " + time + " ";
				for(int i = 2; i < args.length; i++)
					msg += args[i] + " ";
				msg.trim();
			}

			L2Player plyr = L2World.getPlayer(player);
			if(plyr != null)
			{
				plyr.sendMessage(new CustomMessage("scripts.commands.admin.AdminBan.YoureBannedByGM", plyr).addString(activeChar.getName()));
				plyr.setAccessLevel(-100);
				AutoBan.Banned(plyr, time, msg, activeChar.getName());
				plyr.logout(false, false, true, true);
				activeChar.sendMessage("You banned " + plyr.getName());
			}
			else if(AutoBan.Banned(player, -100, time, msg, activeChar.getName()))
				activeChar.sendMessage("You banned " + player);
			else
				activeChar.sendMessage("Can't find char: " + player);
		}
		catch(Exception e)
		{
			activeChar.sendMessage("Command syntax: //ban char_name days reason");
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