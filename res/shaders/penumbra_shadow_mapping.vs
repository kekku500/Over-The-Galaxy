#version 120

uniform vec3 LightPosition, CameraPosition;
uniform mat4x4 LightTextureMatrices[5];

varying vec3 LightDirection, LightDirectionReflected, CameraDirection, Normal;
varying vec4 ShadowTexCoord[5];

void main()
{
	LightDirection = LightPosition - gl_Vertex.xyz;
	LightDirectionReflected = reflect(-LightDirection, gl_Normal);
	CameraDirection = CameraPosition - gl_Vertex.xyz;
	Normal = gl_Normal;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	for(int i = 0; i < 5; i++) ShadowTexCoord[i] = LightTextureMatrices[i] * gl_Vertex;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
