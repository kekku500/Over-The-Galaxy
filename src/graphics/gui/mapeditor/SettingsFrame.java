package graphics.gui.mapeditor;

import input.InputConfig;
import main.PlayState;
import de.matthiasmann.twl.BorderLayout;
import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.BorderLayout.Location;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import entitymanager.Level;
import graphics.Graphics3D;

public class SettingsFrame extends CloseableFrame{

	private PlayState state;

	public SettingsFrame(final PlayState state){
		this.state = state;
		setTheme("resizableframe-title");
		setSize(150, 100);
		setTitle("Settings");
		
		BoxLayout verticalList = new BoxLayout(BoxLayout.Direction.VERTICAL);
		verticalList.setTheme("");
		verticalList.setSpacing(10);
		add(verticalList);
		
		LabeledToggleButton toggleTexturingButton = new LabeledToggleButton("Texturing: ");
		toggleTexturingButton.getButton().setActive(Graphics3D.texturing);
		toggleTexturingButton.setActivationCallback(new Runnable(){
			@Override
			public void run() {
				
				Graphics3D.texturing = !Graphics3D.texturing;
		}});
		toggleTexturingButton.setDeactivationCallbackFromActication();
		verticalList.add(toggleTexturingButton);
		
		LabeledToggleButton toggleNormalButton = new LabeledToggleButton("Normal mapping: ");
		toggleNormalButton.getButton().setActive(Graphics3D.normalMapping);
		toggleNormalButton.setActivationCallback(new Runnable(){
			@Override
			public void run() {
				Graphics3D.normalMapping = !Graphics3D.normalMapping;
		}});
		toggleNormalButton.setDeactivationCallbackFromActication();
		verticalList.add(toggleNormalButton);
		
		LabeledToggleButton toggleShadowButton = new LabeledToggleButton("Shadows: ");
		toggleShadowButton.getButton().setActive(Graphics3D.shadows);
		toggleShadowButton.setActivationCallback(new Runnable(){
			@Override
			public void run() {
				Graphics3D.shadows = !Graphics3D.shadows;
		}});
		toggleShadowButton.setDeactivationCallbackFromActication();
		verticalList.add(toggleShadowButton);
		
		LabeledToggleButton toggleShadowFilteringButton = new LabeledToggleButton("Shadow filtering: ");
		toggleShadowFilteringButton.getButton().setActive(Graphics3D.filtering);
		toggleShadowFilteringButton.setActivationCallback(new Runnable(){
			@Override
			public void run() {
				Graphics3D.filtering = !Graphics3D.filtering;
		}});
		toggleShadowFilteringButton.setDeactivationCallbackFromActication();
		verticalList.add(toggleShadowFilteringButton);
		
		LabeledToggleButton toggleAmbientButton = new LabeledToggleButton("Ambient occlusion: ");
		toggleAmbientButton.getButton().setActive(Graphics3D.occlusion);
		toggleAmbientButton.setActivationCallback(new Runnable(){
			@Override
			public void run() {
				Graphics3D.occlusion = !Graphics3D.occlusion;
		}});
		toggleAmbientButton.setDeactivationCallbackFromActication();
		verticalList.add(toggleAmbientButton);
		
		LabeledToggleButton toggleScatteringButton = new LabeledToggleButton("Light scattering: ");
		toggleScatteringButton.getButton().setActive(Graphics3D.lightScattering);
		toggleScatteringButton.setActivationCallback(new Runnable(){
			@Override
			public void run() {
				Graphics3D.lightScattering = !Graphics3D.lightScattering;
		}});
		toggleScatteringButton.setDeactivationCallbackFromActication();
		verticalList.add(toggleScatteringButton);
		
		BoxLayout tex = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
		tex.setTheme("");
		
		final ComboBox<String> comboBox = new ComboBox<String>();
		
		comboBox.setComputeWidthFromModel(true);
		SimpleChangableListModel<String> comboModel = new SimpleChangableListModel<String>();
		comboBox.setModel(comboModel);

		comboModel.addElement("Default");
		comboModel.addElement("Color");
		comboModel.addElement("Normal");
		comboModel.addElement("Depth");
		comboModel.addElement("Material Ambient");
		comboModel.addElement("Material Diffuse");
		comboModel.addElement("Material Emission");
		comboModel.addElement("Material Shininess");
		comboModel.addElement("Material Specular");
		comboModel.addElement("Light Scattering");
		comboModel.addElement("Ambient Occlusion");
		comboBox.setSelected(0);
		comboBox.addCallback(new Runnable(){

			@Override
			public void run() {
				int tex = comboBox.getSelected();
				switch(tex){
				case 0:
					Graphics3D.showTexture = 0;break;
				case 1:
					Graphics3D.showTexture = Graphics3D.colorBuffer;break;
				case 2:
					Graphics3D.showTexture = Graphics3D.normalBuffer;break;
				case 3:
					Graphics3D.showTexture = Graphics3D.depthBuffer;break;
				case 4:
					Graphics3D.showTexture = Graphics3D.materialAmbient;break;
				case 5:
					Graphics3D.showTexture = Graphics3D.materialDiffuse;break;
				case 6:
					Graphics3D.showTexture = Graphics3D.materialEmission;break;
				case 7:
					Graphics3D.showTexture = Graphics3D.materialShininess;break;
				case 8:
					Graphics3D.showTexture = Graphics3D.materialSpecular;break;
				case 9:
					Graphics3D.showTexture = Graphics3D.sunTextures.get(0);break;
				case 10:
					Graphics3D.showTexture = Graphics3D.SSAOTexturesBlurred.get(1);break;
				}
			}});
		comboBox.setTheme("combobox");
		
		tex.add(new Label("Show Texture: "));
		tex.add(comboBox);
		
		verticalList.add(tex);
	}
}
