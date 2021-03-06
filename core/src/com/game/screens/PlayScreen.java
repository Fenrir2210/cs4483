package com.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.game.Application;
import com.game.levels.Level;

public class PlayScreen implements Screen{

	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	Application game;
	
	private int numberOfEnemies = 2;

	private int stageCount = 0;
  
	private int timer = 0;
	
	private int dialogueCounter = 0;
	private Texture healthPip = new Texture("healthPip.png");
	
	private boolean switchedToLayer2, switchedToLayer3, switchedToLayer4;
	
	//Dialogue arrays\\
	private String dialogueLayer2[] = {"I feel strange, as though I am somehow lighter.", "What images of ascension reveal themselves to me?", "I can reach new heights!"};
	private String dialogueLayer3[] = {"All my life, I felt something gnawing at the back of my mind...", "Mercy, mercy! Such vistas of emptiness, show me no more!", "Oh god, the eyes! The eyes!!"};	
	public static Level currLevel;
	public String currStage[] = {"", "Stage1_a.tmx", "Stage2_a.tmx", "Stage3_a.tmx", "Stage4_a.tmx"};
	
	BitmapFont dialogueFont = new BitmapFont();
	SpriteBatch spriteBatch = new SpriteBatch();
	BitmapFont font = new BitmapFont();
	
	
	private boolean debug;
	
	public PlayScreen(boolean debug, int stageCount) {
		this.debug = debug;
		this.stageCount=stageCount;
	}
	
	public PlayScreen(boolean debug) {
		this.debug = debug;
	}

	public PlayScreen(boolean debug, int stageCount, Application game) {
		this.debug = debug;
		this.stageCount = stageCount;
		
		this.game = game;
		font.setColor(1, 1, 1, 1);
	}

	@Override
	public void render(float delta){

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.position.set(currLevel.getPlayer().getX() + currLevel.getPlayer().getWidth() / 2, currLevel.getPlayer().getY() + currLevel.getPlayer().getHeight() / 2, 0);
		fixBounds();
		camera.update();
		
		renderer.setView(camera); //Tell the renderer to use the camera we made.
		
		/**THIS IS IMPORTANT!! We need to pass an array of layers to the renderer, so it knows what order to render the layers in. So its very important, in Tiled
		/to keep layers organized and neat.*/
		renderer.getBatch().begin();
		renderer.renderTileLayer((TiledMapTileLayer) currLevel.getMap().getLayers().get("Background")); //Renders the background of our Tiled maps
		
		
		// Space to render enemies
		currLevel.renderEnemies(renderer.getBatch(), this.debug);
		currLevel.renderBubbles(renderer.getBatch(), this.debug);
		
	    //Renders the player
		currLevel.getPlayer().update(Gdx.graphics.getDeltaTime(), currLevel.getEnemyArray());
		currLevel.getPlayer().drawPlayer(renderer.getBatch(), this.debug);
		
		renderer.renderTileLayer((TiledMapTileLayer) currLevel.getMap().getLayers().get("Foreground")); //Renders the foreground platforms
		
		renderer.getBatch().end();
		
		if(this.debug == true) {
			
			spriteBatch.begin();
			font.draw(spriteBatch, "x = " + currLevel.getPlayer().getX(), 10, 50);
			font.draw(spriteBatch, "y = " + currLevel.getPlayer().getY(), 150, 50);
			font.draw(spriteBatch, "currentLayer = " + currLevel.getPlayer().getCurrentLayer(), 300, 50);
			
			font.draw(spriteBatch, "isFacingRight = " + currLevel.getPlayer().getFacingRight(), 10, 30);
			font.draw(spriteBatch, "State = " + currLevel.getPlayer().getState(), 150, 30);
			font.draw(spriteBatch, "HP = " + currLevel.getPlayer().getHealthPoints(), 300, 30);
			font.draw(spriteBatch, "isInvincible = " + currLevel.getPlayer().isInvincible(), 450, 30);
			font.draw(spriteBatch, "onWinTile = " + currLevel.getPlayer().getVictory(), 750, 30);
			font.draw(spriteBatch, "isCollided = " + currLevel.getPlayer().getCollidedX(), 875, 30);
			if(currLevel.getPlayer().isInvincible()) {
				font.draw(spriteBatch, "IFRAMES = " + currLevel.getPlayer().getInvincibleTimer(), 600, 30);
			}
			spriteBatch.end();
		}
		
		spriteBatch.begin();
		spriteBatch.draw(currLevel.getPlayer().healthPortrait, 30, 
				(2 * camera.viewportHeight) - (2 * currLevel.getPlayer().healthPortrait.getRegionHeight()));
		
		// draw the number of health pips based on the player's currentHP
		int playerHP = currLevel.getPlayer().getHealthPoints();
		switch(playerHP) {
		case(3):
			spriteBatch.draw(this.healthPip, 30 + (healthPip.getWidth() * 3) - 20, 
					(2 * camera.viewportHeight) - (2 * currLevel.getPlayer().healthPortrait.getRegionHeight()));
		case(2):
			spriteBatch.draw(this.healthPip, 30 + (healthPip.getWidth() * 2) - 10 , 
					(2 * camera.viewportHeight) - (2 * currLevel.getPlayer().healthPortrait.getRegionHeight()));
		case(1):
			spriteBatch.draw(this.healthPip, 30 + healthPip.getWidth(), 
					(2 * camera.viewportHeight) - (2 * currLevel.getPlayer().healthPortrait.getRegionHeight()));
			break;
		default:
			break;
		}
		
		spriteBatch.end();
		
		if(currLevel.getPlayer().getPromptTile() && currLevel.getPlayer().getCurrentLayer() == 1)
		{
			setDialogue("Press E to shift", 675, 390);
		}
		else
		{
				clearDialogue();	
		}
		
		if(currLevel.getPlayer().getCurrentLayer() == 2)
		{
			if(timer < 240)
			{
				timer+=1;
			setDialogue(dialogueLayer2[dialogueCounter], 675, 390);
			}
			else
			{
				clearDialogue();
				dialogueCounter++;
				if(dialogueCounter > 2)
					dialogueCounter = 0;
				switchedToLayer2 = true;
			}
		}
		
		if(currLevel.getPlayer().getCurrentLayer() == 3)
		{
			if(switchedToLayer2)
			{
				timer = 0;
				switchedToLayer2 = false;
				switchedToLayer3 = true;
			}
			
			if(timer < 200 && switchedToLayer3 == true)
			{
			timer+=1;
			setDialogue(dialogueLayer3[dialogueCounter], 675, 390);
			}
			else
			{
				clearDialogue();
				dialogueCounter++;
				if(dialogueCounter > 2)
					dialogueCounter = 0;
				switchedToLayer3 = false;
				switchedToLayer4 = true;
			}
		}
		
		if(currLevel.getPlayer().isDead())
		{
			
			dispose();
			game.setScreen(new GameOverScreen(game));
		}
		
		if(currLevel.getPlayer().getVictory()) //If the player encounters a 'victory tile,' change the current level the player is on
		{
			currLevel.getPlayer().getMusicForLayer1().dispose();
			currLevel.getPlayer().getMusicForLayer2().dispose();
			currLevel.getPlayer().getMusicForLayer3().dispose();
			stageCount++;
			dispose();
			game.setScreen(new StageStartScreen(game, stageCount));
			
			try 
			{
				Thread.sleep(600);
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}

			resize(1280, 730); //Honestly our code is pretty messy so a super cheesy way to 'update' the screen is to just resize it to the same thing it already is

		}
		
	}
	
	public void setDialogue(String str, int xPos, int yPos)
	{	
		dialogueFont.setColor(1, 1, 1, 1);
		spriteBatch.begin();
		dialogueFont.draw(spriteBatch, str, xPos, yPos);
		spriteBatch.end();
	}
	
	public void clearDialogue()
	{
		dialogueFont.setColor(1, 1, 1, 0);
	}
	
	@Override
	public void resize(int width, int height)
	{
		/*
		 * The viewport is gonna help us focus in closer to the game, so we dont have a zoomed out game. 
		 * */
		camera.viewportWidth = width / 2;
		camera.viewportHeight = height / 2;
	}

	@Override
	public void show()
	{
		// create new Level object
		currLevel = new Level(new TmxMapLoader().load(currStage[stageCount]), new  //currStage holds the string value of the current stage the player is on
				Vector2(48, 128)); // create the level
		
		renderer = new OrthogonalTiledMapRenderer(currLevel.getMap()); //Create the renderer
		
		camera = new OrthographicCamera(); //create a new camera focused on the map we are rendering
	}
	
	@Override
	public void pause() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void hide()
	{
		// TODO Auto-generated method stub

	}


	@Override
	public void dispose() 
	{
		currLevel.disposeMap();
		renderer.dispose();
		currLevel.getPlayer().getMusicForLayer1().dispose();
		currLevel.getPlayer().getMusicForLayer2().dispose();
		currLevel.getPlayer().getMusicForLayer3().dispose();
	}

	/*
	 * Method that constrains the camera to the actual map space, hiding the unsightly abyss from sight
	 */
	public void fixBounds() {
	    // Horizontal
	    if (camera.position.x < camera.viewportWidth / 2)
	        camera.position.x = camera.viewportWidth / 2;
	    else if (camera.position.x > (currLevel.getMap().getProperties().get("width", Integer.class) * 32) - camera.viewportWidth / 2)
	        camera.position.x = (currLevel.getMap().getProperties().get("width", Integer.class) * 32) - camera.viewportWidth / 2;
	    else
	    	camera.position.x = currLevel.getPlayer().getX() + currLevel.getPlayer().getWidth() / 2;
	    
	    // Vertical
	    if (camera.position.y < camera.viewportHeight / 2)
	        camera.position.y = camera.viewportHeight / 2;
	    else if (camera.position.y > (currLevel.getMap().getProperties().get("height", Integer.class) * 32) - camera.viewportHeight / 2)
	        camera.position.y = (currLevel.getMap().getProperties().get("height", Integer.class) * 32) - camera.viewportHeight / 2;
	    else
	    	camera.position.y = currLevel.getPlayer().getY() + currLevel.getPlayer().getHeight() / 2;
	    
	    //camera.position.z = 0;
	}
	
}
