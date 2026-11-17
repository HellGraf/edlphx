package l2p.gameserver.network.serverpackets;

import java.util.Collection;

import l2p.gameserver.instancemanager.CastleManager;
import l2p.gameserver.model.entity.residence.Castle;
import l2p.gameserver.model.entity.residence.Residence;

public class ExSendManorList extends L2GameServerPacket
{
	private static Collection<Castle> _castles = CastleManager.getInstance().getCastles().values();

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x22);
		writeD(_castles.size());
		for(Residence castle : _castles)
		{
			writeD(castle.getId());
			writeS(castle.getName().toLowerCase());
		}
	}
}