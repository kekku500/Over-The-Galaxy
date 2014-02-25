#version 120

uniform sampler2D Texture;

varying vec3 Normal;

void main()
{
    gl_FragData[0] = texture2D(Texture, gl_TexCoord[0].st);
    gl_FragData[1] = vec4(normalize(Normal) * 0.5 + 0.5, 1.0);
}