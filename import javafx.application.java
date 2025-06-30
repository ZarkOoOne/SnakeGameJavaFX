import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SnakeGame extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private Snake snake;
    private GameBoard gameBoard;
    private int lives = 3;
    private Label livesLabel;
    private boolean gameOver = false;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        
        gameBoard = new GameBoard(WIDTH, HEIGHT);
        snake = new Snake();
        
        livesLabel = new Label("Ζωές: " + lives);
        livesLabel.setFont(new Font(20));
        livesLabel.setTextFill(Color.WHITE);
        
        root.getChildren().addAll(gameBoard, snake.getBody(), livesLabel);
        
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:    snake.setDirection(0, -1); break;
                case DOWN:  snake.setDirection(0, 1); break;
                case LEFT:  snake.setDirection(-1, 0); break;
                case RIGHT: snake.setDirection(1, 0); break;
                case ENTER: 
                    if (gameOver) restartGame();
                    break;
            }
        });
        
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) {
                    update();
                }
            }
        }.start();
        
        primaryStage.setTitle("Φιδάκι");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void update() {
        snake.move();
        checkCollisions();
        gameBoard.update();
    }
    
    private void checkCollisions() {
        if (snake.checkWallCollision(WIDTH, HEIGHT) || 
            gameBoard.checkObstacleCollision(snake.getHead())) {
            lives--;
            livesLabel.setText("Ζωές: " + lives);
            
            if (lives <= 0) {
                gameOver = true;
                showGameOver();
            } else {
                resetSnake();
            }
        }
    }
    
    private void resetSnake() {
        snake.reset();
    }
    
    private void restartGame() {
        lives = 3;
        livesLabel.setText("Ζωές: " + lives);
        gameOver = false;
        resetSnake();
        gameBoard.reset();
    }
    
    private void showGameOver() {
        Label gameOverLabel = new Label("Game Over!\nΠατήστε ENTER για νέο παιχνίδι");
        gameOverLabel.setFont(new Font(30));
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setLayoutX(WIDTH/2 - 150);
        gameOverLabel.setLayoutY(HEIGHT/2 - 50);
        ((Pane)livesLabel.getParent()).getChildren().add(gameOverLabel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}