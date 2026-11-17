package l2p.gameserver.network.clientpackets;

import java.util.logging.Logger;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.network.serverpackets.ExListPartyMatchingWaitingRoom;

/** 
 * Format: dddd
 */
public class RequestListPartyMatchingWaitingRoom extends L2GameClientPacket
{
	protected static final Logger _log = Logger.getLogger(RequestListPartyMatchingWaitingRoom.class.getName());

	private int _minLevel, _maxLevel, _page, _size, _classes[];

	@Override
	protected void readImpl()
	{
		_page = readD();
		_minLevel = readD();
		_maxLevel = readD();
		_size = readD();
		if(_size > Byte.MAX_VALUE || _size < 0) _size = 0;
		_classes = new int[_size];
		for(int i = 0; i < _size; i++)
			_classes[i] = readD();
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, _minLevel, _maxLevel, _page));
	}
}