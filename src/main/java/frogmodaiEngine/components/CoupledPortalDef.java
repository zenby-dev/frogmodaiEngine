package frogmodaiEngine.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class CoupledPortalDef extends Component {
	@EntityId public int portal1;
	@EntityId public int portal2;
}

