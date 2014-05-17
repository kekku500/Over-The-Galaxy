package graphics.gui.mapeditor;

import java.util.List;

import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

import main.state.RenderState;
import de.matthiasmann.twl.BorderLayout;
import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ColorSelector;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.ValueAdjusterFloat;
import de.matthiasmann.twl.model.ColorSpaceHSL;
import de.matthiasmann.twl.model.OptionBooleanModel;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.model.SimpleFloatModel;
import de.matthiasmann.twl.model.SimpleIntegerModel;
import entity.creation.DefaultPointLight;
import entity.creation.DefaultSpotLight;
import entity.creation.ModeledEntity;
import entity.sheet.Entity;
import entity.sheet.Lighting;
import entity.sheet.PointLighting;
import entity.sheet.SpotLighting;

public class LightingPropFrame extends CloseableFrame{
	
    private EntityCreator creator;
    
    ComboBox<String> comboBox;
    
    public Lighting lighting;
    
	ValueAdjusterFloat cAAdjuster;
	ValueAdjusterFloat cLAdjuster;
	ValueAdjusterFloat cQAdjuster;
	
	ValueAdjusterFloat sCutAdjuster;
	ValueAdjusterFloat sExpAdjuster;
	
	ColorPickerWindow colorPicker;
	
	public LightingPropFrame(final EntityCreator creator){
		super(false);
		this.creator = creator;
		setTheme("resizableframe-title");
		setTitle("Lighting Properties");
		setSize(200, 200);
		
		
        Label cbLabel = new Label("Type: ");
        cbLabel.setTheme("label");
        final SimpleChangableListModel<String> lmStyle = 
        		new SimpleChangableListModel<String>("Point", "Spot");
        
        comboBox = new ComboBox<String>(lmStyle);
        comboBox.addCallback(new Runnable(){

			@Override
			public void run() {
				if(lighting != null){
					destoryLighting();
					createLighting((ModeledEntity)creator.creatingEntity);
				}
			}});
        comboBox.setSelected(0);
        comboBox.setComputeWidthFromModel(true);
        
        BorderLayout root = new BorderLayout();
        root.setTheme("");
        add(root);
        BoxLayout topHorz = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        topHorz.setTheme("");
        root.add(topHorz, BorderLayout.Location.NORTH);
        
        topHorz.add(cbLabel);
        topHorz.add(comboBox);
        
        BoxLayout centerVert = new BoxLayout(BoxLayout.Direction.VERTICAL);
        centerVert.setTheme("");
        root.add(centerVert, BorderLayout.Location.CENTER);
        
		SimpleFloatModel xChanger = new SimpleFloatModel(0, 1, .5f);
		xChanger.addCallback(new Runnable(){

			@Override
			public void run() {
				if(lighting != null && lighting instanceof PointLighting){
					PointLighting p = (PointLighting)lighting;
					p.setConstantAttenuation(cAAdjuster.getValue());
				}
				
			}});
		cAAdjuster = new ValueAdjusterFloat(xChanger);
		cAAdjuster.setStepSize(0.01f);
        Label xLabel = new Label("Const attenuation: ");
        xLabel.setLabelFor(cAAdjuster);
        
        BoxLayout bHz1 = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        bHz1.setTheme("");
        bHz1.add(xLabel);
        bHz1.add(cAAdjuster);
        centerVert.add(bHz1);
        
        
		SimpleFloatModel xChanger2 = new SimpleFloatModel(0, .001f, 0);
		xChanger2.addCallback(new Runnable(){

			@Override
			public void run() {
				if(lighting != null && lighting instanceof PointLighting){
					PointLighting p = (PointLighting)lighting;
					p.setLinearAttenuation(cLAdjuster.getValue());
				}
				
			}});
		
		cLAdjuster = new ValueAdjusterFloat(xChanger2);
		cLAdjuster.setStepSize(0.0001f);

        Label xLLabel = new Label("Linear attenuation: ");
        xLLabel.setLabelFor(cAAdjuster);
        
        BoxLayout bHz2 = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        bHz2.setTheme("");
        bHz2.add(xLLabel);
        bHz2.add(cLAdjuster);
        centerVert.add(bHz2);
        
        
		SimpleFloatModel xChanger3 = new SimpleFloatModel(0, .0001f, 0);
		xChanger3.addCallback(new Runnable(){

			@Override
			public void run() {
				if(lighting != null && lighting instanceof PointLighting){
					PointLighting p = (PointLighting)lighting;
					System.out.println(cQAdjuster.getValue());
					p.setQuadricAttenuation(cQAdjuster.getValue());
				}
				
			}});
		cQAdjuster = new ValueAdjusterFloat(xChanger3);
		cQAdjuster.setStepSize(0.0000001f);
        Label xQLabel = new Label("Quadric attenuation: ");
        xQLabel.setLabelFor(cQAdjuster);
        
        BoxLayout bHz3 = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        bHz3.setTheme("");
        bHz3.add(xQLabel);
        bHz3.add(cQAdjuster);
        centerVert.add(bHz3);
        

        
        colorPicker = new ColorPickerWindow();
        colorPicker.hide();
        creator.editor.desktop.add(colorPicker);
        
        BoxLayout horzColorBox = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        horzColorBox.setTheme("");
        centerVert.add(horzColorBox);
        
        horzColorBox.add(new Label("Colors: "));
        
        
        ToggleButton[] colorButtons = new ToggleButton[3];
        final SimpleIntegerModel optionModel = new SimpleIntegerModel(0, colorButtons.length, 0);
        String[] btnLabels = {"Ambient", "Diffuse", "Specular"};
        for(int i=0 ; i<colorButtons.length ; i++) {
            colorButtons[i] = new ToggleButton(new OptionBooleanModel(optionModel, i));
            colorButtons[i].setText(btnLabels[i]);
            colorButtons[i].setTheme("button");
            horzColorBox.add(colorButtons[i]);
        }
        
        optionModel.addCallback(new Runnable(){

			@Override
			public void run() {
				if(lighting != null){
					if(optionModel.getValue() == 0){
						colorPicker.setVector(lighting.getAmbient());
						colorPicker.setTitle("Ambient Selector");
					}else if(optionModel.getValue() == 1){
						colorPicker.setVector(lighting.getDiffuse());
						colorPicker.setTitle("Diffuse Selector");
					}else if(optionModel.getValue() == 2){
						colorPicker.setVector(lighting.getSpecular());
						colorPicker.setTitle("Specular Selector");
					}
					colorPicker.show();
				}
			}});
        

		SimpleFloatModel spotCutoff = new SimpleFloatModel(0, (float)Math.PI, (float)Math.cos(Math.toRadians(30)));
		spotCutoff.addCallback(new Runnable(){

			@Override
			public void run() {
				if(lighting != null && lighting instanceof SpotLighting){
					SpotLighting p = (SpotLighting)lighting;
					p.setSpotCutoff(sCutAdjuster.getValue());
				}
				
			}});
		sCutAdjuster = new ValueAdjusterFloat(spotCutoff);
		sCutAdjuster.setStepSize(0.01f);
		Label sCutLabel = new Label("Spot cutoff: ");
        xQLabel.setLabelFor(cQAdjuster);
        
        BoxLayout bHz4 = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        bHz4.setTheme("");
        bHz4.add(sCutLabel);
        bHz4.add(sCutAdjuster);
        centerVert.add(bHz4);
        
        
        
		SimpleFloatModel spotExponent = new SimpleFloatModel(0, .3f, 0);
		spotExponent.addCallback(new Runnable(){


			@Override
			public void run() {
				if(lighting != null && lighting instanceof SpotLighting){
					SpotLighting p = (SpotLighting)lighting;
					p.setSpotExponent(sExpAdjuster.getValue());
				}
				
			}});
		sExpAdjuster = new ValueAdjusterFloat(spotExponent);
		sExpAdjuster.setStepSize(0.01f);
        Label sExpLabel = new Label("Spot exponent: ");
        sExpLabel.setLabelFor(sExpAdjuster);
        
        BoxLayout bHz5 = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        bHz5.setTheme("");
        bHz5.add(sExpLabel);
        bHz5.add(sExpAdjuster);
        centerVert.add(bHz5);
        
        Button removeLight = new Button("Remove light");
        removeLight.setTheme("button");
        removeLight.addCallback(new Runnable(){

			@Override
			public void run() {
				destoryLighting();
			}});
        
        centerVert.add(removeLight);
        
        Button but = new Button("Enable shadows");
        but.setTheme("button");
        but.addCallback(new Runnable(){

			@Override
			public void run() {
				castShadows();		
			}
        	
        });
        centerVert.add(but);
	}
	
	public void createLighting(ModeledEntity e){
		if(e != null && lighting == null){
			//point
			boolean created = false;
			if(comboBox.getSelected() == 0){
				lighting = new DefaultPointLight(e.getEntityManager());
				created = true;
			//spot
			}else if(comboBox.getSelected() == 1){
				lighting = new DefaultSpotLight(e.getEntityManager());
				created = true;
			}
			if(created){
				System.out.println("created light " + lighting);
				e.addChild(lighting);
				e.castShadow(false);
			}
			
		}

	}
	
	private void  castShadows(){
		if(lighting != null){
			List<Lighting> lights = creator.editor.state.getEntityManager().getLightingEntities(RenderState.uptodate());
			for(Lighting l: lights){
				if(l.isShadowed())
					l.setShadowed(false);
			}
			System.out.println(lighting + " casts shadows");
			lighting.setShadowed(true);
		}
	}
	
	public void destoryLighting(){
		if(lighting != null){
			lighting.getEntityManager().removeEntity(lighting);
			
			if(creator.creatingEntity != null){
				creator.creatingEntity.getChildren().remove(lighting);
				creator.creatingEntity.castShadow(true);
			}
			
			lighting = null;
		}
	}
	
	@Override
	public void show(){
		super.show();
		
		createLighting(creator.creatingEntity);
	}
	
	@Override
	public void hide(){
		super.hide();
		colorPicker.hide();
		if(creator.enableLighting != null)
			creator.enableLighting.setActive(false);
	}
	
	public Lighting getLighting(){
		return lighting;
	}
	
	public void setLighting(Lighting l){
		lighting = l;
	}

}
