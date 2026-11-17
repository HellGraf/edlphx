package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.entity.vehicle.L2AirShip;
import l2p.gameserver.util.Location;

public class ExAirShipInfo extends L2GameServerPacket
{
	private int _objId, _speed1, _speed2, _fuel, _driverObjId, _helm;
	private Location _loc;

	public ExAirShipInfo(L2AirShip ship)
	{
		_objId = ship.getObjectId();
		_loc = ship.getLoc();
		_speed1 = ship.getRunSpeed();
		_speed2 = ship.getRotationSpeed();
		_fuel = ship.getFuel();
		_driverObjId = ship.getDriver() == null ? 0 : ship.getDriver().getObjectId();
		//_ownerObjId = ship.getOwner() == null ? 0 : ship.getOwner().getClanId();
		_helm = 1; //TODO: _helm = ship.getHelmObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x60);

		writeD(_objId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
		writeD(_driverObjId); // object id of player who control ship
		writeD(_speed1);
		writeD(_speed2);
		writeD(_helm);
		if (_helm != 0)
		{
			// TODO: unhardcode these!
			writeD(0x16e); // Controller X
			writeD(0x00); // Controller Y
			writeD(0x6b); // Controller Z
			writeD(0x15c); // Captain X
			writeD(0x00); // Captain Y
			writeD(0x69); // Captain Z
		}
		else
		{
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
		}
		writeD(_fuel); // current fuel
		writeD(L2AirShip.MAX_FUEL); // max fuel
	}
}