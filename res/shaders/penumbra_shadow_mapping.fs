#version 120

#extension GL_EXT_texture_array : enable

uniform sampler2D Texture;
uniform sampler2DArrayShadow ShadowMap;

uniform int Texturing;

varying vec3 LightDirection, LightDirectionReflected, CameraDirection, Normal;
varying vec4 ShadowTexCoord[5];

void main()
{
	float Shadow = 0.0;
	
	for(int i = 0; i < 5; i++)
	{
		Shadow += shadow2DArray(ShadowMap, vec4(ShadowTexCoord[i].xy / ShadowTexCoord[i].w, i, ShadowTexCoord[i].z / ShadowTexCoord[i].w)).r;
	}
	
	Shadow /= 5.0;

	float NdotLD = max(dot(normalize(LightDirection), Normal), 0.0) * Shadow;
	float Spec = pow(max(dot(normalize(LightDirectionReflected), normalize(CameraDirection)), 0.0), 32.0) * Shadow;
	
	//Current
	gl_FragColor.rgb = gl_FrontMaterial.diffuse.rgb;
	if(Texturing == 1) gl_FragColor *= vec4(texture2D(Texture, gl_TexCoord[0].st).rgb, 1.0);
	gl_FragColor.rgb *= (0.25 + NdotLD * 0.75 + Spec);
}
