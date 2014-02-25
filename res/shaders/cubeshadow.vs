#version 120

uniform mat4x4 Model;

varying vec3 Position, Normal;

void main()
{
    Position = (Model * gl_Vertex).xyz;
    Normal = gl_Normal;
    gl_FrontColor = gl_Color;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * vec4(Position, 1.0);
}