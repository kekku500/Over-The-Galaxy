varying vec4 vColor;
uniform mat4 mView;
uniform mat4 mProjection;
uniform mat4 mNormal;

void main(){
	vColor = gl_Color;
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	gl_Position = mProjection * mView * gl_Vertex;
}