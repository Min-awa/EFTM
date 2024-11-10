import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EFTI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 设置窗口标题
        primaryStage.setTitle("EFTI");

        // 创建一个垂直布局容器
        VBox root = new VBox();

        // 创建场景并将布局容器添加到场景中，设置窗口大小为 1250x700
        Scene scene = new Scene(root, 1250, 700);
        primaryStage.setScene(scene);

        // 去除标题栏
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // 获取屏幕尺寸并设置窗口位置在屏幕中央
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setX((screenWidth - 1250) / 2);
        primaryStage.setY((screenHeight - 700) / 2);

        // 创建一个带有竖向红色分割线的 Pane
        Pane dividerPane = new Pane();
        dividerPane.getStyleClass().add("myVerticalDivider");

        // 将分割线添加到已有的 VBox 中
        root.getChildren().addAll(dividerPane);

        // 设置分割线的高度与 VBox 的高度相同
        dividerPane.prefHeightProperty().bind(root.heightProperty());

        // 加载 CSS 文件，假设 styles.css 与主类文件在同一目录下
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // 显示窗口
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}