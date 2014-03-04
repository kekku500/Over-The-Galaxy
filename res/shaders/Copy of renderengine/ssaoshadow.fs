#version 120

uniform sampler2D NormalBuffer, DepthBuffer, RotationTexture;
//uniform sampler2DArrayShadow ShadowCubeMap;
uniform mat4x4 ProjectionBiasInverse; //, ViewInverse, LightTexture[6];
uniform vec2 Samples[16];

void main()
{
    float Depth = texture2D(DepthBuffer, gl_TexCoord[0].st).r;

    if(Depth < 1.0)
    {
        // screen space ambient occlusion -------------------------------------------------------------------------------------
        
        vec3 Normal = normalize(texture2D(NormalBuffer, gl_TexCoord[0].st).rgb * 2.0 - 1.0);

        vec4 Position = ProjectionBiasInverse * vec4(gl_TexCoord[0].st, Depth, 1.0);
        Position.xyz /= Position.w;
        
        if(dot(Normal, Position.xyz) > 0.0){
        	//Normal = -Normal;
        	Normal *= -1.0;
		}
		
        //float Distance = length(Position.xyz);

        //vec2 sr = normalize(texture2D(RotationTexture, gl_TexCoord[1].st).rg * 2.0 - 1.0) / min(Distance, 4.0);
        vec2 sr = normalize(texture2D(RotationTexture, gl_TexCoord[1].st).rg * 2.0 - 1.0) / length(Position.xyz);
        
        mat2x2 ScaleRotationMatrix = mat2x2(sr.x, sr.y, -sr.y, sr.x);
        
        float SSAO = 0.0;

        for(int i = 0; i < 16; i++)
        {
            //vec2 TexCoord = clamp(ScaleRotationMatrix * Samples[i] + gl_TexCoord[0].st, 0.0, 0.9995);
            vec2 TexCoord = clamp(ScaleRotationMatrix * Samples[i] + gl_TexCoord[0].st, 0.0, 0.999999);
            
            vec4 position = ProjectionBiasInverse * vec4(TexCoord, texture2D(DepthBuffer, TexCoord).r, 1.0);
            position.xyz /= position.w;

            vec3 p2p = position.xyz - Position.xyz;
            
            float Distance2 = dot(p2p, p2p);
            
            //SSAO += max(dot(Normal, p2p), 0.0) / (sqrt(Distance2) * (1.0 + Distance2));
            float Weight = 1.0 - Distance2 * 0.25;
			if(Weight > 0.0){
				p2p /= sqrt(Distance2);

				float NdotP2P = dot(Normal, p2p);

				if(NdotP2P > 0.342){
					SSAO += NdotP2P * Weight;
				}
			}
        }
        
        gl_FragColor = vec4(vec3(1.0 - SSAO * 0.0625), 1.0);
        
    }else{
        //gl_FragColor = vec4(vec3(0.0), 1.0);
        gl_FragColor = vec4(1.0);
    }
}