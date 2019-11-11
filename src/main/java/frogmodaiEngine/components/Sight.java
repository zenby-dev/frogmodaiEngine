package frogmodaiEngine.components;

import java.lang.reflect.Array;
import java.util.HashMap;

import com.artemis.Component;

public class Sight extends Component {
	public int distance = 12;
	public boolean blinded = false;
	public float darkSight = 0.25f; //Multiplier for distance
	public HashMap<String, RelativePosition> visibleTiles = new HashMap<String, RelativePosition>();
	public boolean refreshNeeded = true;
	public void clear() {
		/*Object[] keys = visibleTiles.keySet().toArray();
		//System.out.println(keys.length);
		for (int i = 0; i < keys.length; i++) {
			String key = (String)keys[i];
			RelativePosition rel = visibleTiles.get(key);
			//visibleTiles.remove(key);
			RelativePosition.dispose(rel);
		}*/
		visibleTiles.clear();
	}
}
