package frogmodaiEngine.generators;

import frogmodaiEngine.*;

public interface LevelGenerator {
	//Takes in a chunk
	//Marks things
	//Follows rules
	//Outputs a changed chunk
	public Chunk generate(Chunk chunk);
}
