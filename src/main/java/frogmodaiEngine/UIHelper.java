package frogmodaiEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import com.artemis.ComponentMapper;
import com.artemis.World;

import frogmodaiEngine.events.ScreenRefreshRequest;
import frogmodaiEngine.systems.*;
import frogmodaiEngine.components.*;
import net.mostlyoriginal.api.event.common.EventSystem;

public class UIHelper {
	/*ComponentMapper<Description> mDescription;
	boolean triggerRedraw = false;
	
	EventSystem es;
	
	public UIHelper(World world) {
		world.inject(this);
	}
	
	public void Pickup(int entity, ArrayList<Integer> entities) {
		Pickup(entity, entities, -1);
	}
	
	private Button newGrab(Window window, int e, int entity, ArrayList<Integer> entities, int i) {
		return new Button("Grab", new Runnable() {
			@Override
			public void run() {
				//Do pickup
				FFMain.worldManager.world.getSystem(PickupSystem.class).doPickup(entity, e);
				//Reload UI?
				window.close();
				entities.remove((Integer)e);
				Pickup(entity, entities, i);
			}
		});
	}
	
	public void Pickup(int entity, ArrayList<Integer> entities, int selected) {
		if (entities.size() < 1) return;
		
		//triggerRedraw = true;
		es.dispatch(new ScreenRefreshRequest());
		
		final Window window = new BasicWindow("Pick up items");
		Panel contentPanel = new Panel(new GridLayout(2));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(3);
		
		contentPanel.addComponent(new Button("Close", new Runnable() {
			@Override
			public void run() {
				window.close();
			}
		}));
		
		contentPanel.addComponent(new Button("Grab all", new Runnable() {
			@Override
			public void run() {
				//Do pickup
				FFMain.worldManager.world.getSystem(PickupSystem.class).doPickupAll(entity, entities);
				window.close();
			}
		}));
		
		contentPanel.addComponent(
				new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));

		ArrayList<Button> buttons = new ArrayList<Button>();
		for (int i = 0; i < entities.size(); i++) {
			int e = entities.get(i);
			Description desc = mDescription.create(e);
			contentPanel.addComponent(new Label(desc.name));
			int sel = i;
			if (i == entities.size()-1) sel--;
			Button b = newGrab(window, e, entity, entities, sel).setLayoutData(
					GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER));
			contentPanel.addComponent(b);
			buttons.add(b);
		}

		//}).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));

		window.setComponent(contentPanel);
		
		if (selected != -1 && selected < buttons.size()) {
			window.setFocusedInteractable(buttons.get(selected));
		}
		
		window.addWindowListener(new WindowListenerAdapter() {
			@Override
			public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
				if (keyStroke.getKeyType() == KeyType.Escape) {
					hasBeenHandled.set(true);
					basePane.close();
				}
			}
		});
		
		FFMain.textGUI.addWindowAndWait(window);
	}
	
	private Button newDrop(Window window, int e, int entity, ArrayList<Integer> entities, int i) {
		return new Button("Drop", new Runnable() {
			@Override
			public void run() {
				//Do pickup
				FFMain.worldManager.world.getSystem(DropSystem.class).dropItem(entity, e);
				//Reload UI?
				window.close();
				entities.remove((Integer)e);
				Drop(entity, entities, i);
			}
		});
	}
	
	public void Drop(int entity, ArrayList<Integer> entities) {
		Drop(entity, entities, -1);
	}
	
	public void Drop(int entity, ArrayList<Integer> entities, int selected) {
		if (entities.size() < 1) return;
		
		//triggerRedraw = true;
		es.dispatch(new ScreenRefreshRequest());
		
		final Window window = new BasicWindow("Drop items");
		Panel contentPanel = new Panel(new GridLayout(2));
		GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
		gridLayout.setHorizontalSpacing(3);
		
		contentPanel.addComponent(new Button("Close", new Runnable() {
			@Override
			public void run() {
				window.close();
			}
		}));
		
		contentPanel.addComponent(new Button("Drop all", new Runnable() {
			@Override
			public void run() {
				//Do pickup
				FFMain.worldManager.world.getSystem(DropSystem.class).dropAll(entity);
				window.close();
			}
		}));
		
		contentPanel.addComponent(
				new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));

		ArrayList<Button> buttons = new ArrayList<Button>();
		for (int i = 0; i < entities.size(); i++) {
			int e = entities.get(i);
			Description desc = mDescription.create(e);
			contentPanel.addComponent(new Label(desc.name));
			int sel = i;
			if (i == entities.size()-1) sel--;
			Button b = newDrop(window, e, entity, entities, sel).setLayoutData(
					GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER));
			contentPanel.addComponent(b);
			buttons.add(b);
		}

		window.setComponent(contentPanel);
		
		if (selected != -1 && selected < buttons.size()) {
			window.setFocusedInteractable(buttons.get(selected));
		}
		
		window.addWindowListener(new WindowListenerAdapter() {
			@Override
			public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
				if (keyStroke.getKeyType() == KeyType.Escape) {
					hasBeenHandled.set(true);
					basePane.close();
				}
			}
		});
		
		FFMain.textGUI.addWindowAndWait(window);
	}*/
}
