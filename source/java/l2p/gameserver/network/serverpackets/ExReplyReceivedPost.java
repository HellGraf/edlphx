package l2p.gameserver.network.serverpackets;

import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.TradeItem;
import l2p.gameserver.model.items.MailParcelController;
import l2p.gameserver.model.items.MailParcelController.Letter;

public class ExReplyReceivedPost extends L2GameServerPacket
{
	private Letter _letter;

	public ExReplyReceivedPost(L2Player cha, int post)
	{
		_letter = MailParcelController.getInstance().getLetter(post);

		if(_letter == null)
			_letter = new Letter();
		else if(_letter.unread > 0)
		{
			MailParcelController.getInstance().markMailRead(post);
			_letter.unread = 0;
		}

		cha.sendPacket(new ExShowReceivedPostList(cha));
	}

	// dddSSS dx[hddQdddhhhhhhhhhh] Qdd
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xAB);

		writeD(_letter.id); // id письма
		writeD(_letter.price > 0 ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо
		writeD(_letter.system); // для писем с флагом "от news informer" в отправителе значится "****", всегда оверрайдит тип на просто письмо

		writeS(_letter.senderName); // от кого
		writeS(_letter.topic); // топик
		writeS(_letter.body); // тело

		writeD(_letter.attached.size()); // количество приложенных вещей

		for(TradeItem temp : _letter.attached)
		{
			writeItem(temp);
			writeD(temp.getObjectId());
		}

		writeQ(_letter.price); // для писем с оплатой - цена
		writeD(_letter.attachments > 0 ? 1 : 0); // 1 - письмо можно вернуть
		writeD(_letter.system); // 1 - на письмо нельзя отвечать, его нельзя вернуть, в отправителе значится news informer (или "****" если установлен флаг в начале пакета)
	}
}