package main;

import state.Game;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Menüü extends Application {

	@Override
	public void start(final Stage lava) {
		VBox juur = new VBox();
		juur.setPadding(new Insets(10));
		juur.setSpacing(8);
				
		Button start = new Button("Start");
		start.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle (MouseEvent e){
				new Game().start();
				lava.close();
			}
		});
		Button resolution = new Button("resolution");
		resolution.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				resolution();
			}
		});
		
		juur.getChildren().addAll(start, resolution);
		lava.setTitle("Options");
		lava.setScene(new Scene(juur, 50,200,Color.SNOW));
		lava.show();
	}

	public static void menüü() {
		launch();
	}
	
	public void resolution(){
		final Stage vastus = new Stage();
		VBox box = new VBox();
		RadioButton rb1 = new RadioButton();
		rb1.setText("800X600");
		RadioButton rb2 = new RadioButton();
		rb2.setText("1200X700");
		box.setAlignment(Pos.CENTER);
		Button OKButton = new Button("Ok");
		OKButton.setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event){
				vastus.hide();
			}
		});
		box.getChildren().addAll( OKButton, rb1, rb2);
		Scene stseen2 = new Scene(box);
		vastus.setScene(stseen2);
		vastus.show();
	}
}

