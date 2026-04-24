import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Перечисление возможных направлений движения змейки.
 */
enum Direction {
    UP, DOWN, LEFT, RIGHT
}

/**
 * Класс, представляющий змейку.
 * Хранит список сегментов тела и направление движения.
 */
public class Snake {
    
    private List<Point> segments;
    private Direction direction;
    
    /**
     * Конструктор. Создаёт змейку из 3 сегментов в центре поля.
     */
    public Snake() {
        segments = new ArrayList<>();
        // Начальное положение: горизонтальная змейка из 3 сегментов
        segments.add(new Point(10, 10));  // Голова
        segments.add(new Point(9, 10));   // Тело
        segments.add(new Point(8, 10));   // Хвост
        direction = Direction.RIGHT;
    }
    
    /**
     * Движение змейки (без роста).
     * Добавляет новую голову и удаляет хвост.
     * @param newHead координаты новой головы
     */
    public void move(Point newHead) {
        segments.add(0, newHead);
        segments.remove(segments.size() - 1);
    }
    
    /**
     * Рост змейки (при поедании еды).
     * Добавляет новую голову, хвост не удаляется.
     * @param newHead координаты новой головы
     */
    public void grow(Point newHead) {
        segments.add(0, newHead);
    }
    
    /**
     * @return список сегментов змейки (первый элемент — голова)
     */
    public List<Point> getSegments() {
        return segments;
    }
    
    /**
     * @return текущее направление движения
     */
    public Direction getDirection() {
        return direction;
    }
    
    /**
     * Устанавливает новое направление движения.
     * @param direction новое направление
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
