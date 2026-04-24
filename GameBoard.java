import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Properties;
import java.util.Random;

/**
 * Игровое поле. Содержит всю логику игры Змейка.
 */
public class GameBoard extends JPanel implements ActionListener {
    
    // Константы игрового поля
    public static final int BOARD_SIZE = 20;      // Размер поля в клетках (20x20)
    public static final int UNIT_SIZE = 25;       // Размер одной клетки в пикселях
    
    // Компоненты игры
    private Snake snake;
    private int foodX, foodY;
    private int score;
    private int bestScore;
    private boolean gameOver;
    private Timer timer;
    private boolean waitingForNewGame;
    
    // Цвета для отрисовки
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0);
    private static final Color GRID_COLOR = new Color(35, 35, 35);
    private static final Color FOOD_COLOR = new Color(255, 50, 50);
    private static final Color SNAKE_HEAD_COLOR = new Color(50, 200, 50);
    private static final Color SNAKE_BODY_COLOR = new Color(0, 150, 0);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color OVERLAY_COLOR = new Color(0, 0, 0, 180);
    
    /**
     * Конструктор игрового поля.
     */
    public GameBoard() {
        setPreferredSize(new Dimension(BOARD_SIZE * UNIT_SIZE, BOARD_SIZE * UNIT_SIZE));
        setBackground(BACKGROUND_COLOR);
        setFocusable(true);
        
        loadBestScore();
        initGame();
        
        // Обработчик нажатий клавиш
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }
    
    /**
     * Обработка нажатий клавиш.
     */
    private void handleKeyPress(KeyEvent e) {
        // Если игра окончена и нажат пробел
        if (waitingForNewGame) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                startGame();
            }
            return;
        }
        
        // Управление змейкой
        if (!gameOver) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if (snake.getDirection() != Direction.DOWN) {
                        snake.setDirection(Direction.UP);
                    }
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if (snake.getDirection() != Direction.UP) {
                        snake.setDirection(Direction.DOWN);
                    }
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (snake.getDirection() != Direction.RIGHT) {
                        snake.setDirection(Direction.LEFT);
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if (snake.getDirection() != Direction.LEFT) {
                        snake.setDirection(Direction.RIGHT);
                    }
                    break;
            }
        }
    }
    
    /**
     * Инициализация игры (сброс состояния).
     */
    private void initGame() {
        snake = new Snake();
        score = 0;
        gameOver = false;
        waitingForNewGame = false;
        generateFood();
        updateScore();
    }
    
    /**
     * Запуск новой игры.
     */
    public void startGame() {
        initGame();
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(100, this);  // 100 мс = 10 FPS
        timer.start();
        repaint();
    }
    
    /**
     * Генерация новой еды в случайной пустой клетке.
     */
    private void generateFood() {
        Random random = new Random();
        do {
            foodX = random.nextInt(BOARD_SIZE);
            foodY = random.nextInt(BOARD_SIZE);
        } while (isFoodOnSnake(foodX, foodY));
    }
    
    /**
     * Проверка, находится ли еда на теле змейки.
     */
    private boolean isFoodOnSnake(int x, int y) {
        for (Point p : snake.getSegments()) {
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Движение змейки.
     */
    private void moveSnake() {
        Point head = snake.getSegments().get(0);
        Point newHead = new Point(head.x, head.y);
        
        switch (snake.getDirection()) {
            case UP:
                newHead.y--;
                break;
            case DOWN:
                newHead.y++;
                break;
            case LEFT:
                newHead.x--;
                break;
            case RIGHT:
                newHead.x++;
                break;
        }
        
        // Проверка поедания еды
        if (newHead.x == foodX && newHead.y == foodY) {
            snake.grow(newHead);
            score++;
            if (score > bestScore) {
                bestScore = score;
                saveBestScore();
            }
            generateFood();
            updateScore();
        } else {
            snake.move(newHead);
        }
    }
    
    /**
     * Проверка столкновений со стеной или с собой.
     * @return true, если произошло столкновение
     */
    private boolean checkCollision() {
        Point head = snake.getSegments().get(0);
        
        // Столкновение со стеной
        if (head.x < 0 || head.x >= BOARD_SIZE || head.y < 0 || head.y >= BOARD_SIZE) {
            return true;
        }
        
        // Столкновение с собственным телом
        for (int i = 1; i < snake.getSegments().size(); i++) {
            if (head.equals(snake.getSegments().get(i))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Обновление отображения счёта.
     */
    private void updateScore() {
        repaint();
    }
    
    /**
     * Загрузка лучшего рекорда из файла.
     */
    private void loadBestScore() {
        try {
            Properties props = new Properties();
            File file = new File(System.getProperty("user.home"), "snake_stats.properties");
            if (file.exists()) {
                props.load(new FileInputStream(file));
                String bestScoreStr = props.getProperty("bestScore", "0");
                bestScore = Integer.parseInt(bestScoreStr);
            } else {
                bestScore = 0;
            }
        } catch (Exception e) {
            bestScore = 0;
        }
    }
    
    /**
     * Сохранение лучшего рекорда в файл.
     */
    private void saveBestScore() {
        try {
            Properties props = new Properties();
            props.setProperty("bestScore", String.valueOf(bestScore));
            File file = new File(System.getProperty("user.home"), "snake_stats.properties");
            props.store(new FileOutputStream(file), "Snake Game Best Score");
        } catch (Exception e) {
            System.err.println("Failed to save best score: " + e.getMessage());
        }
    }
    
    /**
     * Обработка таймера (один шаг игры).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            moveSnake();
            if (checkCollision()) {
                gameOver = true;
                waitingForNewGame = true;
                timer.stop();
            }
        }
        repaint();
    }
    
    /**
     * Отрисовка игрового поля.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Включение сглаживания
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Отрисовка сетки
        g2d.setColor(GRID_COLOR);
        for (int i = 0; i <= BOARD_SIZE; i++) {
            g2d.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, getHeight());
            g2d.drawLine(0, i * UNIT_SIZE, getWidth(), i * UNIT_SIZE);
        }
        
        // Отрисовка еды
        g2d.setColor(FOOD_COLOR);
        g2d.fillOval(foodX * UNIT_SIZE + 2, foodY * UNIT_SIZE + 2, 
                     UNIT_SIZE - 4, UNIT_SIZE - 4);
        
        // Отрисовка змейки
        for (int i = 0; i < snake.getSegments().size(); i++) {
            Point p = snake.getSegments().get(i);
            int x = p.x * UNIT_SIZE;
            int y = p.y * UNIT_SIZE;
            int size = UNIT_SIZE - 2;
            
            if (i == 0) {
                // Голова змейки
                g2d.setColor(SNAKE_HEAD_COLOR);
                g2d.fillRoundRect(x + 1, y + 1, size, size, 8, 8);
                
                // Глаза
                g2d.setColor(Color.WHITE);
                int eyeSize = UNIT_SIZE / 6;
                if (snake.getDirection() == Direction.RIGHT) {
                    g2d.fillOval(x + UNIT_SIZE - eyeSize - 3, y + UNIT_SIZE / 4, eyeSize, eyeSize);
                    g2d.fillOval(x + UNIT_SIZE - eyeSize - 3, y + UNIT_SIZE - UNIT_SIZE / 4 - eyeSize, eyeSize, eyeSize);
                } else if (snake.getDirection() == Direction.LEFT) {
                    g2d.fillOval(x + 3, y + UNIT_SIZE / 4, eyeSize, eyeSize);
                    g2d.fillOval(x + 3, y + UNIT_SIZE - UNIT_SIZE / 4 - eyeSize, eyeSize, eyeSize);
                } else if (snake.getDirection() == Direction.UP) {
                    g2d.fillOval(x + UNIT_SIZE / 4, y + 3, eyeSize, eyeSize);
                    g2d.fillOval(x + UNIT_SIZE - UNIT_SIZE / 4 - eyeSize, y + 3, eyeSize, eyeSize);
                } else {
                    g2d.fillOval(x + UNIT_SIZE / 4, y + UNIT_SIZE - eyeSize - 3, eyeSize, eyeSize);
                    g2d.fillOval(x + UNIT_SIZE - UNIT_SIZE / 4 - eyeSize, y + UNIT_SIZE - eyeSize - 3, eyeSize, eyeSize);
                }
            } else {
                // Тело змейки
                g2d.setColor(SNAKE_BODY_COLOR);
                g2d.fillRoundRect(x + 1, y + 1, size, size, 5, 5);
            }
        }
        
        // Отрисовка счёта и рекорда
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String scoreText = "Счёт: " + score + "    Рекорд: " + bestScore;
        int textWidth = g2d.getFontMetrics().stringWidth(scoreText);
        g2d.drawString(scoreText, (getWidth() - textWidth) / 2, 25);
        
        // Отрисовка сообщения об окончании игры
        if (gameOver) {
            g2d.setColor(OVERLAY_COLOR);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            String gameOverMsg = "ИГРА ОКОНЧЕНА!";
            int msgWidth = g2d.getFontMetrics().stringWidth(gameOverMsg);
            g2d.drawString(gameOverMsg, (getWidth() - msgWidth) / 2, getHeight() / 2 - 30);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            String restartMsg = "Нажмите ПРОБЕЛ, чтобы начать заново";
            int restartWidth = g2d.getFontMetrics().stringWidth(restartMsg);
            g2d.drawString(restartMsg, (getWidth() - restartWidth) / 2, getHeight() / 2 + 20);
        }
    }
}
