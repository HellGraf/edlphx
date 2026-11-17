package l2p.gameserver.network.serverpackets;

import l2p.commons.util.GArray;
import l2p.gameserver.model.entity.vehicle.L2AirShip;
import l2p.gameserver.tables.AirShipDocksTable;
import l2p.gameserver.tables.AirShipDocksTable.AirShipDock;

public class ExAirShipTeleportList extends L2GameServerPacket
{
	private int _dockId;
	private GArray<AirShipDock> _airports;

	public ExAirShipTeleportList(L2AirShip ship)
	{
		int currentDockNpcId = ship.getCurrentDockNpcId();
		if(currentDockNpcId == 0)
			return;
		
		AirShipDock ad = AirShipDocksTable.getInstance().getAirShipDockByNpcId(currentDockNpcId);
		
		_dockId = ad.getId();
		_airports = AirShipDocksTable.getInstance().getAirShipDocksForTeleports(currentDockNpcId);
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x9A);
		writeD(_dockId);
		
		writeD(_airports.size());
		for(AirShipDock airport : _airports)
		{
			writeD(airport.getId()); // AirportID
			writeD(airport.getFuel()); // need fuel
			writeD(airport.getLoc().x); // Airport x
			writeD(airport.getLoc().y); // Airport y
			writeD(airport.getLoc().z); // Airport z
		}
	}
}