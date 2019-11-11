package frogmodaiEngine.components;

import com.artemis.Component;

import net.mostlyoriginal.api.event.common.Subscribe;

public class Stat extends Component {
	public int maxValue=1;
	public int currentValue=1;
	public String name = "HP";

	/*public Stat(String _name, int _maxValue) {
		name = _name;
		maxValue = _maxValue;
		currentValue = maxValue;
	}

	public Stat(int _maxValue) {
		maxValue = _maxValue;
		currentValue = maxValue;
	}*/
	
	public void setMaxValue(int _maxValue) {
		maxValue = _maxValue;
		//currentValue = maxValue;
	}
	
	public void setMaxValue(int _maxValue, boolean aaa) {
		maxValue = _maxValue;
		if (aaa)
			currentValue = maxValue;
	}

	public void changeValue(int amount) {
		currentValue += amount;
		currentValue = Math.min(currentValue, maxValue);
	}
}


