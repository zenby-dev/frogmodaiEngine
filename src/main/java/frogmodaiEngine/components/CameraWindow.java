package frogmodaiEngine.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class CameraWindow extends Component {
	public int width = 32;
	public int height = 32;
	@EntityId public int focus;
	public int tolerance;
}
