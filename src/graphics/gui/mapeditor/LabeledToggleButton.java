package graphics.gui.mapeditor;

import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ToggleButton;

public class LabeledToggleButton extends BoxLayout{
	
	private Label label;
	private ToggleButton button;
	
	private String[] buttonStates = {"Enabled", "Disabled"};
	
	private Runnable activationRunnable;
	private Runnable deactivationRunnable;
	
	public LabeledToggleButton(String labelText){
		super(BoxLayout.Direction.HORIZONTAL);
		setTheme("");
		setSpacing(10);
		label = new Label(labelText);
		label.setTheme("label");
		
		button = new ToggleButton(buttonStates[0]);
		button.setTheme("togglebutton");
		button.addCallback(new Runnable(){

			@Override
			public void run() {
				if(button.isActive()){
					activated();
				}else{
					deactivated();
				}
				
			}
			
		});
		
		add(label);
		add(button);
	}
	
	public void setLabelText(String text){
		label.setText(text);
	}
	
	public void activated(){
		new Thread(activationRunnable).start();
	}
	
	public void setActivationCallback(Runnable run){
		activationRunnable = run;
	}
	
	public void deactivated(){
		new Thread(deactivationRunnable).start();
	}
	
	public void setDeactivationCallback(Runnable run){
		deactivationRunnable = run;
	}
	
	public void setDeactivationCallbackFromActication(){
		deactivationRunnable = activationRunnable;
	}
	
	public ToggleButton getButton(){
		return button;
	}

}
