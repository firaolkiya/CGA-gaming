// Variables
var score = 0;
var platform;
var platform2;
var item;
var player;
var isJumping = false;
var gameOver = false;
var level = 1;
// Create Sprites
function setup() {
  createCanvas(400, 400);
  // Create the first platform sprite
  platform = createSprite(200, 100, 100, 20);
  platform.setAnimation("platform");
  platform.velocityY = 2;
  // Create the second platform sprite
  platform2 = createSprite(300, 50, 100, 20);
  platform2.setAnimation("platform");
  platform2.velocityY = 2;
  // Create the new item sprite
  item = createSprite(randomNumber(0, 400), randomNumber(0, 200), 20, 20);
  item.setAnimation("coin_bronze_1");
  item.velocityY = 3;
  // Create the player sprite
  player = createSprite(200, 350, 30, 30);
  player.setAnimation("alien");
  player.velocityY = 0;
}
function draw() {
  if (gameOver) {
    // Display "Game Over" message and stop the game
    background("black");
    textAlign(CENTER, CENTER);
    textSize(30);
    fill("red");
    text("Game Over", width / 2, height / 2);
    noLoop();
    return;
  }
  // Level logic
  if (score >= 250 && level === 1) {
    startLevelTwo();
  } else if (score >= 500 && level === 2) {
    startLevelThree();
  } else if (score >= 750 && level === 3) {
    startLevelFour();
  } else if (score >= 1000 && level === 4) {
    startLevelFive();
  } else if (score >= 1250 && level === 5) {
    startLevelSix();
  } else if (score >= 1500 && level === 6) {
    startLevelSeven();
  } else if (score >= 1750 && level === 7) {
    startLevelEight();
  } else if (score >= 2000 && level === 8) {
    startLevelNine();
  } else if (score >= 2250 && level === 9) {
    startLevelTen();
  }
  // Background based on level
  if (level === 1) {
    background1();
  } else if (level === 2) {
    background2();
  } else if (level === 3) {
    background3();
  } else if (level === 4) {
    background4();
  } else if (level === 5) {
    background5();
  } else if (level === 6) {
    background6();
  } else if (level === 7) {
    background7();
  } else if (level === 8) {
    background8();
  } else if (level === 9) {
    background9();
  } else if (level === 10) {
    background10();
  }
  // Draw the frame and show the score
  drawFrame();
  showScore();
  // Update the platforms and items
  loopPlatforms();
  loopItems();
  // Player mechanics
  playerFall();
  controlPlayer();
  // Handle collisions
  playerLands();
  collectItems();

  drawSprites();
}
// Backgrounds for levels
function background1() {
  background("skyblue");
  fill("yellow");
  ellipse(50, 50, 70, 70);
  fill("white");
  ellipse(100, 100, 60, 40);
  ellipse(300, 80, 80, 50);
  ellipse(200, 120, 100, 60);
}

function background2() {
  background("purple");
  fill("darkblue");
  ellipse(200, 350, 120, 120);
  fill("white");
  ellipse(randomNumber(0, 400), randomNumber(0, 200), 5, 5);
}

function background3() {
  background("black");
  fill("red");
  ellipse(randomNumber(50, 350), randomNumber(50, 350), 80, 80);
  fill("white");
  ellipse(randomNumber(0, 400), randomNumber(0, 200), 3, 3);
}
function background4() {
  background("green");
  fill("gold");
}

function background5() {
  background("orange");
  fill("pink");
  fill("blue");
  ellipse(randomNumber(0, 400), randomNumber(0, 200), 15, 15);
}
function background6() {
  background("lightblue");
  fill("purple");
  ellipse(randomNumber(0, 400), randomNumber(0, 200), 30, 30);
}

function background7() {
  background("yellow");
  fill("red");
  ellipse(randomNumber(0, 400), randomNumber(0, 200), 40, 40);
}

function background8() {
  background("darkgray");
  fill("white");
  ellipse(randomNumber(0, 400), randomNumber(0, 200), 10, 10);
}

function background9() {
  background("pink");
  fill("blue");
  ellipse(randomNumber(0, 400), randomNumber(0, 200), 25, 25);
}

function background10() {
  background("brown");
  fill("yellow");
  ellipse(randomNumber(0, 400), randomNumber(0, 200), 50, 50);
}

// Draw frame based on level
function drawFrame() {
  if (level === 1) {
    stroke("black");
    strokeWeight(4);
    noFill();
    rect(0, 0, width, height);
  } else if (level === 2) {
    stroke("blue");
    strokeWeight(6);
    noFill();
    rect(0, 0, width, height);
  } else if (level === 3) {
    stroke("red");
    strokeWeight(8);
    noFill();
  } else if (level === 4) {
    stroke("yellow");
    strokeWeight(10);
    noFill();
  } else if (level === 5) {
    stroke("white");
    strokeWeight(12);
    noFill();
    rect(0, 0, width, height);
  } else if (level === 6) {
    stroke("green");
    strokeWeight(14);
    noFill();
  } else if (level === 7) {
    stroke("pink");
    strokeWeight(16);
    noFill();
  } else if (level === 8) {
    stroke("purple");
    strokeWeight(18);
    noFill();
  } else if (level === 9) {
    stroke("blue");
    strokeWeight(20);
    noFill();
  } else if (level === 10) {
    stroke("gold");
    strokeWeight(22);
    noFill();
  }
}

// Show the score
function showScore() {
  fill("black");
  textSize(20);
  textAlign(LEFT, TOP);
  text("Score: " + score, 10, 10);
}

// Loop the platforms
function loopPlatforms() {
  if (platform.position.y > height) {
    platform.position.y = 0;
    platform.position.x = randomNumber(0, 400);
  }

  if (platform2.position.y > height) {
    platform2.position.y = 0;
    platform2.position.x = randomNumber(0, 400);
  }
}

// Loop the items
function loopItems() {
  if (item.position.y > height) {
    item.position.y = randomNumber(0, 200);
    item.position.x = randomNumber(0, 400);
  }
}

// Player fall mechanics
function playerFall() {
  player.velocityY += 0.5;
  if (player.position.y > height) {
    player.position.y = height;
    player.velocityY = 0;
  }
}

// Control the player
function controlPlayer() {
  if (keyDown("left")) {
    player.x -= 5;
  }
  if (keyDown("right")) {
    player.x += 5;
  }
  if (keyDown("up") && !isJumping) {
    player.velocityY = -10;
    isJumping = true;
  }
  if (player.position.y >= height) {
    isJumping = false;
  }
}

// Player landing mechanics
function playerLands() {
  if (player.collide(platform) || player.collide(platform2)) {
    player.velocityY = 0;
    isJumping = false;
    score -= 1;
    if (score < 0) {
      gameOver = true;
    }
  }
}
// Collect items
function collectItems() {
  if (player.isTouching(item)) {
    item.position.y = randomNumber(0, 200);
    item.position.x = randomNumber(0, 400);
    score += 50;
  }
}

// Start Level 2
function startLevelTwo() {
  level = 2;
  platform.velocityY = 4;
  platform2.velocityY = 4;
  item.velocityY = 5;
  console.log("Level 2 started!");
}
// Start Level 3
function startLevelThree() {
  level = 3;
  platform.velocityY = 6;
  platform2.velocityY = 6;
  item.velocityY = 7;
  console.log("Level 3 started!");
}
// Start Level 4
function startLevelFour() {
  level = 4;
  platform.velocityY = 8;
  platform2.velocityY = 8;
  item.velocityY = 9;
  console.log("Level 4 started!");
}
// Start Level 5
function startLevelFive() {
  level = 5;
  platform.velocityY = 10;
  platform2.velocityY = 10;
  item.velocityY = 11;
  console.log("Level 5 started!");
}
// Start Level 6
function startLevelSix() {
  level = 6;
  platform.velocityY = 12;
  platform2.velocityY = 12;
  item.velocityY = 13;
  console.log("Level 6 started!");
}
// Start Level 7
function startLevelSeven() {
  level = 7;
  platform.velocityY = 14;
  platform2.velocityY = 14;
  item.velocityY = 15;
  console.log("Level 7 started!");
}
// Start Level 8
function startLevelEight() {
  level = 8;
  platform.velocityY = 16;
  platform2.velocityY = 16;
  item.velocityY = 17;
  console.log("Level 8 started!");
}
// Start Level 9
function startLevelNine() {
  level = 9;
  platform.velocityY = 18;
  platform2.velocityY = 18;
  item.velocityY = 19;
  console.log("Level 9 started!");
}
// Start Level 10
function startLevelTen() {
  level = 10;
  platform.velocityY = 20;
  platform2.velocityY = 20;
  item.velocityY = 21;
  console.log("Level 10 started!");
}
// Utility function for random numbers
function randomNumber(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}
