#version 120

varying vec3 Normal;

void main()
{
    gl_FrontColor = gl_Color;
    Normal = gl_NormalMatrix * gl_Normal;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}