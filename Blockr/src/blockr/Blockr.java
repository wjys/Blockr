package blockr;

//import java.io.IOException;
import java.util.ArrayList;

import ddf.minim.AudioPlayer;
import ddf.minim.AudioSample;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import themidibus.MidiBus;


public class Blockr extends PApplet {

	private static final long serialVersionUID = 1L;
	
	
	
	// ***World-Coordinates
	//----------------------------------
	float centerX = width/2;
	float centerY = height/2;
	
	float continuedX = 0.1f;
	float continuedY = 0.1f;
	
	static boolean startGame = false;
	static boolean noGame = true;
	boolean keyEnter = false;
	
	boolean End = false;
	static boolean inGame = false;
	static boolean endGame = false;
	static boolean preGame = true;
	
	public PImage image;
	public PImage imageWin;
	public PImage imageLoss;
	
	
	
	// ***Camera variables
	//----------------------------------
	float meR,meG,meB;
	static float menowX = 0.0f;
	static float menowY = 0.0f;
	
	
	// ***Background
	//----------------------------------
	ArrayList<PVector> stars = new ArrayList<PVector>();
	
	float bri = 100.0f;
	float bridir = random(1,3);
	
	int totalNovas = 20;
	Star[] novas = new Star[totalNovas];

	int totalVortexes = 20;
	Vortex[] v = new Vortex[totalVortexes];
	
	// ***Shield Variables
	//----------------------------------

	float aRed1 = 255;
	float aGreen1 = 255;
	float aBlue1 = 255;
	float aWeight1 = 2;
	float aAlpha1 = 100;
	float aDist1 = 0;
	float paDist1 = 0;
	float aRotSpeed1;
	float aSpeedX = 0;
	float aSpeedY = 0;
	float aRotateShip = 0;
	
	int angleOfShape;
	int angleOfShip;
	
	
	
	//Collision Stuff
	float shieldRad = 200.0f;
	int colCheck;
	float bulletAngle;
	float shieldAngle;
	boolean entered = false;
	boolean ouch = false;
	
	int errorMargin = 50;
	
	float shieldStrength;
	int shieldL = 0;
	
	
	
	// ***Me Variables
	//----------------------------------
	public PImage esq;
	public PImage tri;
	
	PVector mePos;
	PVector refPos;
	PVector meDir;
	float meAccel = 0.5f;
	float meSpeed = 0.2f;
	float meStartX, meStartY;
	
	PVector meCol;
	int meRad;
	int meC;
	
	float health;
	float healthBar;
	float scoreBar;
	
	
	
	// ***Juice Variables
	//----------------------------------
	//Shield Juice
	float [] jX = new float[100];
	float [] jY = new float[100];
	float [] jA = new float[100];
	float [] jB = new float[100];
	float [] jDOTx = new float[500];
	float [] jDOTy = new float [500];
	
	float jRad = 4;
		
	float jRand = random (-250,250);
	float jRand2 = random (-250,250);
	float jRand3 = random (-250,250);
	
	float jCenterX = width/2;
	float jCenterY = height/2;
	
	float jRed = random (100,200);
	float jGreen = random (100, 200);
	float jBlue= random (100, 200);
	
	
	
	// ***MIDI Variables
	//----------------------------------
	MidiBus nanoKontrol;
	boolean shieldKnobInput = false;
	
	
	
	// ***Bullet Variables
	//----------------------------------
	int totalBullets = 10;
	Bullet[] bullets = new Bullet[totalBullets];
	
	//Bullet bull = new Bullet(400,200,50,255,0,0);
	public boolean colliding = false;
	
	
	
	//DEBUG
	PVector testPos[] = new PVector[totalBullets];
	float angleIncoming[] = new float[totalBullets];
	
	//TIMER
	
	int savedTime;
	int totalTime;
	int passedTime;
	
	// ***SOUND VARIABLES
	//---------------------------------
	
	Minim minim;
	AudioPlayer sfx_shield;
	AudioSample sfx_shieldInv;
	AudioSample sfx_absorb;
	AudioSample sfx_get;
	AudioSample sfx_hit_lo;
	AudioSample sfx_hit_mid;
	AudioSample sfx_hit_tre;
	
	AudioPlayer sfx_engine;
	
	AudioPlayer theme;
	
	float shieldGain = -7f;
	
	boolean isPlaying_Shield = false;
	boolean isLooping_Shield = false;
	boolean isPlaying_Hit = false;
	boolean isPlaying_Absorb = false;
	boolean sfx_triggerHit = false;
	boolean sfx_triggerAbsorb = false;

	public void setup() {
		
		
		// ***World Setup
		//----------------------------------
		size (displayWidth, displayHeight);
		noStroke();
		ellipseMode(CENTER);
		
		MidiBus.list();
		
		nanoKontrol = new MidiBus(this, "nanoKONTROL", "nanoKONTROL");
		
		
		//load the different images for win and loss here
		//imageWin = loadImage("imageWin.png");
		//imageLoss = loagImage("imageLoss.png");
		
		// ***BG Setup
		//----------------------------------
		for (int s = 0; s < 10000; s++) {
			PVector sP = new PVector((random(10*width))-5f*width, (random(10*height))-5f*width);
			stars.add(sP);
		}
		
		
		for (int i = 0; i < totalNovas; i++){
			novas[i] = new Star();
		}
		
		for(int j = 0; j < totalVortexes; j++){
			v[j] = new Vortex((int)(random(-width*2, width*2)), (int)(random(-height*2, height*2)), 150, 150, 140, random(0.1f, 0.3f), this);
		}
		
		
		
		// ***AudioContext
		//----------------------------------
		
		
		minim = new Minim(this);
		
		theme = minim.loadFile("data/StarSquare_Mono.wav");
		
		sfx_engine = minim.loadFile("data/Engine_1.wav");
		sfx_engine.setGain(-5f);
		sfx_engine.loop();
		
		sfx_absorb = minim.loadSample("data/Absorb_1.wav"); //Absorb is when the shield deflects the missiles
		sfx_absorb.setGain(-7f);
		
		sfx_get = minim.loadSample("data/Get_1.wav"); //Get is when the ship gets bonuses
		
		sfx_hit_lo = minim.loadSample("data/Hit_1_Lo.wav"); //Hit is when the ship is hit
		sfx_hit_lo.setGain(-7f);
		sfx_hit_mid = minim.loadSample("data/Hit_1_Mid.wav");
		sfx_hit_mid.setGain(-7f);
		sfx_hit_tre = minim.loadSample("data/Hit_1_Tre.wav");
		sfx_hit_tre.setGain(-7f);
		
		sfx_shield = minim.loadFile("data/Shield_Pad_2.wav"); //Shield is when the shield is increased/decreased
		
		
		sfx_shieldInv = minim.loadSample("data/Shield_Pad_Inverted.wav");
		
		sfx_engine.mute();
		theme.play();
		theme.loop();
		
		
		
		
		// ***Camera Setup
		//----------------------------------
		
		meR = 255;
		meG = 0;
		meB = 0;
		
		
		
		// ***Me Setup
		//----------------------------------
		meStartX = width/2;
		meStartY = height/2;
		
		mePos = new PVector(meStartX, meStartY);
		meDir = new PVector(menowX, menowY);
		
		meRad = 75;
		
		health = 100.0f;
		healthBar = 100.0f;
		scoreBar = 0.0f;
		
		esq = loadImage("esquisitice.png");
		tri = loadImage("color.png");
		
		
		// ***Bullet Setup
		//----------------------------------
		for (int i = 0; i < totalBullets; i++){
			bullets[i] = new Bullet();
			bullets[i].render();
		}
		
				
		// ***Juice Setup
		//----------------------------------
		for (int jCount = 0; jCount < 500; jCount++){
			jDOTx[jCount] = random (500);
			jDOTy[jCount] = random(50);
		}
				
		for (int jI = 0; jI < 100; jI++){
			jX[jI] = 250;
			jY[jI] = 250;
			jA[jI] = jRand2;
			jB[jI] = jRand3;
		}
		
		  
	}
	
	public void keyPressed(){
		if (key == ENTER || key == RETURN){
			keyEnter = true;
		}
	}
	
	public boolean sketchFullScreen() {
		  return true;
		}
	
	public void draw(){
		
		if (keyEnter){
			inGame = true;
			endGame = false;
			preGame = false;
		}
		
		if (End){
			preGame = false;
			inGame = false;
			endGame = true;
		}
		
		if (endGame){
			imageMode(CORNER);
			if(health <= 0) image = imageLoss;
			if(scoreBar >= 100.f) image = imageWin;
			image.resize(displayWidth, displayHeight);
			image(image,0,0);
			sfx_engine.mute();
			
		}
		
		
		if (preGame){
			preGame = true;
			inGame = false;
			background(255);
			image = loadImage("MENU.png");
			image.resize(displayWidth, displayHeight);
			image(image,0,0);
			sfx_engine.mute();
		}
	
		if (inGame){
		// ***BG
		//----------------------------------
		background(0);
		noStroke();
		fill(255);
		
		for (int i = 0; i < stars.size(); i++){
			int displace = 800;
			PVector sP = stars.get(i);
			PVector sM = new PVector((mePos.x-meDir.x)/displace, (mePos.y-meDir.y)/displace);
			
			if (mePos.x > (width/2+150)){
				sP.x -= sM.x;
				displace --;
			} else if (mePos.x < (width/2-150)){
				sP.x += sM.x;
				displace ++;
			}
			
			if (mePos.y < (height/2 - 100)){
				sP.y += sM.y;
				displace  --;
			}else if (mePos.y > (height/2 + 100)){
				sP.y -= sM.y;
				displace++;
			}
			
			float d = dist(sP.x, sP.y, width/2, height/2);
			d = map(d, 0, width/2, 0, 3);
			ellipse(sP.x, sP.y, d, d);
			//ellipse(sP.x+randStar, sP.y+randStar, d+2, d+2);
		}
		if (bri < 0){
			bri = 0;
		} else if (bri > 255) {
			bri = 255;
			bridir = random(-1, -3);
		}
		
		for (int i = 0; i < totalNovas; i++){
			
			novas[i].display();
			
			PVector sM = new PVector((mePos.x-meDir.x)/800, (mePos.y-meDir.y)/800);
			
			if (mePos.x > width/2){
				novas[i].origX -= sM.x;
			} else if (mePos.x < width/2){
				novas[i].origX += sM.x;
			}
			
			if (mePos.y < height/2){
				novas[i].origY += sM.y;
			}else if (mePos.y > height/2){
				novas[i].origY -= sM.y;
			}
		}
		
		for(int i = 0; i < totalVortexes; i++){
			v[i].display();
		}

		
		
		// ***DEBUG
		//----------------------------------
		for (int i = 0; i < totalBullets; i++){
		pushMatrix();
		translate(mePos.x, mePos.y);
			testPos[i] = new PVector(bullets[i].bX-mePos.x, bullets[i].bY-mePos.y);
			//stroke(0, 0, 255, 255);
			//strokeWeight(100);
			//point(testPos.x, testPos.y);
		popMatrix();
		}
		
		
		
		// ***Me
		//----------------------------------
		meDir = new PVector(menowX, menowY);
		meDir.sub(mePos);
		//meDir.normalize();
		PVector move = PVector.mult(meDir, meSpeed);
		mePos.add(new PVector(aSpeedX, aSpeedY));
		
		if(mePos.x <= -300) mePos.x += 5;
		if(mePos.x >= width+300) mePos.x -= 5;
		if(mePos.y <= 140) mePos.y += 5;
		if(mePos.y >= height-140) mePos.y -= 5;

		
		
		
		// ***Enemy
		//----------------------------------
		//CHECK COLLISION
		for (int i = 0; i < totalBullets; i++){
		if (dist(mePos.x, mePos.y, bullets[i].bX, bullets[i].bY) < bullets[i].bR/2 + meRad){
			colliding = true;
			if (entered){
				ouch = true;
				bullets[i].isActive = false;
				sfx_triggerHit = true;
				if (health > 0){
					health -= 5.0f;
					healthBar -= 5.0f;
				} else if (health <= 0){
					End = true;
					health = 0.0f;
					healthBar = 0.0f;
				}
				
			} else {
				ouch = true;
				println("NOTHURT");
				sfx_triggerHit = false;
			}
		} else {
			colliding = false;
			ouch = false;
		}
		}
		
		//MAKE BULLET
		for (int i = 0; i < totalBullets; i++){
			bullets[i].make();
			//bullets[i].move();
		//MOVE BULLET
		//bull.bX +=0.01f;
		}
		
		//CHECK BULLET
		for (int i = 0; i < totalBullets; i++){
			if (bullets[i].isActive == false){
				bullets[i] = new Bullet();
			}
		}
		
		// ***Shield
		//----------------------------------
		//CHECK SHIELD COLLISION
		int offsetX = 15;
		int offsetY = 40;
		fill(255);
		textSize(10);
		text("H E A L T H", mePos.x+103-offsetX, mePos.y-34+offsetY);
		text("P O I N T S", mePos.x+148-offsetX, mePos.y+2+offsetY);
		noFill();
		strokeWeight(1);
		stroke(255,255,255,100);
		rect (mePos.x+100-offsetX,mePos.y-30+offsetY, 105, 20);
		rect(mePos.x+102-offsetX, mePos.y-20+offsetY, scoreBar, 5);
		
		noStroke();
		fill (255,255,255,100);
		rect(mePos.x+102-offsetX, mePos.y-27+offsetY, healthBar, 5);
		
		fill(colCheck,50);
		ellipse(mePos.x, mePos.y, shieldRad, shieldRad);
		
		for (int i = 0; i < totalBullets; i++){
		if (dist(mePos.x, mePos.y, bullets[i].bX, bullets[i].bY) < bullets[i].bR + shieldRad/2){
			//IF ENEMY IS HITTING THE SHIELD OR NOT
			fill(255);
			
			//
			//DEBUGGING THE ANGLE INCOMING OF EACH BULLET:
			text(degrees(angleIncoming[i]), mePos.x+100,mePos.y+100);
			//
			
//			if ((degrees(angleIncoming[i])) >= shieldAngle)
			if (bullets[i].bY > mePos.y && (degrees(angleIncoming[i])) <= shieldAngle-angleOfShape){
				colCheck = 255;
				println("IS HITTING BOTTOM SHIELD");
				
				//IF SHIELD COLOUR MATCHES ENEMY COLOUR
				if ((bullets[i].bRed - errorMargin) > aRed1 || aRed1 < (bullets[i].bRed + errorMargin)) {
					if ((bullets[i].bGreen - errorMargin) > aGreen1 || aGreen1 < (bullets[i].bGreen + errorMargin)){
						if ((bullets[i].bBlue - errorMargin) > aBlue1 || aBlue1 < (bullets[i].bBlue + errorMargin)){
							
							//IF SHIELD STRENGTH MATCHES ENEMY SIZE
							//bullet radius 5 ~ 100 -- shield strength 1 - 21
							if (bullets[i].bLevel <= shieldL){
								//bullets[i].bR < shieldStrength*5){
								//println("BLOCKED");
								sfx_absorb.trigger();
								
								if (scoreBar < 100.0f){
									scoreBar += 10.0f;
								} else if (scoreBar >= 100.0f) {
									scoreBar = 100.0f;
									End = true;
								}
								
								bullets[i].isActive = false;
								entered = false;
								ouch = false;
							} else {
								//println("not strong enough");
								colliding = true;
								entered = true;
								ouch = false;
								sfx_triggerAbsorb = false;
							}
							
							
						} else{
							//println("blue is not right");
							colliding = true;
							entered = true;
							ouch = false;
						}
					} else {
						//println("green is not right");
						colliding = true;
						entered = true;
						ouch = false;
					}
				} else {
					//println("red is not right");
					colliding = true;
					entered = true;
					ouch = false;
				}
				
			}
			
			if (bullets[i].bY < mePos.y && (degrees(angleIncoming[i]))-180 <= shieldAngle-angleOfShape) {
				colCheck = 255;
				println("IS HITTING TOP SHIELD");
				
				//IF SHIELD COLOUR MATCHES ENEMY COLOUR
				if ((bullets[i].bRed - errorMargin) > aRed1 || aRed1 < (bullets[i].bRed + errorMargin)) {
					if ((bullets[i].bGreen - errorMargin) > aGreen1 || aGreen1 < (bullets[i].bGreen + errorMargin)){
						if ((bullets[i].bBlue - errorMargin) > aBlue1 || aBlue1 < (bullets[i].bBlue + errorMargin)){
							
							//IF SHIELD STRENGTH MATCHES ENEMY SIZE
							//bullet radius 5 ~ 100 -- shield strength 1 - 21
							if (bullets[i].bLevel <= shieldL){
								//bullets[i].bR < shieldStrength*5){
								//println("BLOCKED");
								sfx_absorb.trigger();
								
								if (scoreBar < 100.0f){
									scoreBar += 10.0f;
								} else if (scoreBar >= 100.0f) {
									scoreBar = 100.0f;
									End = true;
								}
								
								bullets[i].isActive = false;
								entered = false;
								ouch = false;
							} else {
								//println("not strong enough");
								colliding = true;
								entered = true;
								ouch = false;
								sfx_triggerAbsorb = false;
							}
							
							
						} else{
							//println("blue is not right");
							colliding = true;
							entered = true;
							ouch = false;
						}
					} else {
						//println("green is not right");
						colliding = true;
						entered = true;
						ouch = false;
					}
				} else {
					//println("red is not right");
					colliding = true;
					entered = true;
					ouch = false;
				}
			}
			else {
			//println("ENTERED SHIELD AREA");
			colliding = true;
			ouch = false;
			entered = true;
			colCheck = 10;
			}
		} else {
			ouch = false;
			colCheck = 0;	
		}
		}
		
		// ***Audio Booleans
		// ---------------------------------
		
		if(sfx_triggerHit){
			if(!isPlaying_Hit){
				sfx_hit_mid.trigger();
				isPlaying_Hit = true;
			}else{
				sfx_triggerHit = false;
			}
		}else if(!sfx_triggerHit){
			isPlaying_Hit = false;
		}
		
		if(sfx_triggerAbsorb){
			if(!isPlaying_Absorb){
				sfx_absorb.trigger();
			isPlaying_Absorb = true;
			}else{
				sfx_triggerAbsorb = false;
			}
		}else if(!sfx_triggerAbsorb){
			isPlaying_Absorb = false;
		}
		
		if(sfx_triggerAbsorb) println("-----------------------------------trig: "+sfx_triggerAbsorb+"\n isPlay: "+isPlaying_Absorb);
		
		//menowX = mouseX;
		//menowY = mouseY;
		
		
		//Checking when the player is moving
		PVector isMoving = PVector.sub(mePos, meDir);
		//println(move);
		if(abs(move.x) > 2.3f && abs(move.x) < 2.5f && abs(move.y) > 1.1f && abs(move.y) < 1.3f){
			sfx_engine.mute();
		}else {
			sfx_engine.unmute();			
		}
		
		
		// MAKE MEEEE
		angleOfShip += aRotateShip;
		noStroke();
		pushMatrix();
		translate(mePos.x, mePos.y);
		if (ouch){
			tint(255, 0, 0);
		} else if (!ouch){
			noTint();
		}
		rotate(PApplet.radians(angleOfShip));
		noTint();
		imageMode(CENTER);
		image(esq, 0, 45);
		imageMode(CENTER);

		image(tri, 0, 0);
		popMatrix();
		
		
		
		// MAKE SHIELD
		noFill();
		
//		aRed1 = 255;
//		aGreen1 = 100;
//		aBlue1 = 0;
//		aDist1 = PI/2;
		
		
		pushMatrix();
		translate(mePos.x, mePos.y);
			rotate(PApplet.radians(angleOfShape));
			
			shieldStrength = 21-((aDist1/PI)*20);
			strokeWeight(shieldStrength);
			stroke(aRed1, aGreen1, aBlue1, aAlpha1);
			arc(0, 0, 200.0f, 200.0f, PI-aDist1, PI+aDist1);
			
			if (shieldStrength >= 0 && shieldStrength < 5){
				shieldL = 1;
			} else if (shieldStrength < 10){
				shieldL = 2;
			} else if (shieldStrength < 15){
				shieldL = 3;
			} else if (shieldStrength < 22){
				shieldL = 4;
			}
			for (int i=0; i < totalBullets; i++){
				angleIncoming[i] = PVector.angleBetween(new PVector(testPos[i].x, testPos[i].y), new PVector(-50, 0));
			}
		popMatrix();
		
		//ANGLE TEST
		for (int i=0; i<totalBullets; i++){
//		stroke(255, 0, 0, 255);
//		strokeWeight(2);
//		
//		line(mePos.x, mePos.y, testPos[i].x+mePos.x, testPos[i].y+mePos.y); //line from the origin to the bullet
//		stroke(0, 255, 0, 255);
//		line(mePos.x, mePos.y, mePos.x-50, mePos.y+0); //line from the origin to the reference
		
		//bulletAngle = (degrees(angleIncoming[i]));
		shieldAngle = degrees(PI-aDist1);
		}
		
		//println("bulletAngle = " +bulletAngle);
//		println("shieldAngle = " +shieldAngle);
//		println (shieldStrength);
		
		
		//PUT ROTATION INFO HERE
		angleOfShape += aRotSpeed1;
		if (angleOfShape >= 360){
			angleOfShape = 0;
		}
		
		/*
		strokeWeight(aWeight2);
		stroke(aRed2, aGreen2, aBlue2, aAlpha2);
		arc(mePos.x, mePos.y, 200.0f-aDist2, 200.0f-aDist2, (2*PI)/3, (4*PI)/3);
		strokeWeight(aWeight3);
		stroke(aRed3, aGreen3, aBlue3, aAlpha3);
		arc(mePos.x, mePos.y, 200.0f-aDist3, 200.0f-aDist3, (4*PI)/3, 2*PI);
		*/
		
		
		
		
		
		// ***Juice
		//-----------------------------
		if (ouch == true){
	  		jRed = 255;
	  		jGreen = 0;
	  		jBlue = 0;
	  	} else if (ouch == false){
	  		jRed = random (100, 200);
			jGreen = random (100, 200);
			jBlue = random (100, 200);
	  	}
						
						
		if (colliding == true) {
							
			for (int i = 0; i < 100; i++) {
				jX[i]= jX[i] + ((mePos.x-jX[i]) / (5.0f + (i*5.0f) ));
				jY[i]= jY[i] + ((mePos.y-jY[i]) / (5.0f + (i*5.0f) ));
				}
						 
			int newDot = 0;
			int arms=7;
			jCenterX += ((jCenterX)/60.0f);
			jCenterY += ((mePos.y-jCenterY)/20.0f);
			
			while (newDot<arms) {
				pushMatrix();
				translate(mePos.x, mePos.y);
				rotate((TWO_PI/arms)*newDot);
			  	translate(-jCenterX, -jCenterY);
						 
			  	for (int i = 0; i < 100; i++) {
			  		fill (jRed, jGreen, jBlue);
					noStroke();
		    		ellipse (jX[i]/4, jY[i]/4, jRad, jRad);
		    		ellipse (mePos.x-jA[i]/3, mePos.y-jB[i]/3, 5,5);
			  	}
			  	popMatrix();
						 
			  	newDot++;
			}
						 
			//ellipse(jCenterX, jCenterY, jRad, jRad);
		}
						   
				else {
						 
				   float t = 5;
				   for (int i = 0; i < 100; i++) {
					   jX[i]= jX[i] + ((mePos.x-jX[i]) / (5.0f + (i*5.0f) ));
					   jX[i]= jY[i] + ((mePos.y-jY[i]) / (5.0f + (i*5.0f) ));
				   }
						 
				   int newDot = 0;
				   int arms=7;
				   jCenterX=((mePos.x-centerX)/100.0f);
				   jCenterY=((mePos.y-centerY)/100.0f);
						 
				   while (newDot<arms) {
					   pushMatrix();
					   translate(mePos.x, mePos.y);
					   rotate((TWO_PI/arms)*newDot);
					   translate(-jCenterX, -jCenterY);
						 
					   for (int i = 0; i < 100; i++) {
					   		fill (jRed, jGreen, jBlue);
					   		noStroke();
						   fill(jRed, jGreen, jBlue, t);
						   ellipse (jX[i]/3, jY[i]/3, jRad, jRad);
					   }
						 
					   popMatrix();
						 
					   newDot++;
				   }
						    
				   //fill(jRed, jGreen, jBlue, t);
				   //ellipse(mePos.x, mePos.y, r, r);
				   
				}
		}
	}
	
	//------------------THIS IS THE FUNCTION GETTING CHANGES IN VALUES FROM THE MIDI CONTROLLER
	public void controllerChange(int channel, int number, int value){
		
		/*
		println("+------+");
		println("Channel: "+channel);
		println("Number: "+number);
		println("Value: "+value);
		*/
		
		//-------------------FIRST ARC
		if(number==33){//first fader - RED
			aRed1 = map(value, 0, 127, 0, 255);
			shieldKnobInput = false;
		}else if(number == 34){//second fader - GREEN
			aGreen1 = map(value, 0, 127, 0, 255);
			shieldKnobInput = false;
		}else if(number == 35){//third fader - BLUE
			aBlue1 = map(value, 0, 127, 0, 255);
			shieldKnobInput = false;
		}else if(number==17){//first knob
			aRotSpeed1 = map(value, 127, 0, -9.0f, 9.0f); 
			shieldKnobInput = false;
		}else if(number==18){//second knob
			aAlpha1 = map(value, 127, 0, 100, 255);
			shieldKnobInput = false;
		}
		if(number == 19){// third knob - SHIELD
			paDist1 = aDist1;
			aDist1 = map(value, 0, 127, PI, 0f);
			shieldKnobInput = true;
		}
		if(number == 22){//fourth knob - POS X
			aSpeedX = map(value, 127, 0, -15, 15);		
		}
		if(number == 23){//fifth knob - POS Y
			//aRotateShip = map(value, 127, 0, -7, 7);	
		}
		if(number == 24){
			aSpeedY = map(value, 0, 127, -15, 10);	
		}

	}
	
	

	public void mousePressed (){
		jCenterX=mePos.x;
		jCenterY=mePos.y;
	}
	
	public void waitFor (int waitTime){
		totalTime = waitTime;
		savedTime = millis();
		
		passedTime = millis() - savedTime;
		if (passedTime > totalTime){
			println(waitTime/1000 + " secs passed");
			savedTime = millis();
		}
	}
	
//CLASSES

	//Bullet Class
	//Bullet Class
		class Bullet {
			
			float bX = random(-200, width+200);
			float bY;
			
			// the Bullet Radius
			float bR = random(5,100);
			// the Bullet Color
			float bRed = random(255);
			float bGreen = random(255);
			float bBlue = random(255);
			
			PVector bulletPos;
			PVector bDir = new PVector(random(width/2-500,width/2+500), random(height/2-300, height/2+300));
			//float bSpeed;
			
			boolean isActive = true;
			
			float dirX;
			float dirY;
			float speed;
			
			float t = 3;
			float velocity = 0.001f;
			
			//move variables
			float fluxX = random(1,8);
			float fluxY = random(5*t, 10*t);
			float fluxW = random(0,8);
			float fluxH = random(0.1f*t, 0.5f*t);
			
			float dirRand1x = random(-1,1);
			float dirRand1y = random(-1,1);
			float dirRand2x = random(-2,2);
			float dirRand2y = random(-2,2);
			float fluxRand1 = random(50,250);
			float fluxRand2 = random(50,200);
			float speedRand1 = random(0.5f, 1.0f);
			float speedRand2 = random(0.5f, 1.0f);
			
			PVector move;
			
			int bLevel = 0;
			
			
			
			/*Bullet(float xpos, float ypos, float radius, int r, int g, int b, 
					float dirX, float dirY, float speed){
				bX = xpos;
				bY = ypos;
				bR = radius;
				bRed = r;
				bGreen = g;
				bBlue = b;
				
				bulletPos = new PVector(bX, bY);
				bDir = new PVector(dirX, dirY);
				bSpeed = speed;
			}*/
			
			Bullet(){
				
				
			}
			void render(){
				
				if (bX <= 0 || bX > width){
					bY = random(height);
				} else if (bX > 0 && bX <= width){
					int y = (int)random(2);
					switch(y) {
					case 0:
						bY = -150;
						break;
					case 1:
						bY = height+150;
						break;
					}
				}
			}
			
			void make() {
				
				bulletPos = new PVector(bX, bY);
//				bDir = new PVector(random(mePos.x-100,mePos.x+100), random(mePos.y-100, mePos.y+100));
				
				if (bR >= 10 && bR < 20){
					bX -= (bX-bDir.x)*0.01f;
					bY -= (bY-bDir.y)*0.01f;
					bLevel = 1;
				} else if (bR >= 20 && bR < 45){
					bX = fluxRand1*cos(fluxX*t+fluxW+30)+width/2;
				    bY = fluxRand2*sin(fluxY*t+fluxH-30)+height/2;
				    bLevel = 2;
				} else if (bR >=45 && bR < 75){
					bX -= (bX-bDir.x)*0.005f;
					bY -= (bY-bDir.y)*0.005f;
					bLevel = 3;
				} else if (bR >= 75){
					bX -= (bX-bDir.x)*0.003f;
					bY -= (bY-bDir.y)*0.003f;
					bLevel = 4;
				}
				
				t += velocity;
				
				fill(bRed, bGreen, bBlue, 150);
				noStroke();
				ellipse (bX, bY, bR+(bR/2), bR+(bR/2));
				stroke(bRed, bGreen, bBlue);
				strokeWeight(1);
				ellipse (bX, bY, bR*2, bR*2);
				
				
				//println(move);
				//println(bDir);
				//println(speed);
				
//					fill(bRed, bGreen, bBlue, 100);
//					noStroke();
//					ellipse (bX, bY, bR, bR);
//					ellipse (bX, bY, bR*2, bR*2);
			}
			
		}
	
	class Star {
	  float origX = random(2*width)-width;
	  float origY = random(2*height)-height;
	  float v = random(0.001f, 0.005f);
	  float value = random(0,5);
	  float x1 = 0;
	  float y1 = 0;
	  float x2 = 5;
	  float y2 = 0;

	  float radius;
	  float angle;

	  Star() {
	  }

	  void display() {
	    pushMatrix();
	    translate(origX, origY);
	    strokeWeight(1);
	    stroke(255, 255, 255, 50+noise(sin(value))*100);


	    rotate(QUARTER_PI+angle);
	    line(x1, y1, x2, y2);
	    rotate(QUARTER_PI);
	    line(x1, y1, x2, y2);
	    rotate(QUARTER_PI);
	    line(x1, y1, x2, y2);
	    rotate(QUARTER_PI);
	    line(x1, y1, x2, y2);
	    rotate(QUARTER_PI);
	    line(x1, y1, x2, y2);
	    rotate(QUARTER_PI);
	    line(x1, y1, x2, y2);
	    rotate(QUARTER_PI);
	    line(x1, y1, x2, y2);
	    rotate(QUARTER_PI);
	    line(x1, y1, x2, y2);
	    noFill();
	    for (int i =1; i < 30; i++) {
	      //ellipse(0, 0, (radius*radius)/i, (radius*radius)/i);
	    }/*
	  	ellipse(0, 0, (radius), (1/(radius+1))*1);
	    rotate(QUARTER_PI);
	    ellipse(0, 0, (radius), (1/(radius+1))*1);
	    rotate(QUARTER_PI);
	  	ellipse(0, 0, (radius), (1/(radius+1))*1);

	    rotate(QUARTER_PI);
	   	ellipse(0, 0, (radius), (1/(radius+1))*1);*/
	    
	    x2+=sin(value)/15;
	    y1+=sin(value)/15;

	    angle += sin(value)/10;
	    radius += sin(value)/2;

	    if (x2 > 10) x2--;
	    if (y1 > 0) y1--;
	    
	    if (angle > 0) angle -=0.01;


	    value+=0.005;
	    popMatrix();
	  }
	}
	
	//CLASS VORTEX
	class Vortex {

		  float rad = 0.1f;
		  float count = 0;
		  float x;
		  float y;
		  float r;
		  float g;
		  float b;
		  float radRate;
		  PApplet pApplet;

		  Vortex(int x, int y, int r, int g, int b, float radRate, PApplet pApplet) {
		    this.x = x;
		    this.y = y;
		    this.r = r;
		    this.g = g;
		    this.b = b;
		    this.radRate = radRate;
		    this.pApplet = pApplet;
		  }

		  void display() {
			pushMatrix();
		    translate(x, y);
		    noStroke();
		    for (int i = 1; i < 21; i++) {
			      pApplet.fill((float)(r+noise(i)*(i*0.8)), (float)(g+noise(i)*(i*0.8)), (float)(b+noise(i)*(i*0.8)), noise(i)*5);
			      ellipse(0, 0, rad*(i/2), rad+random(50)+(sin(count)));
			    } 

			    if (rad > 1){
			      rad = 0;
			      count++;
			    }

			    if (rad < -(abs(radRate)*200)) {
				      radRate *= -1;
				    }
				    rad -= radRate;
				    popMatrix();
				  }
				}

	
	public static void main(String _args[]) {
		PApplet.main(new String[] { blockr.Blockr.class.getName() });
	}
}