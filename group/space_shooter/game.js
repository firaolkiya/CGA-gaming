// Select canvas and context
const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// Game variables
let spaceshipWidth = 50;
let spaceshipHeight = 30;
let spaceshipX = canvas.width / 2 - spaceshipWidth / 2;
let spaceshipY = canvas.height - spaceshipHeight - 10;
let spaceshipSpeed = 7;
let bullets = [];
let enemies = [];
let enemySpeed = 3;
let score = 0;
let gameOver = false;

// Handle key presses for movement
let leftPressed = false;
let rightPressed = false;
let spacePressed = false;

// Spaceship object
let spaceship = {
    x: spaceshipX,
    y: spaceshipY,
    width: spaceshipWidth,
    height: spaceshipHeight,
    color: 'cyan'
};

// Bullet object
function Bullet(x, y) {
    this.x = x;
    this.y = y;
    this.width = 5;
    this.height = 10;
    this.color = 'red';
    this.speed = 5;
}

// Enemy object
function Enemy(x, y) {
    this.x = x;
    this.y = y;
    this.width = 40;
    this.height = 40;
    this.color = 'yellow';
    this.speed = enemySpeed;
}

// Keydown event listeners
document.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowLeft' || e.key === 'a') {
        leftPressed = true;
    }
    if (e.key === 'ArrowRight' || e.key === 'd') {
        rightPressed = true;
    }
    if (e.key === ' ' && !spacePressed) {
        spacePressed = true;
        createBullet();
    }
});

// Keyup event listeners
document.addEventListener('keyup', (e) => {
    if (e.key === 'ArrowLeft' || e.key === 'a') {
        leftPressed = false;
    }
    if (e.key === 'ArrowRight' || e.key === 'd') {
        rightPressed = false;
    }
    if (e.key === ' ') {
        spacePressed = false;
    }
});

// Create bullet
function createBullet() {
    let bullet = new Bullet(spaceship.x + spaceship.width / 2 - 2, spaceship.y);
    bullets.push(bullet);
}

// Create enemy
function createEnemy() {
    let x = Math.random() * (canvas.width - 40);
    let y = -40;
    let enemy = new Enemy(x, y);
    enemies.push(enemy);
}

// Update spaceship position
function updateSpaceship() {
    if (leftPressed && spaceship.x > 0) {
        spaceship.x -= spaceshipSpeed;
    }
    if (rightPressed && spaceship.x < canvas.width - spaceship.width) {
        spaceship.x += spaceshipSpeed;
    }
}

// Update bullets and check collision with enemies
function updateBullets() {
    bullets.forEach((bullet, index) => {
        bullet.y -= bullet.speed;
        if (bullet.y < 0) {
            bullets.splice(index, 1);
        }

        // Check collision with enemies
        enemies.forEach((enemy, enemyIndex) => {
            if (bullet.x < enemy.x + enemy.width &&
                bullet.x + bullet.width > enemy.x &&
                bullet.y < enemy.y + enemy.height &&
                bullet.y + bullet.height > enemy.y) {
                // Bullet hits enemy
                bullets.splice(index, 1);
                enemies.splice(enemyIndex, 1);
                score += 10;
            }
        });
    });
}

// Update enemies
function updateEnemies() {
    enemies.forEach((enemy, index) => {
        enemy.y += enemy.speed;

        // Check if enemy hits spaceship
        if (enemy.x < spaceship.x + spaceship.width &&
            enemy.x + enemy.width > spaceship.x &&
            enemy.y < spaceship.y + spaceship.height &&
            enemy.y + enemy.height > spaceship.y) {
            gameOver = true;
        }

        // Remove enemy if out of screen
        if (enemy.y > canvas.height) {
            enemies.splice(index, 1);
        }
    });
}

// Draw everything on the canvas
function draw() {
    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    if (!gameOver) {
        // Draw spaceship
        ctx.fillStyle = spaceship.color;
        ctx.fillRect(spaceship.x, spaceship.y, spaceship.width, spaceship.height);

        // Draw bullets
        bullets.forEach(bullet => {
            ctx.fillStyle = bullet.color;
            ctx.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        });

        // Draw enemies
        enemies.forEach(enemy => {
            ctx.fillStyle = enemy.color;
            ctx.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
        });

        // Draw score
        document.getElementById('scoreboard').innerText = `Score: ${score}`;

        // Create new enemies
        if (Math.random() < 0.02) {
            createEnemy();
        }

        // Update game objects
        updateSpaceship();
        updateBullets();
        updateEnemies();
    } else {
        // Game Over screen
        ctx.fillStyle = 'red';
        ctx.font = '50px Arial';
        ctx.fillText('Game Over', canvas.width / 2 - 150, canvas.height / 2);
        ctx.font = '25px Arial';
        ctx.fillText('Refresh the page to play again', canvas.width / 2 - 150, canvas.height / 2 +50);
        document.getElementById('game-over').style.display = 'block';
        document.getElementById('instructions').style.display = 'none'; // Hide the instructions after game over
    }
}

// Display instructions at the start of the game
function showInstructions() {
    document.getElementById('instructions').style.display = 'block';
    document.getElementById('instructions').innerText = 'Press Space to Shoot.';
}

// Game loop
function gameLoop() {
    draw();
    requestAnimationFrame(gameLoop);
}

// Start the game loop
gameLoop();

// Show instructions at the beginning
showInstructions();
