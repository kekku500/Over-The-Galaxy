#version 120

uniform sampler2D Texture, NormalMap;
uniform int Texturing, NormalMapping;

varying vec3 Tangent, Binormal, Normal;

void main()
{
    gl_FragData[0] = gl_Color;
    
    //material stuff
    
    //gl_FragData[0] *= gl_FrontMaterial.diffuse;
    
    if(Texturing == 1){
    	gl_FragData[0] *= texture2D(Texture, gl_TexCoord[0].st);
    }
    
    
    if(NormalMapping == 1){
    	vec3 NormalMapNormal = normalize(texture2D(NormalMap, gl_TexCoord[0].st).rgb * 2.0 - 1.0);

		NormalMapNormal = mat3x3(Tangent, Binormal, Normal) * NormalMapNormal;

		gl_FragData[1] = vec4(NormalMapNormal * 0.5 + 0.5, 1.0);
    }else{
    	gl_FragData[1] = vec4(normalize(Normal) * 0.5 + 0.5, 1.0);
    }
}