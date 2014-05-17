package graphics.gui.mapeditor;

import de.matthiasmann.twl.BorderLayout;
import de.matthiasmann.twl.BorderLayout.Location;
import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.EditField.Callback;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import entitymanager.Level;
import main.PlayState;

public class LevelLoader extends CloseableFrame{
	
	SimpleChangableListModel<String> model;
	ListBox<String> list;
	EditField levelNameHolder;
	
	private PlayState state;
	
	public boolean requestingSave = false;
	public boolean requestingLoad = false;
	
	public LevelLoader(final PlayState state){
		this.state = state;
		setTheme("resizableframe-title");
		setSize(150, 100);
		setTitle("Level Loader");
		
		list = new ListBox<String>();
		list.setTheme("listbox");
		model = new SimpleChangableListModel<String>();
		list.setModel(model);
		
		BorderLayout root = new BorderLayout();
		root.setTheme("");
		add(root);
		
		root.add(list, Location.CENTER);
		
		BoxLayout buttonBox = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
		buttonBox.setSpacing(10);
		buttonBox.setTheme("");
		root.add(buttonBox, Location.SOUTH);
		

		
		
		
		Button button = new Button("Load");
		button.setTheme("button");
		buttonBox.add(button);
		
		button.addCallback(new Runnable(){

			@Override
			public void run() {
				if(!requestingLoad && !requestingSave)
					requestingLoad = true;
			}
		});
		
		Button buttonSave = new Button("Save");
		buttonSave.setTheme("button");
		buttonBox.add(buttonSave);
		
		buttonSave.addCallback(new Runnable(){

			@Override
			public void run() {
				if(!requestingSave && !requestingLoad)
					requestingSave = true;
			}
		});
		
		
		levelNameHolder = new EditField();
		levelNameHolder.setText(state.getEntityManager().getLevel().getName());
		levelNameHolder.setTheme("editfield");
		
		
		buttonBox.add(new Label("Name: "));
		buttonBox.add(levelNameHolder);
		
		Button buttonRemove = new Button("Remove");
		buttonRemove.setTheme("button");
		buttonBox.add(buttonRemove);
		
		buttonRemove.addCallback(new Runnable(){

			@Override
			public void run() {
				if(list.getSelected() != -1)
					if(Level.removeLevel(model.getEntry(list.getSelected()))){
						model.removeElement(list.getSelected());
					}
			}
		});
		
	}
	
	public void saveLevel(){
		state.getEntityManager().getLevel().setName(levelNameHolder.getText());
		Level.saveLevelEntities(state.getEntityManager().getLevel());
		boolean duplicateName = false;
		for(int i = 0;i<model.getNumEntries();i++){
			if(model.getEntry(i).toLowerCase().equals(levelNameHolder.getText())){
				duplicateName = true;
				break;
			}
		}
		if(!duplicateName)
			addLevel(levelNameHolder.getText());
		requestingSave = false;
	}
	
	public void loadLevel(){
		if(list.getSelected() != -1){
			state.getEntityManager().getLevel().loadNewEntities(
					Level.loadLevelEntities(model.getEntry(list.getSelected())));
			requestingLoad = false;
		}
		levelNameHolder.setText(model.getEntry(list.getSelected()));

	}
	
	@Override
	public void layout(){
		super.layout();
		
		levelNameHolder.setMinSize(150, levelNameHolder.getMinHeight());
	}
	
	public void addLevel(String path){
		model.addElement(path);
	}

}
