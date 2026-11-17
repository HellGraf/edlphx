package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2Player;

public class ExBrExtraUserInfo extends L2GameServerPacket
{
	private int _charId, _val;

	public ExBrExtraUserInfo(L2Player cha)
	{
		_charId = cha.getObjectId();
		_val = 0; //TODO: _val = cha.getEventEffectId();
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xDA);
		writeD(_charId);
		writeD(_val);
	}
}