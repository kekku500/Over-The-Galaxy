#version 120

attribute vec3 vert_Tangent;
attribute vec3 Material_Ambient, Material_Diffuse, Material_Specular;
attribute vec4 Material_Emission_Shininess;

varying vec3 Tangent, Binormal, Normal, varyAmbient, varyDiffuse, varySpecular;
varying vec4 varyEmissionAndShininess;

void main(){
	Tangent = gl_NormalMatrix * vert_Tangent;
	Normal = gl_NormalMatrix * gl_Normal;
	Binormal = cross(Normal, Tangent);
    gl_FrontColor = gl_Color;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	varyAmbient = Material_Ambient;
	varyDiffuse = Material_Diffuse;
	varySpecular = Material_Specular;
	varyEmissionAndShininess = Material_Emission_Shininess;

}