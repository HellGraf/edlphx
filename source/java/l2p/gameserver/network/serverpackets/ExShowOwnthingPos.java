package l2p.gameserver.network.serverpackets;

import java.util.Map.Entry;
import java.util.Set;

import l2p.gameserver.model.entity.siege.territory.TerritorySiege;
import l2p.gameserver.util.Location;

public class ExShowOwnthingPos extends L2GameServerPacket
{
	private Set<Entry<Integer, Location>> _locs = null;

	public ExShowOwnthingPos()
	{
		_locs = TerritorySiege.getWardsLoc().entrySet();
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x93);
		writeD(_locs.size());
		for(Entry<Integer, Location> e : _locs)
		{
			writeD(e.getValue().id);
			writeD(e.getValue().x);
			writeD(e.getValue().y);
			writeD(e.getValue().z);
		}
	}
}