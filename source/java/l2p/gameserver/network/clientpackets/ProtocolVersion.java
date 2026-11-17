package l2p.gameserver.network.clientpackets;

import java.util.logging.Logger;

import l2p.gameserver.Config;
import l2p.gameserver.network.L2GameClient;
import l2p.gameserver.network.serverpackets.KeyPacket;
/**
 * packet type id 0x0E
 * format:	cdbd
 */
public class ProtocolVersion extends L2GameClientPacket
{
	static Logger _log = Logger.getLogger(ProtocolVersion.class.getName());

	private static final KeyPacket wrong_protocol = new KeyPacket(null);
	private KeyPacket pk;
	private long _version;
	private byte[] _data = new byte[256];

	@Override
	public void readImpl()
	{
		L2GameClient _client = getClient();
		if(_buf.remaining() < 4)
		{
			// Проверки рейтинга типа l2top.in.ua
			_client.close(wrong_protocol);
			return;
		}
		_version = readD();
		if(_version == -2 || _buf.remaining() == 0)
		{
			_client.close(wrong_protocol);
			return;
		}

		readB(_data);
		if(_buf.remaining() >= 4)
			readD(); //?
		
		pk = new KeyPacket(_client.enableCrypt());
	}

	@Override
	public void runImpl()
	{
		if(_version == -2)
			return;
		L2GameClient _client = getClient();
		if(_version < Config.MIN_PROTOCOL_REVISION || _version > Config.MAX_PROTOCOL_REVISION)
		{
			_log.info("Client Protocol Revision: " + _version + ", client IP: " + _client.getIpAddr() + " not allowed. Supported protocols: from " + Config.MIN_PROTOCOL_REVISION + " to " + Config.MAX_PROTOCOL_REVISION + ". Closing connection.");
			_client.close(wrong_protocol);
			return;
		}
		_client.setRevision((int) _version);

		sendPacket(pk);
	}
}