#version 120

varying vec3 Normal;

void main()
{
    gl_FragData[0] = gl_Color;
    gl_FragData[1] = vec4(normalize(Normal) * 0.5 + 0.5, 1.0);
}