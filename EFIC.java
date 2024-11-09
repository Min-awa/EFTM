import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EFIC {
    private static final String folderPath = "A:\\Users\\Administrator\\Documents\\Escape from Tarkov\\Screenshots";
    private static final Set<String> processedCoordinates = new HashSet<>();
    private static ScheduledExecutorService executorService;
    private static boolean isRunning = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("脚本控制");
        JLabel statusLabel = new JLabel("脚本已停止");
        JButton startStopButton = new JButton("启动");

        // 添加快捷键监听
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
            if (event.getKeyCode() == KeyEvent.VK_F1 && event.isControlDown()) {
                if (isRunning) {
                    stopScript();
                    startStopButton.setText("启动");
                    statusLabel.setText("脚本已停止");
                } else {
                    startScript();
                    startStopButton.setText("停止");
                    statusLabel.setText("脚本正在运行");
                }
                return true;
            }
            return false;
        });

        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRunning) {
                    stopScript();
                    startStopButton.setText("启动");
                    statusLabel.setText("脚本已停止");
                } else {
                    startScript();
                    startStopButton.setText("停止");
                    statusLabel.setText("脚本正在运行");
                }
            }
        });

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(startStopButton);
        panel.add(statusLabel);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopScript();
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setSize(new Dimension(300, 100));
        frame.setVisible(true);
    }

    public static void startScript() {
        if (!isRunning) {
            isRunning = true;
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                try {
                    Robot robot = new Robot();
                    robot.keyPress(java.awt.event.KeyEvent.VK_PAUSE);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_PAUSE);
                    System.out.println("按下 Pause 键。");
                } catch (AWTException e) {
                    System.err.println("按下 Pause 键时出现错误：" + e.getMessage());
                }

                try {
                    File folder = new File(folderPath);
                    if (!folder.exists() ||!folder.isDirectory()) {
                        System.out.println("指定文件夹不存在。");
                        return;
                    }

                    List<File> pngFilesList;
                    try {
                        pngFilesList = new ArrayList<>(List.of(folder.listFiles((dir, name) -> name.endsWith(".png"))));
                    } catch (Exception e) {
                        System.err.println("获取 PNG 文件列表时出现错误：" + e.getMessage());
                        return;
                    }

                    List<String> currentFileNames = new ArrayList<>();
                    for (File file : pngFilesList) {
                        currentFileNames.add(file.getName());
                        readCoordinates(file.getName());
                    }

                    // 删除已处理的旧文件
                    for (String oldFileName : processedCoordinates) {
                        File oldFile = new File(folderPath, oldFileName);
                        if (oldFile.exists() && oldFile.isFile() &&!currentFileNames.contains(oldFileName)) {
                            try {
                                oldFile.delete();
                            } catch (Exception e) {
                                System.err.println("删除旧文件 " + oldFileName + " 时出现错误：" + e.getMessage());
                            }
                        }
                    }

                    // 删除当前已处理的文件
                    for (String fileName : currentFileNames) {
                        File fileToDelete = new File(folderPath, fileName);
                        if (fileToDelete.exists() && fileToDelete.isFile()) {
                            try {
                                fileToDelete.delete();
                            } catch (Exception e) {
                                System.err.println("删除文件 " + fileName + " 时出现错误：" + e.getMessage());
                            }
                        }
                    }

                    processedCoordinates.clear();
                    processedCoordinates.addAll(currentFileNames);
                } catch (Exception e) {
                    System.err.println("处理文件时出现错误：" + e.getMessage());
                }
            }, 0, 2, TimeUnit.SECONDS);
        }
    }

    public static void stopScript() {
        if (isRunning && executorService!= null) {
            executorService.shutdown();
            isRunning = false;
        }
    }

    public static void readCoordinates(String fileName) {
        String pattern = "_(-?\\d+\\.\\d+), (-?\\d+\\.\\d+), (-?\\d+\\.\\d+)_(-?\\d+\\.\\d+), (-?\\d+\\.\\d+), (-?\\d+\\.\\d+), (-?\\d+\\.\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(fileName);
        if (m.find()) {
            double x = Double.parseDouble(m.group(1));
            double y = Double.parseDouble(m.group(2));
            double z = Double.parseDouble(m.group(3));
            double rotationX = Double.parseDouble(m.group(4));
            double rotationY = Double.parseDouble(m.group(5));
            double rotationZ = Double.parseDouble(m.group(6));
            double rotationW = Double.parseDouble(m.group(7));

            String coordStr = "x=" + x + ", y=" + y + ", z=" + z;
            String uniqueKey = coordStr + "_" + rotationX + "_" + rotationY + "_" + rotationZ + "_" + rotationW;
            if (!processedCoordinates.contains(uniqueKey)) {
                System.out.println("文件名：" + fileName);
                System.out.println("坐标信息：");
                System.out.println("x=" + x);
                System.out.println("y=" + y);
                System.out.println("z=" + z);
                System.out.println("旋转信息：");
                System.out.println("x=" + rotationX);
                System.out.println("y=" + rotationY);
                System.out.println("z=" + rotationZ);
                System.out.println("w=" + rotationW);
                System.out.println();

                processedCoordinates.add(uniqueKey);
            }
        } else {
            System.out.println("文件名：" + fileName + " 未找到坐标信息。");
            System.out.println();
        }
    }
}