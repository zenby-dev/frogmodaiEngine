package frogmodaiEngine.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class DirectedPortalDef extends Component {
	@EntityId public int[] tiles;
	public int width;
	public int dir;
	public int ox;
	public int oy;
	public int dx;
	public int dy;
	
	//int chunk; //ChunkAddress
	//int x; //Position
	//int y; 
}
