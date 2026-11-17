package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2Clan;

/**
 * sample
 * 0000: cd b0 98 a0 48 1e 01 00 00 00 00 00 00 00 00 00    ....H...........
 * 0010: 00 00 00 00 00                                     .....
 *
 * format   ddddd
 */
public class PledgeStatusChanged extends L2GameServerPacket
{
	private int _leaderId, _clanId, _crestId, _allyId, _allyCrestId;

	public PledgeStatusChanged(L2Clan clan)
	{
		_leaderId = clan.getLeaderId();
		_clanId = clan.getClanId();
		_crestId = clan.getCrestId();
		_allyId = clan.getAllyId();
		_allyCrestId = clan.getAlliance().getAllyCrestId();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xCD);
		writeD(_leaderId);
		writeD(_clanId);
		writeD(_crestId);
		writeD(_allyId);
		writeD(_allyCrestId);
		writeD(0);
		writeD(0);
	}
}