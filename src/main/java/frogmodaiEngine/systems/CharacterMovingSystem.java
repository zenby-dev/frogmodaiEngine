package frogmodaiEngine.systems;

import java.util.LinkedList;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
//import com.artemis.EntitySystemTest.C;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.*;
//import frogmodaiEngine.commands.*;
import frogmodaiEngine.components.*;
import frogmodaiEngine.events.MoveAttempt;
import frogmodaiEngine.events.MoveCollision;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class CharacterMovingSystem extends BaseEntitySystem {
	ComponentMapper<Position> mPosition;
	ComponentMapper<Mobile> mMobile;
	ComponentMapper<VirtualController> mVirtualController;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<OnTile> mOnTile;
	ComponentMapper<Tile> mTile;
	ComponentMapper<IsPlayer> mIsPlayer;
	ComponentMapper<OnTouch> mOnTouch;
	ComponentMapper<OnTouched> mOnTouched;
	ComponentMapper<CameraWindow> mCameraWindow;
	ComponentMapper<Sight> mSight;
	// TODO: set player OnTile

	EventSystem es;

	public CharacterMovingSystem() {
		super(Aspect.all(Position.class, Mobile.class, VirtualController.class, ChunkAddress.class));
	}

	private boolean tryMove(int e, int dx, int dy) {
		// Access Components
		Position pos = mPosition.create(e);
		Mobile mobile = mMobile.create(e);
		VirtualController virtualController = mVirtualController.create(e);
		ChunkAddress chunkAddress = mChunkAddress.create(e);
		Chunk chunk = FrogmodaiEngine.worldManager.getChunk(chunkAddress.worldID);
		OnTile onTile = mOnTile.create(e);
		if (onTile.tile == -1) {
			onTile.tile = chunk.getTile(pos.x, pos.y);
		}
		// TODO: Exception?
		Tile tile = mTile.create(onTile.tile);

		int targetX = pos.x;
		int targetY = pos.y;

		// if (!(virtualController.peek() instanceof MoveCommand)) // only skim off and
		// process move commands!
		// return false;

		// for (Command command : virtualController.actionList.toArray(new Command[1]))
		// {
		// if (command instanceof MoveCommand) {
		// MoveCommand move = (MoveCommand) command;
		// virtualController.actionList.remove(command);
		// And it will only go when earlier commands have been processed by their system
		// (ie a MoveCommand is at the front of the queue)

		// Read controller input
		targetX = pos.x + dx;// + move.dx;
		targetY = pos.y + dy;// + move.dy;

		// System.out.println(String.format("%d %d", targetX, targetY));

		int dir = DirectionConverter.toInt(new Position(dx, dy));

		// Failure points
		// If it does fail, all the moves that it didn't make it to in this list will
		// try again and again and again...
		if (!chunk.posInChunk(targetX, targetY) || tile.neighbors[dir] != -1) { // Any outside or special
			// POTENTIALLY MOVING INTO A DIFFERENT CHUNK
			// If tile.atplayerpos.neighborInDirectionPlayerIsMoving then
			// move player there
			// DONE: Player should be able to grab what tile they're on
			// So they can grab their neighboring tiles

			if (tile.neighbors[dir] == -1)
				return false; // But if neither, fail to move
			int neighbor = tile.neighbors[dir]; // potential new tile to move to

			Position tilePos = mPosition.create(neighbor); // Change the ent's position to be chunk-relative
			// We know what tile we're going to because neighbors
			ChunkAddress tileChunkAddress = mChunkAddress.create(neighbor);

			Chunk newChunk = FrogmodaiEngine.worldManager.getChunk(tileChunkAddress.worldID);
			CameraWindow camWindow = mCameraWindow.create(FrogmodaiEngine.cameraID);
			Position camPos = mPosition.create(FrogmodaiEngine.cameraID);
			Position camOffset = new Position();

			if (newChunk.isSolid(tilePos.x, tilePos.y)) {
				collisionEvent(e, neighbor);
				return false;
			}
			if (newChunk.isOccupied(tilePos.x, tilePos.y)) {
				collisionEvent(e, neighbor);
				return false;
			}

			boolean sameChunk = chunkAddress.worldID == tileChunkAddress.worldID;

			// These were before failure points!!!! Don't mutate data b4 failure points
			// please.
			chunkAddress.worldID = tileChunkAddress.worldID;

			camOffset.x = camPos.x - pos.x;
			camOffset.y = camPos.y - pos.y;

			//if (chunk.getTile(pos.x, pos.y).entitiesHere)
			chunk.updateOccupied(pos.x, pos.y); // IS THIS CORRECT??? what if something else is still there?? or is
													// that impossible
			pos.x = tilePos.x;
			pos.y = tilePos.y;
			onTile.tile = neighbor;
			// onTile.tile = newChunk.getTile(pos.x, pos.y);
			newChunk.updateOccupied(pos.x, pos.y);
			if (mIsPlayer.has(e)) {
				if (!sameChunk)
					FrogmodaiEngine.worldManager.shiftChunks(newChunk);

				int nx = pos.x + camOffset.x - dx;
				int ny = pos.y + camOffset.y - dy;
				// nx %= camWindow.width;
				// ny %= camWindow.height;
				// if (nx < 0)
				// nx += camWindow.width;
				// if (ny < 0)
				// ny += camWindow.height;
				// if (nx >= camWindow.width)
				// nx %= camWindow.width;
				// if (ny >= camWindow.height)
				// ny %= camWindow.height;
				camPos.x = nx;
				camPos.y = ny;
			}
		} else { // New position is within same chunk
			int neighbor = chunk.getTile(targetX, targetY);
			if (chunk.isSolid(targetX, targetY)) {
				collisionEvent(e, neighbor);
				return false;
			}
			if (chunk.isOccupied(targetX, targetY)) {
				collisionEvent(e, neighbor);
				return false;
			}

			// Locking in new values (if no failure!)
			chunk.updateOccupied(pos.x, pos.y); // update occupation per-move
			pos.x = targetX;
			pos.y = targetY;
			onTile.tile = neighbor; // THIS DOES NOT WORK FOR CHANGING CHUNKS THO
			chunk.updateOccupied(pos.x, pos.y);
		}
		// }
		// }

		tile.cachedLOS = false;
		Tile newTile = mTile.create(onTile.tile);
		newTile.cachedLOS = false;

		// Failures to move don't make it here
		// Successful movement of not necessarily the player, but the perspective,
		// necessitates redraw
		// if (FrogmodaiEngine.worldManager.getRenderingPerspective() == e)
		// FrogmodaiEngine.worldManager.triggerTileRedraw();

		return true;
	}

	private void collisionEvent(int e, int neighbor) { // idk how i feel about this
		// should use the new event system
		// and consider having collisions like this only activate
		// very specific context sensitive actions?
		// And otherwise use standard roguelike controls or an "action" key + direction
		// like caves of qud
		FrogmodaiEngine.logEventEmit("CharacterMovingSystem", "MoveCollision");
		FrogmodaiEngine.dispatch(new MoveCollision(e, neighbor));
	}

	private void process(int e) {
		Position pos = mPosition.create(e);
		int x = pos.x;
		int y = pos.y;

		// ***TODO*** Only move if it's your turn!
		//boolean success = tryMove(e);

		// Access Components
		// pos = mPosition.create(e);
		// Mobile mobile = mMobile.create(e);
		// VirtualController virtualController = mVirtualController.create(e);
		// ChunkAddress chunkAddress = mChunkAddress.create(e);
		// Chunk chunk = FrogmodaiEngine.worldManager.getChunk(chunkAddress.worldID);

		/*
		 * if (success) { // HOPE THIS COVERS IT //if (x != pos.x || y != pos.y)
		 * //chunk.setOccupied(x, y, false); //just always set where you were to
		 * unoccupied, because only you will have the chance to move there in this cycle
		 * 
		 * chunk.setOccupied(pos.x, pos.y, true); }
		 */
	}

	@Subscribe(ignoreCancelledEvents=true)
	public void MoveAttemptDuringListener(MoveAttempt.During event) {
		//System.out.println(event.entity + ", " + event.dx + ", " + event.dy);
		boolean success = tryMove(event.entity, event.dx, event.dy);
		if (!success) event.setCancelled(true);
		if (success) {
			if (mSight.has(event.entity)) {
				mSight.get(event.entity).refreshNeeded = true;
			}
		}
	}

	@Override
	protected void processSystem() {
		// TODO Auto-generated method stub
		/*
		 * LinkedList<Integer> entities =
		 * FrogmodaiEngine.worldManager.world.getSystem(TimeSystem.class).queue; for (Integer e :
		 * entities) { process(e); }
		 */
	}

}
