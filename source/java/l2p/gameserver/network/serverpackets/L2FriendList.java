package l2p.gameserver.network.serverpackets;

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2p.commons.database.DatabaseUtils;
import l2p.commons.database.FiltredPreparedStatement;
import l2p.commons.database.L2DatabaseFactory;
import l2p.commons.database.ThreadConnection;
import l2p.commons.util.GArray;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;

public class L2FriendList extends L2GameServerPacket
{
	private static Logger _log = Logger.getLogger(L2FriendList.class.getName());
	private GArray<FriendInfo> friends = new GArray<FriendInfo>();
	private L2Player _cha;
	private boolean _message = false;
	private boolean _packet = false;

	public L2FriendList(L2Player cha)
	{
		_cha = cha;
		_message = true;
		_packet = false;
		common();
	}

	public L2FriendList(L2Player cha, boolean sendMessage)
	{
		_cha = cha;
		_message = sendMessage;
		_packet = true;
		common();
	}

	private void common()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		FriendInfo friendinfo;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT friend_id, char_name FROM character_friends LEFT JOIN characters ON ( character_friends.friend_id = characters.obj_Id ) WHERE char_id=?");
			statement.setInt(1, _cha.getObjectId());
			rset = statement.executeQuery();

			if(_message)
				_cha.sendPacket(Msg._FRIENDS_LIST_);
			while(rset.next())
			{
				friendinfo = new FriendInfo(rset.getString("char_name"));
				L2Player friend = L2World.getPlayer(friendinfo.name);
				if(friend == null)
				{
					if(_message)
						_cha.sendPacket(new SystemMessage(SystemMessage.S1_CURRENTLY_OFFLINE).addString(friendinfo.name));
					friendinfo.id = 0;
					friendinfo.online = false;
				}
				else
				{
					if(_message)
						_cha.sendPacket(new SystemMessage(SystemMessage.S1_CURRENTLY_ONLINE).addString(friendinfo.name));
					friendinfo.id = friend.getObjectId();
					friendinfo.online = true;
				}
				friends.add(friendinfo);
			}
			if(_message)
				_cha.sendPacket(Msg.__EQUALS__);
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "Error in friendlist ", e);
			_packet = false;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(!_packet)
			return;

		writeC(0x75);
		writeD(friends.size());
		for(FriendInfo friend : friends)
		{
			writeD(friend.id);
			writeS(friend.name);
			writeD(friend.online ? 1 : 0); //online or offline
			writeD(friend.online ? friend.id : 0); //object_id
		}
	}

	private class FriendInfo
	{
		public final String name;
		public int id = 0;
		public boolean online = false;

		public FriendInfo(String _name)
		{
			name = _name;
		}
	}
}