package frogmodaiEngine.generators;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.artemis.ComponentMapper;

import frogmodaiEngine.Chunk;
import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.components.Char;
import frogmodaiEngine.components.Tile;

//Map filestructure:
//main folder: maps
//Each map is a subfolder (names with the map)
// maps/AbandonedFactory
//	/foregroud.png
//	/background.png
//  /metadata.png
//  /characters.txt

public class MapLoader {
	ComponentMapper<Char> mChar;
	ComponentMapper<Tile> mTile;

	final String workingDir = System.getProperty("user.dir");
	private ArrayList<String> maptxt;
	
	public MapLoader() {
		//System.out.println(workingDir);
		maptxt = new ArrayList<String>();
		
		//loadMapFromANSI("/maps/AbandonedFactory/room.ans");
	}
	
	public void loadMapFromANSI(String filepath) {
		File f = new File(workingDir + filepath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			//StringBuilder b = new StringBuilder();
			//int c;
			try {
				//while ((c=br.read())!=-1) {
				maptxt.clear();
				String str;
				while ((str=br.readLine())!=null) {
					//b.append((char)c);
					maptxt.add(str);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//maptxt = b.toString();
			//System.out.println(maptxt);
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void drawMap() {
		for (int i = 0; i < maptxt.size(); i++) {
			System.out.printf("%s, %s\n", i, maptxt.get(i));
			//FrogmodaiEngine.screen.newTextGraphics().putCSIStyledString(0, i, maptxt.get(i));
		}
	}
	
	public int loadChunkFromDirectory(String dirName) {
		MapInfo mapInfo = loadInfoFromDirectory(dirName);
		MapLayers mapLayers = loadMapFromInfo(mapInfo);
		int chunk = loadMapLayersToChunk(mapLayers);
		return chunk;
	}
	
	private MapInfo loadInfoFromDirectory(String dirName) {
		MapInfo mapInfo = new MapInfo();
		String path = "maps/"+dirName+"/";
		mapInfo.backgroundFile = path + "background.png";
		mapInfo.foregroundFile = path + "foreground.png";
		mapInfo.metadataFile = path + "metadata.png";
		mapInfo.characterFile = path + "chracters.txt";
		return mapInfo;
	}
	
	private int loadMapLayersToChunk(MapLayers mapLayers) {
		int c = FrogmodaiEngine.worldManager.createChunk(mapLayers.w, mapLayers.h);
		Chunk chunk = FrogmodaiEngine.worldManager.getChunk(c);
		for (int x = 0; x < mapLayers.w; x++) {
			for (int y = 0; y < mapLayers.h; y++) {
				int i = y * mapLayers.w + x;
				int background = mapLayers.background.getRGB(x, y);
				int foreground = mapLayers.foreground.getRGB(x, y);
				int metadata = mapLayers.metadata.getRGB(x, y);
				char ch = mapLayers.characters[i];
				Char tileChar = mChar.create(chunk.getTile(x, y));
				/*tileChar.bgc = FrogmodaiEngine.RGBToLanterna(background);
				tileChar.fgc = FrogmodaiEngine.RGBToLanterna(foreground);
				tileChar.character = ch;
				tileChar.bold = (FrogmodaiEngine.blue(metadata) == 255);
				Tile tile = mTile.create(chunk.getTile(x, y));
				tile.solid = (FrogmodaiEngine.red(metadata) == 255);*/
			}
		}
		return c;
	}

	private MapLayers loadMapFromInfo(MapInfo mapInfo) {
		MapLayers map = new MapLayers();
		map.background = getImage(mapInfo.backgroundFile);
		map.foreground = getImage(mapInfo.foregroundFile);
		map.metadata = getImage(mapInfo.metadataFile);
		map.w = map.background.getWidth();
		map.h = map.background.getHeight();
		map.characters = getCharacters(mapInfo.characterFile, map.w, map.h);
		return map;
	}

	private char[] getCharacters(String filename, int w, int h) {
		try {
			File f = new File(workingDir + filename);
			BufferedReader br = new BufferedReader(new FileReader(f));

			char[] chars = new char[w * h];

			int c;
			int i = 0;
			while ((c = br.read()) != -1) {
				if ((char) c != '\n') {
					chars[i] = (char) c;
					i++;
				}
			}
			
			return chars;
		} catch (IOException e) {
			System.out.println("The image was not loaded.");
			// System.exit(1);
		}

		return null;
	}

	private BufferedImage getImage(String filename) {
		// This time, you can use an InputStream to load
		try {
			// Grab the InputStream for the image.
			InputStream in = getClass().getResourceAsStream(filename);
			
			// Then read it.
			return ImageIO.read(in);
		} catch (IOException e) {
			System.out.println("The image was not loaded.");
			// System.exit(1);
		}

		return null;
	}

	private class MapLayers {
		BufferedImage background;
		BufferedImage foreground;
		BufferedImage metadata;
		char[] characters;
		int w;
		int h;
	}

	private class MapInfo {
		String backgroundFile;
		String foregroundFile;
		String metadataFile;
		String characterFile;
	}
}

/*try {
ImageIO.write(map.metadata, "png", new File(""));
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}*/

/*File f = new File(workingDir + "/maps/AbandonedFactory/room.ans");
BufferedReader br;
try {
	br = new BufferedReader(new FileReader(f));
	String str = "";
	maptxt = "";
	while((str = br.readLine()) != null) {
		maptxt = maptxt + str;
	}
} catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}*/