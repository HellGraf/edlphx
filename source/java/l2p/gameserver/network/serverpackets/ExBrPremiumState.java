package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2Player;

public class ExBrPremiumState extends L2GameServerPacket
{
	private int _objId;
	private int _state;

	public ExBrPremiumState(L2Player activeChar, int state)
	{
		_objId = activeChar.getObjectId();
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xd9);
		writeD(_objId);
		writeC(_state);
	}
}
