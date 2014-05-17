#version 130

uniform samplerCube CubeMap;

in vec3 TexCoord;

out vec4 frag_Color;

void main()
{
	//frag_Color = texture(CubeMap, TexCoord);
	gl_FragData[0] = texture(CubeMap, TexCoord);
	
	gl_FragData[2] = vec4(0.0);
    gl_FragData[3] = vec4(0.0);
    gl_FragData[4] = vec4(0.0);
    gl_FragData[5] = vec4(0.0);
    gl_FragData[6] = vec4(0.0);
	gl_FragData[7] = vec4(0.0);


}