#version 120

uniform sampler2D ColorBuffer;
uniform int Samples;
uniform float dx, dy;

void main (void)
{
    vec3 Color = vec3(0.0);
    
    for(int y = 0; y < Samples; y++)
    {
        for(int x = 0; x < Samples; x++)
        {
            Color += texture2D(ColorBuffer, gl_TexCoord[0].st + vec2(dx * x, dy * y)).rgb;
        }
    }
    
    Color /= (Samples * Samples);

    gl_FragColor = vec4(Color, 1.0);
}