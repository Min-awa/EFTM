import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScreenshotTaker {
    private static ScheduledExecutorService executorService;
    private static boolean isRunning = false;
    private static final String screenshotsFolderPath = "A:\\Users\\Administrator\\Documents\\Escape from Tarkov\\Screenshots";

    public static void startScreenshotting() {
        if (!isRunning) {
            isRunning = true;
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                try {
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_PAUSE);
                    robot.keyRelease(KeyEvent.VK_PAUSE);
                    System.out.println("按下 Pause 键进行截图。");
                } catch (AWTException e) {
                    System.err.println("按下 Pause 键时出现错误：" + e.getMessage());
                }
            }, 0, 2, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    public static void stopScreenshotting() {
        if (isRunning && executorService!= null) {
            executorService.shutdown();
            isRunning = false;
        }
    }

    public static void main(String[] args) {
        startScreenshotting();
    }
}