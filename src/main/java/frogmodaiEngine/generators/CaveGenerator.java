package frogmodaiEngine.generators;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.sudoplay.joise.*;
import com.sudoplay.joise.module.*;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import frogmodaiEngine.*;
import frogmodaiEngine.components.*;

public class CaveGenerator implements LevelGenerator {
	ComponentMapper<Position> mPosition;
	ComponentMapper<Char> mChar;
	ComponentMapper<Tile> mTile;
	ComponentMapper<ChunkAddress> mChunkAddress;

	public CaveGenerator(World world) {
		world.inject(this);
	}

	public Chunk generate(Chunk chunk) {
		ModuleFractal gen = new ModuleFractal();
		gen.setAllSourceBasisTypes(BasisType.SIMPLEX);
		gen.setAllSourceInterpolationTypes(InterpolationType.CUBIC);
		gen.setNumOctaves(5);
		gen.setFrequency(2.34);
		gen.setType(FractalType.RIDGEMULTI);
		gen.setSeed(898456);

		ModuleAutoCorrect ac = new ModuleAutoCorrect();
		ac.setSource(gen);
		ac.setRange(-1.0f, 1.0f);
		ac.setSamples(10000);
		ac.calculate2D();

		// Cave pass
		for (int y = 0; y < chunk.height; y++) {
			for (int x = 0; x < chunk.width; x++) {
				int i = x + y * chunk.width;
				int t = chunk.tiles[i];
				Position pos = mPosition.create(t);
				Tile tile = mTile.create(t);
				Char character = mChar.create(t);
				double xoffset = 0;
				double yoffset = 0;
				double xscale = 128.0;
				double yscale = 128.0;
				double z = 0.0;
				int seed = 3;
				double v = ac.get((x + xoffset) / xscale, (y + yoffset) / yscale);
				// System.out.println(v);
				if (v < 0.0) {
					character.tile.character = '.';
					character.tile.style.fgc = ColorUtils.Colors.RED;
					character.tile.style.bgc = ColorUtils.Colors.YELLOW;
					tile.solid = false;
				} else {
					character.tile.character = ' ';
					character.tile.style.fgc = ColorUtils.Colors.BLACK;
					character.tile.style.bgc = ColorUtils.Colors.BLACK;
					tile.solid = true;
				}
			}
		}

		// Wall pass
		for (int y = 0; y < chunk.height; y++) {
			for (int x = 0; x < chunk.width; x++) {
				int i = x + y * chunk.width;
				int t = chunk.tiles[i];
				Position pos = mPosition.create(t);
				Tile tile = mTile.create(t);
				Char character = mChar.create(t);

				if (tile.solid == false) {
					for (int dy = -1; dy < 2; dy++) {
						for (int dx = -1; dx < 2; dx++) {
							int nx = x + dx;
							int ny = y + dy;
							int ni = nx + ny * chunk.width;
							if (ni >= 0 && ni < chunk.tiles.length) { //valid tile?
								int nt = chunk.tiles[ni];
								Position npos = mPosition.create(nt);
								Tile ntile = mTile.create(nt);
								Char ncharacter = mChar.create(nt);
								if (ntile.solid == true) { // Solid adjacent to open
									ncharacter.tile.character = '#';
									ncharacter.tile.style.fgc = ColorUtils.Colors.YELLOW;
									ncharacter.tile.style.bgc = ColorUtils.Colors.RED;
								}
							}
						}
					}
				}

				// System.out.println(v);
				/*
				 * if (v < 0.0) { character.character = '.'; character.fgc = 1; character.bgc =
				 * 3; tile.solid = false; } else { character.character = ' '; character.fgc = 0;
				 * character.bgc = 0; tile.solid = true; }
				 */
			}
		}
		return chunk;
		// Noise.gradientCoherentNoise3D(arg0, arg1, arg2, arg3, arg4)
	}
}
