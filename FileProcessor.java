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

public class FileProcessor {
    private static final Set<String> processedCoordinates = new HashSet<>();
    private static final Set<String> processedFileNames = new HashSet<>();
    private static String lastOutputCoordinates = null;

    public static void startFileProcessing() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            try {
                File folder = new File("A:\\Users\\Administrator\\Documents\\Escape from Tarkov\\Screenshots");
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
                    String fileName = file.getName();
                    if (!processedFileNames.contains(fileName)) {
                        currentFileNames.add(fileName);
                        String uniqueKey = readCoordinates(fileName);
                        if (uniqueKey!= null &&!processedCoordinates.contains(uniqueKey)) {
                            processedCoordinates.add(uniqueKey);
                        }
                    }
                }

                // 删除已处理的旧文件
                for (String oldFileName : processedFileNames) {
                    File oldFile = new File("A:\\Users\\Administrator\\Documents\\Escape from Tarkov\\Screenshots", oldFileName);
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
                    File fileToDelete = new File("A:\\Users\\Administrator\\Documents\\Escape from Tarkov\\Screenshots", fileName);
                    if (fileToDelete.exists() && fileToDelete.isFile()) {
                        try {
                            fileToDelete.delete();
                        } catch (Exception e) {
                            System.err.println("删除文件 " + fileName + " 时出现错误：" + e.getMessage());
                        }
                    }
                }

                processedFileNames.clear();
                processedFileNames.addAll(currentFileNames);
            } catch (Exception e) {
                System.err.println("处理文件时出现错误：" + e.getMessage());
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public static String readCoordinates(String fileName) {
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
            if (uniqueKey!= null &&!uniqueKey.equals(lastOutputCoordinates)) {
                lastOutputCoordinates = uniqueKey;
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
                return uniqueKey;
            } else {
                return null;
            }
        } else {
            System.out.println("文件名：" + fileName + " 未找到坐标信息。");
            System.out.println();
            return null;
        }
    }

    public static void main(String[] args) {
        startFileProcessing();
    }
}