import javax.swing.*;

/**
 * Главный класс игры Змейка.
 * Точка входа в приложение.
 */
public class SnakeGame {
    public static void main(String[] args) {
        // Запуск в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Змейка");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            
            // Создание игрового поля
            GameBoard gameBoard = new GameBoard();
            frame.add(gameBoard);
            
            // Настройка окна
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Запуск игры
            gameBoard.startGame();
        });
    }
}
