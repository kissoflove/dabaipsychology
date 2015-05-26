package consult.psychological.dabai.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Query;

public class Db {

	private static Context ctx;

	private static ObjectContainer db;

	private static ExecutorService execService = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	public static <T> void save(T... ts) {
		if (ts != null && ts.length > 0) {
			for (T t : ts) {
				getDb().store(t);
			}
			commit();
		}
	}

	public static <T> void save(List<T> list) {
		if (list != null && !list.isEmpty()) {
			for (T t : list) {
				getDb().store(t);
			}
			commit();
		}
	}

	public static <T> void del(T t) {
		if (t != null) {
			getDb().delete(t);
			commit();
		}
	}

	public static <T> void delAll(Class<T> clazz) {
		Query q = getDb().query();
		q.constrain(clazz);
		ObjectSet<T> tmp = q.execute();
		if (!tmp.isEmpty()) {
			for (T u : tmp) {
				getDb().delete(u);
			}
			getDb().commit();
		}
	}

	public static <T> void delByParam(Class<T> clazz, Map<String, Object> param) {
		Query q = getQuery(clazz);
		for (String k : param.keySet()) {
			q.descend(k).constrain(param.get(k));
		}
		ObjectSet<T> tmp = q.execute();
		if (!tmp.isEmpty()) {
			for (T t : tmp) {
				getDb().delete(t);
			}
			getDb().commit();
		}
	}

	public static <T> Query getQuery(Class<T> clazz) {
		Query q = getDb().query();
		q.constrain(clazz);	
		return q;
	}

	public static void put(String k, Object v) {

		del(k);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("k", k);
		delByParam(Kv.class, param);
		if (v != null) {
			Kv kv = new Kv();
			kv.k = k;
			kv.v = v;
			save(kv);
		}
	}

	public static void putAsync(final String k, final Object v,
			final Callback... cbs) {
		execService.execute(new Runnable() {
			public void run() {
				put(k, v);
				if (cbs != null) {
					for (Callback cb : cbs) {
						cb.done(true);
					}
				}
			}
		});
	}

	public static void getAsync(final String k, final Callback cb) {
		execService.execute(new Runnable() {
			public void run() {
				Object obj = get(k);
				if (cb != null) {
					cb.done(obj);
				}
			}
		});
	}

	public static Object get(String k) {

		Object obj = null;
		Query q = getQuery(Kv.class);
		q.descend("k").constrain(k);
		ObjectSet<Kv> tmp = q.execute();
		if (!tmp.isEmpty()) {
			obj = tmp.get(0).v;
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(String k, Class<T> clazz) {
		return (T) get(k);
	}

	public static <T> int count(Class<T> clazz) {
		Query q = getDb().query();
		q.constrain(clazz);
		ObjectSet<T> tmp = q.execute();
		return tmp.size();
	}

	public synchronized static void init(Context context) {
		ctx = context;
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Kv.class).objectField("k").indexed(true);
		if (db == null || db.ext().isClosed()) {
			db = Db4oEmbedded.openFile(config, ctx.getFilesDir() + "/db.db");
		}
		
	}

	public static ObjectContainer getDb() {
		while (db == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
        if (db.ext().isClosed()) {
            db = db.ext().openSession();
        }
		return db;
	}

	public static void close() {
		getDb().close();
	}

	public static void commit() {
		getDb().commit();
	}

	private static class Kv {

		public String k;
		public Object v;
	}

	public static interface Callback {
		public void done(Object obj);
	}

}
