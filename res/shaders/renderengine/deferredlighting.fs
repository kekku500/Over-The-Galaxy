#extension GL_EXT_texture_array : enable

uniform sampler2D ColorBuffer, NormalBuffer, DepthBuffer;
uniform mat4x4 ProjectionBiasInverse;

//Light source
uniform vec3 LightSourcePosition, LightSourceNormal;
uniform vec4 LightSourceAmbient, LightSourceDiffuse, LightSourceSpecular;
uniform float LightSourceConstantAttenuation, LightSourceLinearAttenuation, LightSourceQuadricAttenuation;
uniform float LightSourceSpotCutoff, LightSourceSpotExponent;
uniform vec3 LightSourceSpotLightDirection;
uniform int LightSourceType; //0 - directional, 1 - point, 2 - spot

uniform mat3x3 NormalMatrix;
uniform mat4x4 ProjectionMatrix, ModelViewMatrix;

void main(){
	
    float Depth = texture2D(DepthBuffer, gl_TexCoord[0].st).r;
    
    if(Depth < 1.0){
    	vec4 Position = ProjectionBiasInverse * vec4(gl_TexCoord[0].st, Depth, 1.0);
        Position.xyz /= Position.w;
       	
        vec3 LightDirection = LightSourcePosition - Position.xyz; 
		
    	//lighting -------------------------------------
		int Lighting = 1;
		vec4 Color = texture2D(ColorBuffer, gl_TexCoord[0].st); //contains ambient color of the model
		if(Lighting == 1){ //Directional || Point || Spot
			vec3 Normal = normalize(texture2D(NormalBuffer, gl_TexCoord[0].st).rgb * 2.0 - 1.0);
	
			float LightDistance2 = dot(LightDirection, LightDirection);
			float LightDistance = sqrt(LightDistance2);
			LightDirection /= LightDistance;
			vec3 LightDirectionReflected = reflect(-LightDirection, Normal);
			float LightIntensity = 1.0;
			if(LightSourceType == 1){ //POINT LIGHT ------------------------------------------------------------
				LightIntensity = max(-dot(LightDirection, LightSourceNormal), 0.0) / (LightSourceConstantAttenuation + LightSourceLinearAttenuation * LightDistance + LightSourceQuadricAttenuation * LightDistance2);
			}
			vec3 ambient = LightSourceAmbient.rgb;
			float NdotLD = max(dot(LightDirection, Normal), 0.0) * LightIntensity;
			vec3 diffuse = LightSourceDiffuse.rgb * NdotLD;
	
			float CDdotLDR = 0.0;
			if(NdotLD > 0.0){
				CDdotLDR = pow(max(dot(normalize(-Position.xyz), LightDirectionReflected), 0.0), 128) * LightIntensity;
			}
			vec3 spec = LightSourceSpecular.rgb * CDdotLDR;
			
			if(LightSourceType == 2){ //SPOT LIGHT -------------------------------------------------------------
				vec4 spotDir = ModelViewMatrix * vec4(LightSourceSpotLightDirection, 1.0);
				
				vec3 D = normalize(spotDir.xyz);
				
				float cos_cur_angle = dot(-LightDirection, D);
				float cos_inner_cone_angle = LightSourceSpotCutoff;
				float cos_outer_cone_angle = LightSourceSpotCutoff - LightSourceSpotExponent;
				
				float cos_inner_minus_outer_angle = 
				cos_inner_cone_angle - cos_outer_cone_angle;

				float falloff = 1.0;
				if (cos_cur_angle > cos_inner_cone_angle || cos_cur_angle > cos_outer_cone_angle) {
					if(cos_cur_angle > cos_outer_cone_angle){
						falloff = (cos_cur_angle - cos_outer_cone_angle) / 
						cos_inner_minus_outer_angle;
					}
					if(NdotLD > 0.0){
						diffuse *= falloff;
						spec *= falloff;
					}else{
						diffuse = vec3(0.0);
						spec = vec3(0.0);
					}
				}else{
					diffuse = vec3(0.0);
					spec = vec3(0.0);
				}		
			}
			gl_FragColor = vec4(Color.rgb * (ambient + diffuse) + spec, 1.0);				
    	}else{ //lighting disabled
			gl_FragColor = vec4(Color.rgb, 1.0);		
		}
    }else{
    	gl_FragColor = vec4(vec3(0.0), 1.0);
    }
}