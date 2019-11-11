package frogmodaiEngine.components;
/*package frogmodaiGame.components;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class RelativePositionFactory extends BasePooledObjectFactory<RelativePosition> {

	@Override
	public RelativePosition create() throws Exception {
		return new RelativePosition();
	}

	@Override
	public PooledObject<RelativePosition> wrap(RelativePosition pos) {
		return new DefaultPooledObject<RelativePosition>(pos);
	}

	@Override
    public void passivateObject(PooledObject<RelativePosition> pooledObject) {
        RelativePosition rel = pooledObject.getObject();
        rel.x = 0;
        rel.y = 0;
        rel.e = -1;
        rel.dx = 0;
        rel.dy = 0;
        rel.pathLength = 0;
    }
}*/