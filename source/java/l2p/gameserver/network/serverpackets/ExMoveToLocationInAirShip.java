package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.entity.vehicle.L2AirShip;
import l2p.gameserver.util.Location;

public class ExMoveToLocationInAirShip extends L2GameServerPacket
{
	private int _charObjId, _airShipId, _heading;
	private Location _destination;

	public ExMoveToLocationInAirShip(L2Player cha, L2AirShip boat, Location origin, Location destination)
	{
		_charObjId = cha.getObjectId();
		_airShipId = boat.getObjectId();
		_heading = cha.getHeading();
		_destination = destination;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x6D);
		writeD(_charObjId);
		writeD(_airShipId);

		writeD(_destination.x);
		writeD(_destination.y);
		writeD(_destination.z);
		writeD(_heading);
	}
}