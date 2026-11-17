package l2p.gameserver.network.clientpackets;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.HennaEquipList;
import l2p.gameserver.tables.HennaTreeTable;

public class RequestHennaList extends L2GameClientPacket
{
	// format: cd

	@Override
	public void readImpl()
	{
		
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		activeChar.sendPacket(new HennaEquipList(activeChar, HennaTreeTable.getInstance().getAvailableHenna(activeChar.getClassId(), activeChar.getSex())));
	}
}