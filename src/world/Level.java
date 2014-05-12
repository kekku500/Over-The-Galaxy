package world;

import java.io.Serializable;

import state.StateVariable;
import utils.ArrayList;
import utils.HashSet;
import world.entity.Entity;
import world.entity.VisualEntity;
import world.entity.create.DynamicEntity;
import world.entity.lighting.Lighting;

public class Level implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public StateVariable<HashSet<Entity>> entities;
	public StateVariable<ArrayList<VisualEntity>> visualEntites;
	public StateVariable<ArrayList<Lighting>> lightingEntities;
	public StateVariable<HashSet<DynamicEntity>> dynamicEntities;
	
	public Level(){
		entities = new StateVariable<HashSet<Entity>>(new HashSet<Entity>());
		visualEntites = new StateVariable<ArrayList<VisualEntity>>(new ArrayList<VisualEntity>());
		lightingEntities = new StateVariable<ArrayList<Lighting>>(new ArrayList<Lighting>());
		dynamicEntities = new StateVariable<HashSet<DynamicEntity>>(new HashSet<DynamicEntity>());
	}

}
