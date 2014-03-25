package blender.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import utils.math.Matrix4f;
import utils.math.Vector3f;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.util.ObjectArrayList;

public class ModelUtils {
	
	public static ConvexHullShape getConvexHull(Model m, Matrix4f scaleRotationMatrix){
		ObjectArrayList<javax.vecmath.Vector3f> vex = new ObjectArrayList<javax.vecmath.Vector3f>();
		for(Vector3f v: m.vertices){
			Vector3f a = v.copy();
				if(scaleRotationMatrix != null)
					a.mul(scaleRotationMatrix);
			vex.add(a);
		}
		ConvexHullShape shape = new ConvexHullShape(vex);
		shape.setMargin(.2f);
		return shape;
	}
	
	public static ConvexHullShape getConvexHull(Model m){
		return getConvexHull(m, null);
	}
	
	public static IndexedMesh getStaticMesh(Model m, Matrix4f scaleRotationMatrix){
		IndexedMesh mesh = new IndexedMesh();
		
		int triangleIndexBaseSize = 0;
		int vertexBaseSize = 0;
		int numTrianglesSize = 0;
		for(SubModel subM: m.submodels){
			triangleIndexBaseSize += subM.faces.size() * 3 * 4;
			vertexBaseSize += subM.faces.size() * 3 * 3;
			numTrianglesSize += subM.faces.size();
		}
		
		mesh.numTriangles = numTrianglesSize; //count faces
		mesh.triangleIndexStride = 3 * 4;
		
		mesh.triangleIndexBase = BufferUtils.createByteBuffer(triangleIndexBaseSize); //indices
		mesh.vertexBase = BufferUtils.createByteBuffer(vertexBaseSize); //vertices
		
		//Transform and rotate points by offset matrix
		FloatBuffer vertices = BufferUtils.createFloatBuffer(m.vertices.size()*3);
		
		for(Vector3f a: (m.vertices)){
			Vector3f f = a.copy();
			if(scaleRotationMatrix != null)
				f.mul(scaleRotationMatrix);
			vertices.put((f.x)).put(f.y).put(f.z);
		}
		vertices.rewind();
		
		mesh.numVertices = vertices.capacity();
		mesh.vertexStride = 3 * 4;
		for(int i = 0; i < vertices.capacity(); i++){
			float tempFloat = vertices.get();
			mesh.vertexBase.putFloat(tempFloat);
		}
		
		for(SubModel subM: m.submodels){
			IntBuffer indices = subM.getVertexIndices();
			for(int i = 0; i < indices.capacity(); i++){
				int temp = indices.get();
				mesh.triangleIndexBase.putInt(temp);
			}
		}
		
		return mesh;
	}
	
	public static IndexedMesh getStaticMesh(Model m){
		return getStaticMesh(m, null);
	}
	
	public static CollisionShape getStaticCollisionShape(Model m, Matrix4f scaleRotationMatrix){
		TriangleIndexVertexArray meshInterface = new TriangleIndexVertexArray();
		
		meshInterface.addIndexedMesh(ModelUtils.getStaticMesh(m, scaleRotationMatrix));
		
		CollisionShape shape = new BvhTriangleMeshShape(meshInterface, true);
		
		return shape;
	}
	
	public static CollisionShape getStaticCollisionShape(Model m){
		return getStaticCollisionShape(m, null);
	}

}
