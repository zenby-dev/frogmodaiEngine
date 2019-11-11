package frogmodaiEngine.systems;

import java.util.Random;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import frogmodaiEngine.ColorUtils;
import frogmodaiEngine.PScreen;
import frogmodaiEngine.PTile;
import frogmodaiEngine.components.*;

public class SphereRenderingSystem extends IteratingSystem {
	ComponentMapper<SphereInfo> mSphereInfo;
	ComponentMapper<Position> mPosition;

	PScreen screen;
	ModuleFractal gen;
	ModuleAutoCorrect ac;
	Random random;

	public SphereRenderingSystem(PScreen _screen) {
		super(Aspect.all(SphereInfo.class, Position.class));
		screen = _screen;
		gen = new ModuleFractal();
		gen.setAllSourceBasisTypes(BasisType.SIMPLEX);
		gen.setAllSourceInterpolationTypes(InterpolationType.CUBIC);
		gen.setNumOctaves(5);
		gen.setFrequency(2.34);
		gen.setType(FractalType.RIDGEMULTI);
		gen.setSeed(898456);

		ac = new ModuleAutoCorrect();
		ac.setSource(gen);
		ac.setRange(-1.0f, 1.0f);
		ac.setSamples(10000);
		ac.calculate2D();
		
		random = new Random();
	}

	@Override
	protected void process(int e) {
		Position pos = mPosition.create(e);
		SphereInfo sphereInfo = mSphereInfo.create(e);
		int r = sphereInfo.radius;
		int phiSteps = 20;
		int thetaSteps = 20;
		int subSteps = 5;
		double threshold = r * 0.99;
		char[] chars = {'#', '@', '$', '%', '&', '~'};
		for (double _phi = 0; _phi < Math.PI; _phi += Math.PI / phiSteps) {
			for (double _theta = 0; _theta < Math.PI; _theta += Math.PI / thetaSteps) {
				for (int k = 0; k < subSteps; k++) {
					for (int l = 0; l < subSteps; l++) {
						double po = k * (Math.PI/phiSteps/subSteps);
						double to = l * (Math.PI/thetaSteps/subSteps);
						double theta = _theta + to;
						double phi = _phi + po;
						theta %= Math.PI;
						phi %= Math.PI;
						double ax = r * Math.sin(phi) * Math.cos(theta);
						double ay = r * Math.cos(phi);
						if (ax * ax + ay * ay < threshold * threshold) { //+ ((double)(sphereInfo.tick)/500.0)
							int x = pos.x + (int) (ax * 2);
							int y = pos.y + (int) ay;
							theta += ((double)(sphereInfo.tick)/sphereInfo.speed);
							int c = ((int)Math.floor(((theta%Math.PI)/Math.PI)*6 + ((phi%Math.PI)/Math.PI)*6) % 6);
							double xoffset = 0.0;
							double yoffset = 0.0;
							double xscale = 8.0;
							double yscale = 8.0;
							double v = ac.get(((theta%(Math.PI*2))+xoffset)/xscale,  ((phi%(Math.PI*2))+yoffset)/yscale);
							if (v < 0.0) {
								screen.set(x, y, new PTile('~', ColorUtils.Colors.CYAN, ColorUtils.Colors.BLUE));
							} else if (v < 0.5) {
								screen.set(x, y, new PTile(',', ColorUtils.Colors.YELLOW, ColorUtils.Colors.GREEN));
							} else {
								screen.set(x, y, new PTile('^', ColorUtils.Colors.YELLOW, ColorUtils.Colors.WHITE));
							}
						}
					}
				}
			}
		}
		sphereInfo.tick++;
	}

}
