package frogmodaiEngine.components;

import java.util.ArrayList;

import com.artemis.Component;

public class Description extends Component {
	//Descriptions have different levels based on character insight level
	//Insight is determined by distance, knowledge/lore, listening, looking, concentration, atmospheric conditions, etc
	//That should probably be handled in a system or something
	public String name = "Something"; //Everything has a name
	public ArrayList<String> descriptions;
	
	public Description() {
		descriptions = new ArrayList<String>();
		descriptions.add("You're not sure what this is.");
	}
	
	public void addDescription(String desc) { 
		//Newer definitions automatically require higher insight
		//But this is only for inspections
		descriptions.add(desc);
	}
	
	public String getDescription() {
		return getDescription(0);
	}
	
	public String getDescription(int ins) {
		int _insight = ins;
		int insight = Math.min(_insight, descriptions.size());
		return descriptions.get(insight);
	}
}
