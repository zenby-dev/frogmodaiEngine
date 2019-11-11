package frogmodaiEngine.components;

import java.util.NoSuchElementException;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.artemis.annotations.EntityId;

public class RelativePosition {

	/*private static ObjectPool<RelativePosition> pool;
	
	public static void init() {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(50000);
		pool = new GenericObjectPool<RelativePosition>(new RelativePositionFactory(), config);
	}
	
	public static RelativePosition borrow() {
		try {
			return pool.borrowObject();
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void dispose(RelativePosition rel) {
		if (rel == null) return;
		try {
			pool.returnObject(rel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	@EntityId public int e = -1;
	public int x = 0;
	public int y = 0;
	public int dx = 0;
	public int dy = 0;
	public float pathLength = 0;
	public boolean withinDistance(Position p, float d) { //squared
		//I'm pretty sure this is wrong
		return distanceSquared(p) < d*d;
	}
	public float distanceSquared(Position p) { //squared
		//I'm pretty sure this is wrong
		return (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y);
	}
	
	public String toString() {
		return x+"|"+y;
	}
}
