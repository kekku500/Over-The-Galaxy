#version 120

uniform sampler2D ColorBuffer, NormalBuffer, DepthBuffer;
uniform mat4x4 ProjectionBiasInverse;

void main()
{
    gl_FragColor = texture2D(ColorBuffer, gl_TexCoord[0].st);
    
    float Depth = texture2D(DepthBuffer, gl_TexCoord[0].st).r;
    
    if(Depth < 1.0)
    {
        vec3 Normal = normalize(texture2D(NormalBuffer, gl_TexCoord[0].st).rgb * 2.0 - 1.0);
        
        vec4 Position = ProjectionBiasInverse * vec4(gl_TexCoord[0].st, Depth, 1.0);
        Position /= Position.w;
        
        vec3 Light = vec3(0.0);
        
        for(int i = 0; i < 4; i++)
        {
            vec3 LightDirection = gl_LightSource[i].position.xyz - Position.xyz;
            
            float LightDistance2 = dot(LightDirection, LightDirection);
            float LightDistance = sqrt(LightDistance2);
            
            LightDirection /= LightDistance;
            
            float NdotLD = max(0.0, dot(Normal, LightDirection));
            
            float att = gl_LightSource[i].constantAttenuation;
            
            att += gl_LightSource[i].linearAttenuation * LightDistance;
            att += gl_LightSource[i].quadraticAttenuation * LightDistance2;
            
            Light += (gl_LightSource[i].ambient.rgb + gl_LightSource[i].diffuse.rgb * NdotLD) / att;
        }
        
        gl_FragColor.rgb *= Light;
    }
}