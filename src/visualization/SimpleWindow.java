package visualization;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class SimpleWindow extends Application {
	
	BorderPane bp = new BorderPane();
	Scene scene = new Scene(bp, 1000, 400);
	Stage myStage = new Stage();
	
	public SimpleWindow() {
		myStage.setScene(scene);
		myStage.show();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//BorderPane bp = new BorderPane();
		//Scene scene = new Scene(bp, 1000, 400);
		//primaryStage.setScene(scene);
		//primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
