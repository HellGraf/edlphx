package l2p.gameserver.scripts;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.logging.Logger;

import l2p.commons.util.GArray;
import l2p.gameserver.GameServer;
import l2p.gameserver.scripts.jarloader.JarClassLoader;

public class Scripts
{
	private static final Logger _log = Logger.getLogger(Scripts.class.getName());

	public static boolean JAR;

	private static Scripts _instance;
	public static boolean loading;

	private final HashMap<String, Script> _classes = new HashMap<String, Script>();
	public static final HashMap<Integer, GArray<ScriptClassAndMethod>> itemHandlers = new HashMap<Integer, GArray<ScriptClassAndMethod>>();
	public static final HashMap<Integer, GArray<ScriptClassAndMethod>> dialogAppends = new HashMap<Integer, GArray<ScriptClassAndMethod>>();
	public static final HashMap<String, ScriptClassAndMethod> onAction = new HashMap<String, ScriptClassAndMethod>();
	public static final HashMap<String, ScriptClassAndMethod> onActionShift = new HashMap<String, ScriptClassAndMethod>();
	public static final GArray<ScriptClassAndMethod> onPlayerExit = new GArray<ScriptClassAndMethod>();
	public static final GArray<ScriptClassAndMethod> onPlayerEnter = new GArray<ScriptClassAndMethod>();
	public static final GArray<ScriptClassAndMethod> onReloadMultiSell = new GArray<ScriptClassAndMethod>();
	public static final GArray<ScriptClassAndMethod> onDie = new GArray<ScriptClassAndMethod>();
	public static final GArray<ScriptClassAndMethod> onEscape = new GArray<ScriptClassAndMethod>();

	public static Scripts getInstance()
	{
		if(_instance == null)
			new Scripts();
		return _instance;
	}

	public Scripts()
	{
		_instance = this;
		load(false);
	}

	public boolean reload()
	{
		loading = true;

		for(ScriptObject go : GameServer.scriptsObjects.values())
			try
			{
				go.invokeMethod("onReload");
			}
			catch(Exception f)
			{
				f.printStackTrace();
			}
		GameServer.scriptsObjects.clear();

		boolean error = load(true);
		callOnLoad();

		loading = false;
		return error;
	}

	public void shutdown()
	{
		for(ScriptObject go : GameServer.scriptsObjects.values())
			try
			{
				go.invokeMethod("onShutdown");
			}
			catch(Exception f)
			{
				f.printStackTrace();
			}
		GameServer.scriptsObjects.clear();
	}

	private synchronized boolean load(boolean reload)
	{
		_log.info("Scripts loading...");
		boolean error = false;
		Class<?> c;

		JAR = new File("./scripts.jar").exists();

		if(JAR)
		{
			JarClassLoader jcl;
			try
			{
				jcl = new JarClassLoader("./scripts.jar");
				for(String name : jcl.getClassNames())
				{
					if(!name.contains(".class"))
						continue;
					if(name.contains("$"))
						continue; // пропускаем вложенные классы
					name = name.replace(".class", "").replace("/", ".");
					c = jcl.loadClass(name);
					Script s = new Script(c);
					_classes.put(c.getName(), s);
				}
			}
			catch(Exception e)
			{
				error = true;
				e.printStackTrace();
			}
		}
		else
		{
			error = true;
		}

		if(error)
		{
			_log.info("Scripts loaded with errors. Loaded " + _classes.size() + " classes.");
			if(!reload)
			{
				System.out.println("Server halting [status=0] / Reason: Scripts loaded with errors. Loaded " + _classes.size() + " classes.");
				Runtime.getRuntime().halt(0);
			}
		}
		else
			_log.info("Scripts successfully loaded. Loaded " + _classes.size() + " classes.");
		return error;
	}

	public void callOnLoad()
	{
		loadAndInitHandlers();
	}

	private void loadAndInitHandlers()
	{
		itemHandlers.clear();
		dialogAppends.clear();
		onAction.clear();
		onActionShift.clear();
		onPlayerExit.clear();
		onPlayerEnter.clear();
		onReloadMultiSell.clear();
		onDie.clear();
		onEscape.clear();

		for(Script _class : _classes.values())
			loadAndInitHandlersForClass(_class);
	}

	private void loadAndInitHandlersForClass(Script _class)
	{
		try
		{
			if(!GameServer.scriptsObjects.containsKey(_class.getName()))
			{
				ScriptObject go = _class.newInstance();
				GameServer.scriptsObjects.put(_class.getName(), go);
				go.invokeMethod("onLoad");
			}

			for(Method method : _class.getRawClass().getMethods())
				if(method.getName().equals("onLoad"))
				{
					if(Modifier.isStatic(method.getModifiers()))
						method.invoke(null);
				}
				else if(method.getName().contains("ItemHandler_"))
				{
					Integer id = Integer.parseInt(method.getName().substring(12));
					GArray<ScriptClassAndMethod> handlers = itemHandlers.get(id);
					if(handlers == null)
					{
						handlers = new GArray<ScriptClassAndMethod>();
						itemHandlers.put(id, handlers);
					}
					handlers.add(new ScriptClassAndMethod(_class, method));
				}
				else if(method.getName().contains("DialogAppend_"))
				{
					Integer id = Integer.parseInt(method.getName().substring(13));
					GArray<ScriptClassAndMethod> handlers = dialogAppends.get(id);
					if(handlers == null)
					{
						handlers = new GArray<ScriptClassAndMethod>();
						dialogAppends.put(id, handlers);
					}
					handlers.add(new ScriptClassAndMethod(_class, method));
				}
				else if(method.getName().contains("OnAction_"))
				{
					String name = method.getName().substring(9);
					if(onAction.containsKey(name))
						onAction.remove(name);
					onAction.put(name, new ScriptClassAndMethod(_class, method));
				}
				else if(method.getName().contains("OnActionShift_"))
				{
					String name = method.getName().substring(14);
					if(onActionShift.containsKey(name))
						onActionShift.remove(name);
					onActionShift.put(name, new ScriptClassAndMethod(_class, method));
				}
				else if(method.getName().equals("OnPlayerExit"))
					onPlayerExit.add(new ScriptClassAndMethod(_class, method));
				else if(method.getName().equals("OnPlayerEnter"))
					onPlayerEnter.add(new ScriptClassAndMethod(_class, method));
				else if(method.getName().equals("OnReloadMultiSell"))
					onReloadMultiSell.add(new ScriptClassAndMethod(_class, method));
				else if(method.getName().equals("OnDie"))
					onDie.add(new ScriptClassAndMethod(_class, method));
				else if(method.getName().equals("OnEscape"))
					onEscape.add(new ScriptClassAndMethod(_class, method));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public HashMap<String, Script> getClasses()
	{
		return _classes;
	}

	public static class ScriptClassAndMethod
	{
		public final Script scriptClass;
		public final Method method;

		public ScriptClassAndMethod(Script _scriptClass, Method _method)
		{
			scriptClass = _scriptClass;
			method = _method;
		}
	}
}