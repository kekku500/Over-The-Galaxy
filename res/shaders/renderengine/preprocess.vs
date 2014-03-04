#version 120

attribute vec3 vert_Tangent;

varying vec3 Tangent, Binormal, Normal;

void main(){
	Tangent = gl_NormalMatrix * vert_Tangent;
	Normal = gl_NormalMatrix * gl_Normal;
	Binormal = cross(Normal, Tangent);
    gl_FrontColor = gl_Color;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}