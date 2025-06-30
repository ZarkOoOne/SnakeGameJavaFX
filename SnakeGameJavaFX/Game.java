package SnakeGameJavaFX;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int SNAKE_SIZE = 20;
    
    private List<Rectangle> snakeBody;
    private List<Circle> obstacles;
    private Random random;
    private int lives = 3;
    private boolean gameOver = false;
    private Text livesText;
    private Text gameOverText;
    
    private double currentDirection = 0; // σε μοίρες
    private static final double ROTATION_SPEED = 3.0;
    private static final double MOVEMENT_SPEED = 2.0;
    
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        random = new Random();
        
        // Αρχικοποίηση φιδιού
        snakeBody = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Rectangle segment = new Rectangle(
                WIDTH/2 - i * SNAKE_SIZE, 
                HEIGHT/2, 
                SNAKE_SIZE, 
                SNAKE_SIZE
            );
            segment.setFill(Color.GREEN);
            segment.setStroke(Color.DARKGREEN);
            snakeBody.add(segment);
            root.getChildren().add(segment);
        }
        
        // Αρχικοποίηση εμποδίων
        obstacles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            spawnObstacle(root);
        }
        
        // Κείμενο για ζωές
        livesText = new Text(10, 30, "Ζωές: " + lives);
        gameOverText = new Text(WIDTH/2 - 100, HEIGHT/2, "GAME OVER - Πατήστε SPACE για επανεκκίνηση");
        gameOverText.setFill(Color.RED);
        gameOverText.setVisible(false);
        
        root.getChildren().addAll(livesText, gameOverText);
        
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:  currentDirection -= ROTATION_SPEED; break;
                case RIGHT: currentDirection += ROTATION_SPEED; break;
                case SPACE: 
                    if (gameOver) {
                        resetGame(root);
                    }
                    break;
            }
        });
        
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) {
                    updateGame();
                    checkCollisions(root);
                }
            }
        };
        timer.start();
        
        primaryStage.setTitle("Φιδάκι");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void updateGame() {
        // Κίνηση φιδιού
        double dx = Math.cos(Math.toRadians(currentDirection)) * MOVEMENT_SPEED;
        double dy = Math.sin(Math.toRadians(currentDirection)) * MOVEMENT_SPEED;
        
        // Ενημέρωση θέσης κάθε τμήματος του φιδιού
        for (int i = snakeBody.size() - 1; i > 0; i--) {
            Rectangle current = snakeBody.get(i);
            Rectangle previous = snakeBody.get(i-1);
            current.setX(previous.getX());
            current.setY(previous.getY());
        }
        
        // Ενημέρωση κεφαλιού
        Rectangle head = snakeBody.get(0);
        head.setX(head.getX() + dx);
        head.setY(head.getY() + dy);
        
        // Έλεγχος ορίων οθόνης
        if (head.getX() < 0) head.setX(WIDTH);
        if (head.getX() > WIDTH) head.setX(0);
        if (head.getY() < 0) head.setY(HEIGHT);
        if (head.getY() > HEIGHT) head.setY(0);
        
        // Κίνηση εμποδίων
        for (Circle obstacle : obstacles) {
            obstacle.setCenterX(obstacle.getCenterX() + random.nextDouble() * 2 - 1);
            obstacle.setCenterY(obstacle.getCenterY() + random.nextDouble() * 2 - 1);
            
            // Διατήρηση εμποδίων εντός ορίων
            if (obstacle.getCenterX() < 0 || obstacle.getCenterX() > WIDTH ||
                obstacle.getCenterY() < 0 || obstacle.getCenterY() > HEIGHT) {
                obstacle.setCenterX(random.nextDouble() * WIDTH);
                obstacle.setCenterY(random.nextDouble() * HEIGHT);
            }
        }
    }
    
    private void checkCollisions(Pane root) {
        Rectangle head = snakeBody.get(0);
        
        for (Circle obstacle : obstacles) {
            if (head.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                lives--;
                livesText.setText("Ζωές: " + lives);
                
                if (lives <= 0) {
                    gameOver = true;
                    gameOverText.setVisible(true);
                } else {
                    resetSnakePosition();
                }
                break;
            }
        }
    }
    
    private void spawnObstacle(Pane root) {
        Circle obstacle = new Circle(
            random.nextDouble() * WIDTH,
            random.nextDouble() * HEIGHT,
            10,
            Color.RED
        );
        obstacles.add(obstacle);
        root.getChildren().add(obstacle);
    }
    
    private void resetSnakePosition() {
        for (int i = 0; i < snakeBody.size(); i++) {
            Rectangle segment = snakeBody.get(i);
            segment.setX(WIDTH/2 - i * SNAKE_SIZE);
            segment.setY(HEIGHT/2);
        }
        currentDirection = 0;
    }
    
    private void resetGame(Pane root) {
        lives = 3;
        gameOver = false;
        gameOverText.setVisible(false);
        livesText.setText("Ζωές: " + lives);
        resetSnakePosition();
        
        // Επαναφορά εμποδίων
        for (Circle obstacle : obstacles) {
            obstacle.setCenterX(random.nextDouble() * WIDTH);
            obstacle.setCenterY(random.nextDouble() * HEIGHT);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 