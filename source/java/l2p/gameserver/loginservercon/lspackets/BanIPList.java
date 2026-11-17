package l2p.gameserver.loginservercon.lspackets;

import l2p.commons.util.BannedIp;
import l2p.commons.util.GArray;
import l2p.gameserver.loginservercon.AttLS;

public class BanIPList extends LoginServerBasePacket
{
	GArray<BannedIp> baniplist = new GArray<BannedIp>();

	public BanIPList(byte[] decrypt, AttLS loginServer)
	{
		super(decrypt, loginServer);
	}

	@Override
	public void read()
	{
		int size = readD();
		for(int i = 0; i < size; i++)
		{
			BannedIp ip = new BannedIp();
			ip.ip = readS();
			ip.admin = readS();
			baniplist.add(ip);
		}
	}
}