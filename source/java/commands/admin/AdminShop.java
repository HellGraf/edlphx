package commands.admin;

import l2p.gameserver.TradeController;
import l2p.gameserver.TradeController.NpcTradeList;
import l2p.gameserver.extensions.Stat;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.BuyList;
import l2p.gameserver.network.serverpackets.ExBuySellList;
import l2p.gameserver.network.serverpackets.NpcHtmlMessage;
import l2p.gameserver.scripts.ScriptFile;

public class AdminShop implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_buy",
		"admin_gmshop",
		"admin_tax",
		"admin_taxclear"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().UseGMShop)
			return false;

		if(command.equals("admin_buy"))
		{
			try
			{
				handleBuyRequest(activeChar, args);
			}
			catch(IndexOutOfBoundsException e)
			{
				activeChar.sendMessage("USAGE: //buy <list_id>");
			}
		}
		else if(command.equals("admin_gmshop"))
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/gmshops.htm"));
		else if(command.equals("admin_tax"))
			activeChar.sendMessage("TaxSum: " + Stat.getTaxSum());
		else if(command.equals("admin_taxclear"))
		{
			Stat.addTax(-Stat.getTaxSum());
			activeChar.sendMessage("TaxSum: " + Stat.getTaxSum());
		}

		return true;
	}

	private void handleBuyRequest(L2Player activeChar, String command)
	{
		int val = -1;

		try
		{
			val = Integer.parseInt(command);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		NpcTradeList list = TradeController.getInstance().getBuyList(val);

		if(list != null)
		{
			activeChar.sendPacket(new BuyList(list, activeChar, 0));
			activeChar.sendPacket(new ExBuySellList(activeChar, true));
		}

		activeChar.sendActionFailed();
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
