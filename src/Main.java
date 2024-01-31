import csc2b.Client.ClientPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 */

/**
 * @author SN MAHLOBO
 *
 */
public class Main extends Application{

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		//Setting title
		primaryStage.setTitle("");
		
		//Setting Scene
		ClientPane root = new ClientPane(5000);
		
		primaryStage.setScene(new Scene(root,850,600));
		
		primaryStage.show();
		
	}

}
