package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.TradeItem;
import l2p.gameserver.model.items.MailParcelController;
import l2p.gameserver.model.items.MailParcelController.Letter;

public class ExReplySentPost extends L2GameServerPacket
{
	private Letter _letter;

	public ExReplySentPost(L2Player cha, int post)
	{
		_letter = MailParcelController.getInstance().getLetter(post);

		if(_letter == null)
		{
			_letter = new Letter();
			cha.sendPacket(new ExShowSentPostList(cha));
		}
	}

	// ddSSS dx[hddQdddhhhhhhhhhh] Qd
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xAD);

		writeD(_letter.id); // id письма
		writeD(_letter.price > 0 ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо

		writeS(_letter.receiverName); // кому
		writeS(_letter.topic); // топик
		writeS(_letter.body); // тело

		writeD(_letter.attached.size());
		for(TradeItem temp : _letter.attached)
		{
			writeItem(temp);
			writeD(temp.getObjectId());
		}

		writeQ(_letter.price); // для писем с оплатой - цена
		writeD(_letter.system); // getSendBySystem
	}
}