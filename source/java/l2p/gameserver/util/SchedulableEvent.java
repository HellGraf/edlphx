package l2p.gameserver.util;

import l2p.gameserver.listeners.MethodInvokeListener;
import l2p.gameserver.listeners.events.MethodEvent;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.scripts.Functions;

public class SchedulableEvent implements MethodInvokeListener
{
	public String _class;
	public String _method;
	public Integer _delay;
	public String[] _args;

	public SchedulableEvent(String clas, String method, String[] args, Integer delay)
	{
		_class = clas;
		_method = method;
		_delay = delay;
		_args = args;
	}

	@Override
	public boolean accept(MethodEvent event)
	{
		return true;
	}

	@Override
	public void methodInvoked(MethodEvent e)
	{
		Functions.executeTask((L2Object) e.getOwner(), _class, _method, new Object[] { _args }, _delay);
		//((L2Object) e.getOwner()).removeMethodInvokeListener(e.getMethodName(), this);
	}
}