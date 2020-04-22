package frogmodaiEngine.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

import frogmodaiEngine.FrogmodaiEngine;

public class CameraWindow extends Component {
	public int width = FrogmodaiEngine.screenWidth;
	public int height = FrogmodaiEngine.screenHeight;
	@EntityId public int focus;
	public int tolerance;
}
