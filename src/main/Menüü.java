package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.lwjgl.input.Keyboard;

import main.state.Game;
import input.InputConfig;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Menüü extends Application {

	@Override
	public void start(final Stage lava) {

		final BorderPane juur = new BorderPane();
		VBox nupud = new VBox();
		juur.setLeft(nupud);
		nupud.setPadding(new Insets(10));
		nupud.setSpacing(8);
				
		Button start = new Button("Start");
		start.setFocusTraversable(false);
		start.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle (MouseEvent e){
				new InputConfig().changeInput();
				new Game().start();
				lava.close();
			}
		});
		Button resolution = new Button("Graphics");
		resolution.setFocusTraversable(false);
		resolution.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				Graphics(juur);
			}
		});
		Button keys = new Button("Keyconfig");
		keys.setFocusTraversable(false);
		keys.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				Keyconfig(juur);
			}
		});
		
		nupud.getChildren().addAll(start, resolution, keys);
		lava.setTitle("Options");
		lava.setScene(new Scene(juur, 400,300,Color.SNOW));
		lava.show();
	}

	public static void menüü() {
		launch();
	}
	
	int Fov;
	String Resolution;
	boolean Shadows_;
	boolean ShadowFiltering;
	boolean AmbientOcclusion;
	boolean LightScattering;
	boolean NormalMapping;
	OutputStream output = null;
	InputStream input = null;
	Properties prop = new Properties();
	
	public void Graphics(BorderPane pane){
		try{
			input = new FileInputStream("res/config/config.properties");
			
			prop.load(input);
			
			Fov = Integer.parseInt(prop.getProperty("FOV"));
			Resolution = prop.getProperty("Resolution");
			Shadows_ = Boolean.parseBoolean(prop.getProperty("Shadows"));
			ShadowFiltering = Boolean.parseBoolean(prop.getProperty("Shadow_Filtering"));
			AmbientOcclusion = Boolean.parseBoolean(prop.getProperty("Ambient_Occlusion"));
			LightScattering = Boolean.parseBoolean(prop.getProperty("Light_Scattering"));
			NormalMapping = Boolean.parseBoolean(prop.getProperty("Normal_Mapping"));
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		GridPane box = new GridPane();
		pane.setCenter(box);

		box.getColumnConstraints().add(new ColumnConstraints(100));
		box.getColumnConstraints().add(new ColumnConstraints(10));
		box.getColumnConstraints().add(new ColumnConstraints(100));
		box.getColumnConstraints().add(new ColumnConstraints(10));
		
		box.getRowConstraints().add(new RowConstraints(20));
		box.getRowConstraints().add(new RowConstraints(20));
		box.getRowConstraints().add(new RowConstraints(20));
		box.getRowConstraints().add(new RowConstraints(20));
		box.getRowConstraints().add(new RowConstraints(20));
		box.getRowConstraints().add(new RowConstraints(20));
		
		final Label FOV = new Label("FOV");
		box.add(FOV, 0, 0);
		
		final ScrollBar scFOV = new ScrollBar();
		scFOV.setMin(45);
		scFOV.setMax(180);
		scFOV.setValue(Fov);
		scFOV.setOrientation(Orientation.HORIZONTAL);
		scFOV.setUnitIncrement(1);
		scFOV.blockIncrementProperty().set(1);
		box.add(scFOV, 2, 0);
		
		final Label lgFOV = new Label();
		lgFOV.setText(Integer.toString(scFOV.valueProperty().getValue().intValue()));
		box.add(lgFOV, 4, 0);
		
		scFOV.valueProperty().addListener(new ChangeListener<Number>() {
		    public void changed(ObservableValue<? extends Number> ov,
		        Number old_val, Number new_val) {
		    	Fov = new_val.intValue();
		            lgFOV.setText(Integer.toString(new_val.intValue()));
		        }
		});
		
		final Label Res = new Label("Resolution");
		box.add(Res, 0, 1);
		
		ObservableList<String> options =
				FXCollections.observableArrayList(
						"800x600",
						"1600x1200");
		
		final ComboBox<String> cbRes = new ComboBox<String>(options);
		cbRes.setValue(Resolution);
		box.add(cbRes, 2, 1);
		
		 cbRes.valueProperty().addListener(new ChangeListener<String>() {
	            @Override 
	            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
	            	Resolution = t1;
	            }    
	        });
		 
		final Label Shadows = new Label("Shadows");
		box.add(Shadows,0,2);
		
		CheckBox cbShadows = new CheckBox();
		cbShadows.setSelected(Shadows_);
		box.add(cbShadows, 2, 2);
		
		cbShadows.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	            Boolean old_val, Boolean new_val) {
	                Shadows_ = new_val;
	        }
	    });
		
		final Label Shadowfiltering = new Label("Shadowfiltering");
		box.add(Shadowfiltering,0,3);
		
		CheckBox cbSf = new CheckBox();
		cbSf.setSelected(ShadowFiltering);
		box.add(cbSf, 2, 3);
		
		cbSf.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	            Boolean old_val, Boolean new_val) {
	        		ShadowFiltering = new_val;
	        }
	    });
		
		final Label ambientocclusion = new Label("Ambient");
		box.add(ambientocclusion,0,4);
		
		CheckBox cbAo = new CheckBox();
		cbAo.setSelected(AmbientOcclusion);
		box.add(cbAo, 2, 4);
		
		cbAo.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	            Boolean old_val, Boolean new_val) {
	        		AmbientOcclusion = new_val;
	        }
	    });
		
		final Label lightscattering = new Label("Light scattering");
		box.add(lightscattering,0,5);
		
		CheckBox cbLs = new CheckBox();
		cbLs.setSelected(LightScattering);
		box.add(cbLs, 2, 5);
		
		cbLs.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	            Boolean old_val, Boolean new_val) {
	        		LightScattering = new_val;
	        }
	    });
		
		final Label normalmapping = new Label("Normal mapping");
		box.add(normalmapping,0,6);
		
		CheckBox cbNm = new CheckBox();
		cbNm.setSelected(NormalMapping);
		box.add(cbNm, 2, 6);
		
		cbNm.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	            Boolean old_val, Boolean new_val) {
	        		NormalMapping = new_val;
	        }
	    });

		Button SaveButton = new Button("Save");
		box.add(SaveButton,0, 7);
		SaveButton.setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event){
				try{
					output = new FileOutputStream("res/config/config.properties");
					
					prop.setProperty("FOV", Integer.toString(Fov));
					prop.setProperty("Resolution", Resolution);
					prop.setProperty("Shadows", Boolean.toString(Shadows_));
					prop.setProperty("Shadow_Filtering", Boolean.toString(ShadowFiltering));
					prop.setProperty("Ambient_Occlusion", Boolean.toString(AmbientOcclusion));
					prop.setProperty("Light_Scattering", Boolean.toString(LightScattering));
					prop.setProperty("Normal_Mapping", Boolean.toString(NormalMapping));
				
					prop.store(output, null);
					
				}catch(IOException io){
					io.printStackTrace();
				}finally{
					if(output != null){
						try{
							output.close();
						}catch(IOException e){
							e.printStackTrace();
						}
					}
				}
			}
		});

	}
	
	int playerAccelerate;
	int playerRotateRight;
	int playerRotateLeft;
	int playerRollRight;
	int playerRollLeft;
	int playerRollBack;
	int playerRollForward;
	
	public void Keyconfig(BorderPane pane){
		try{
			input = new FileInputStream("res/config/keyconfig.properties");
			
			prop.load(input);
			
			playerAccelerate = Integer.parseInt(prop.getProperty("playerAccelerate"));
			playerRotateRight = Integer.parseInt(prop.getProperty("playerRotateRight"));
			playerRotateLeft = Integer.parseInt(prop.getProperty("playerRotateLeft"));
			playerRollRight = Integer.parseInt(prop.getProperty("playerRollRight"));
			playerRollLeft = Integer.parseInt(prop.getProperty("playerRollLeft"));
			playerRollBack = Integer.parseInt(prop.getProperty("playerRollBack"));
			playerRollForward = Integer.parseInt(prop.getProperty("playerRollForward"));
			
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		final GridPane box = new GridPane();
		pane.setCenter(box);
		
		box.getColumnConstraints().add(new ColumnConstraints(70));
		box.getColumnConstraints().add(new ColumnConstraints(10));
		box.getColumnConstraints().add(new ColumnConstraints(70));
		box.getColumnConstraints().add(new ColumnConstraints(10));
		
		box.getRowConstraints().add(new RowConstraints(30));
		box.getRowConstraints().add(new RowConstraints(30));
		box.getRowConstraints().add(new RowConstraints(30));
		box.getRowConstraints().add(new RowConstraints(30));
		box.getRowConstraints().add(new RowConstraints(30));
		box.getRowConstraints().add(new RowConstraints(30));
		box.getRowConstraints().add(new RowConstraints(30));
		box.getRowConstraints().add(new RowConstraints(30));
		
		final Label Forward = new Label ("Forward");
		box.add(Forward,0,0);
		
		final Button forward = new Button(Keyboard.getKeyName(playerAccelerate));
		forward.setMinWidth(70);
		forward.setFocusTraversable(false);
		forward.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				forward.requestFocus();
				forward.setOnKeyPressed(new EventHandler<KeyEvent>(){
					public void handle(KeyEvent k){
						playerAccelerate = Keyboard.getKeyIndex(k.getCode().getName().toUpperCase());
						forward.setText(k.getCode().getName().toUpperCase());
						box.requestFocus();
					}
				});
			}
		});
		box.add(forward, 2, 0);
		
		final Label Left = new Label ("Left");
		box.add(Left,0,1);
		
		final Button left = new Button(Keyboard.getKeyName(playerRotateLeft));
		left.setMinWidth(70);
		left.setFocusTraversable(false);
		left.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				left.requestFocus();
				left.setOnKeyPressed(new EventHandler<KeyEvent>(){
					public void handle(KeyEvent k){
						playerRotateLeft = Keyboard.getKeyIndex(k.getCode().getName().toUpperCase());
						left.setText(k.getCode().getName().toUpperCase());
						box.requestFocus();
					}
				});
			}
		});
		box.add(left, 2, 1);
		
		final Label Right = new Label ("Right");
		box.add(Right,0,2);
		
		final Button right = new Button(Keyboard.getKeyName(playerRotateRight));
		right.setMinWidth(70);
		right.setFocusTraversable(false);
		right.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				right.requestFocus();
				right.setOnKeyPressed(new EventHandler<KeyEvent>(){
					public void handle(KeyEvent k){
						playerRotateRight = Keyboard.getKeyIndex(k.getCode().getName().toUpperCase());
						right.setText(k.getCode().getName().toUpperCase());
						box.requestFocus();
					}
				});
			}
		});
		box.add(right, 2, 2);
		
		final Label RollRight = new Label ("Roll Right");
		box.add(RollRight,0,3);
		
		final Button rollright = new Button(Keyboard.getKeyName(playerRollRight));
		rollright.setMinWidth(70);
		rollright.setFocusTraversable(false);
		rollright.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				rollright.requestFocus();
				rollright.setOnKeyPressed(new EventHandler<KeyEvent>(){
					public void handle(KeyEvent k){
						playerRollRight = Keyboard.getKeyIndex(k.getCode().getName().toUpperCase());
						rollright.setText(k.getCode().getName().toUpperCase());
						box.requestFocus();
					}
				});
			}
		});
		box.add(rollright, 2, 3);
		
		final Label RollLeft = new Label ("Roll Left");
		box.add(RollLeft,0,4);
		
		final Button rollleft = new Button(Keyboard.getKeyName(playerRollLeft));
		rollleft.setMinWidth(70);
		rollleft.setFocusTraversable(false);
		rollleft.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				rollleft.requestFocus();
				rollleft.setOnKeyPressed(new EventHandler<KeyEvent>(){
					public void handle(KeyEvent k){
						playerRollLeft = Keyboard.getKeyIndex(k.getCode().getName().toUpperCase());
						rollleft.setText(k.getCode().getName().toUpperCase());
						box.requestFocus();
					}
				});
			}
		});
		box.add(rollleft, 2, 4);
		
		final Label RollBack = new Label ("Roll Back");
		box.add(RollBack,0,5);
		
		final Button rollback = new Button(Keyboard.getKeyName(playerRollBack));
		rollback.setMinWidth(70);
		rollback.setFocusTraversable(false);
		rollback.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				rollback.requestFocus();
				rollback.setOnKeyPressed(new EventHandler<KeyEvent>(){
					public void handle(KeyEvent k){
						playerRollBack = Keyboard.getKeyIndex(k.getCode().getName().toUpperCase());
						rollback.setText(k.getCode().getName().toUpperCase());
						box.requestFocus();
					}
				});
			}
		});
		box.add(rollback, 2, 5);
		
		final Label RollForward = new Label ("Roll Forward");
		box.add(RollForward,0,6);
		
		final Button rollforward = new Button(Keyboard.getKeyName(playerRollForward));
		rollforward.setMinWidth(70);
		rollforward.setFocusTraversable(false);
		rollforward.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				rollforward.requestFocus();
				rollforward.setOnKeyPressed(new EventHandler<KeyEvent>(){
					public void handle(KeyEvent k){
						playerRollForward = Keyboard.getKeyIndex(k.getCode().getName().toUpperCase());
						rollforward.setText(k.getCode().getName().toUpperCase());
						box.requestFocus();
					}
				});
			}
		});
		box.add(rollforward, 2, 6);
		
		Button SaveButton = new Button("Save");
		SaveButton.setFocusTraversable(false);
		box.add(SaveButton,0, 7);
		SaveButton.setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event){
				try{
					output = new FileOutputStream("res/config/keyconfig.properties");
					
					prop.setProperty("playerAccelerate", Integer.toString(playerAccelerate));
					prop.setProperty("playerRotateRight", Integer.toString(playerRotateRight));
					prop.setProperty("playerRotateLeft", Integer.toString(playerRotateLeft));
					prop.setProperty("playerRollLeft", Integer.toString(playerRollLeft));
					prop.setProperty("playerRollRight", Integer.toString(playerRollRight));
					prop.setProperty("playerRollBack", Integer.toString(playerRollBack));
					prop.setProperty("playerRollForward", Integer.toString(playerRollForward));
				
					prop.store(output, null);
					
				}catch(IOException io){
					io.printStackTrace();
				}finally{
					if(output != null){
						try{
							output.close();
						}catch(IOException e){
							e.printStackTrace();
						}
					}
				}
			}
		});
	}
}

