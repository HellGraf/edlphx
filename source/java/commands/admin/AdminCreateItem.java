package commands.admin;

import l2p.commons.util.Log;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.items.L2ItemInstance;
import l2p.gameserver.network.serverpackets.ItemList;
import l2p.gameserver.network.serverpackets.NpcHtmlMessage;
import l2p.gameserver.network.serverpackets.SystemMessage;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.tables.ItemTable;

public class AdminCreateItem implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_itemcreate",
		"admin_create_item",
		"admin_ci"
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
		
		if(command.equals("admin_itemcreate"))
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/itemcreation.htm"));
		else if(command.equals("admin_create_item") || command.equals("admin_ci"))
		{
			try
			{
				if(args.length() == 0)
				{
					activeChar.sendMessage("USAGE: //create_item id [count]");
					return false;
				}
				
				String[] wordList = args.split("\\s+");
				int item_id = Integer.parseInt(wordList[0]);
				long item_count = wordList.length < 2 ? 1 : Long.parseLong(wordList[1]);
				createItem(activeChar, item_id, item_count);
			}
			catch(NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: //create_item id [count]");
			}
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/itemcreation.htm"));
		}

		return true;
	}

	private void createItem(L2Player activeChar, int id, long num)
	{
		L2ItemInstance createditem = ItemTable.getInstance().createItem(id);
		createditem.setCount(num);
		activeChar.getInventory().addItem(createditem);
		Log.LogItem(activeChar, Log.Adm_AddItem, createditem);
		if(!createditem.isStackable())
			for(long i = 0; i < num - 1; i++)
			{
				createditem = ItemTable.getInstance().createItem(id);
				activeChar.getInventory().addItem(createditem);
				Log.LogItem(activeChar, Log.Adm_AddItem, createditem);
			}
		activeChar.sendPacket(new ItemList(activeChar, true), SystemMessage.obtainItems(id, num, 0));
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
