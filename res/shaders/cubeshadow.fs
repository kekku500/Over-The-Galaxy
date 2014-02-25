#version 120

#extension GL_EXT_texture_array : enable

uniform sampler2D Texture;
uniform sampler2DArrayShadow ShadowCubeMap;

uniform int Texturing;
uniform mat4x4 LightTexture[6];

varying vec3 Position, Normal;

void main()
{
    vec3 LightDirection = gl_LightSource[0].position.xyz - Position;
    float LightDistance2 = dot(LightDirection, LightDirection);
    float LightDistance = sqrt(LightDistance2);
    LightDirection /= LightDistance;
    float Attenuation = gl_LightSource[0].constantAttenuation;
    Attenuation += gl_LightSource[0].linearAttenuation * LightDistance;
    Attenuation += gl_LightSource[0].quadraticAttenuation * LightDistance2;
    
    float Axis[6];

    Axis[0] = -LightDirection.x;
    Axis[1] = LightDirection.x;
    Axis[2] = -LightDirection.y;
    Axis[3] = LightDirection.y;
    Axis[4] = -LightDirection.z;
    Axis[5] = LightDirection.z;
    
    int MaxAxisID = 0;
    
    for(int i = 1; i < 6; i++)
    {
        if(Axis[i] > Axis[MaxAxisID])
        {
            MaxAxisID = i;
        }
    }
    
    vec4 ShadowTexCoord = LightTexture[MaxAxisID] * vec4(Position, 1.0);
    ShadowTexCoord.xyz /= ShadowTexCoord.w;
    
    ShadowTexCoord.w = ShadowTexCoord.z;
    ShadowTexCoord.z = float(MaxAxisID);
    
    float Shadow = shadow2DArray(ShadowCubeMap, ShadowTexCoord).r;
    float NdotLD = max(dot(normalize(Normal), LightDirection), 0.0) * Shadow;
    vec3 Light = (gl_LightSource[0].ambient.rgb + gl_LightSource[0].diffuse.rgb * NdotLD) / Attenuation;
    
    gl_FragColor.rgb = gl_Color.rgb;
    if(Texturing == 1) gl_FragColor.rgb *= texture2D(Texture, gl_TexCoord[0].st).rgb;
    gl_FragColor.rgb *= Light;
}