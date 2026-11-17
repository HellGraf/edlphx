package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2Player;

public class L2FriendStatus extends L2GameServerPacket
{
	private String _charName;
	private boolean _online = false;
	private int _objid;

	public L2FriendStatus(L2Player player, boolean online)
	{
		if(player == null)
			return;
		_online = online;
		_objid = player.getObjectId();
		_charName = player.getName();
	}

	@Override
	protected final void writeImpl()
	{
		if(_charName == null)
			return;
		writeC(0x77);
		writeD(_online ? 1 : 0); //Logged in 1 logged off 0
		writeS(_charName);
		writeD(_objid); //id персонажа с базы оффа, не object_id
	}
}