#extension GL_EXT_texture_array : enable

uniform sampler2D ColorBuffer, NormalBuffer, DepthBuffer, SSAOTexture, RotationTexture;
uniform sampler2DArrayShadow ShadowCubeMap;
uniform mat4x4 ProjectionBiasInverse, ViewInverse, LightTexture[6];
uniform vec2 Samples[16];
uniform int Shadows, Filtering, Occlusion, CubeLight;

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
       	
        //vec3 LightDirection = gl_LightSource[0].position.xyz - Position.xyz; 
        vec3 LightDirection = LightSourcePosition - Position.xyz; 
        
        // shadows ------------------------------------------------------------------------------------------------------------
    	float Shadow = 1.0;
    	if(Shadows == 1){
    		int MaxAxisID = 0;
    		if(CubeLight == 1){
	    		vec3 CubeShadowLightDirection = (ViewInverse * vec4(LightDirection, 0.0)).xyz;
	        	float Axis[6];
	
	        	Axis[0] = -CubeShadowLightDirection.x;
	        	Axis[1] = CubeShadowLightDirection.x;
	        	Axis[2] = -CubeShadowLightDirection.y;
	        	Axis[3] = CubeShadowLightDirection.y;
	        	Axis[4] = -CubeShadowLightDirection.z;
	        	Axis[5] = CubeShadowLightDirection.z;
	                
	        	for(int i = 1; i < 6; i++)
	        	{
	            	if(Axis[i] > Axis[MaxAxisID])
	            	{
	                	MaxAxisID = i;
	            	}
	        	}
    		}


        	vec4 ShadowTexCoord = LightTexture[MaxAxisID] * vec4(Position.xyz, 1.0);
        	ShadowTexCoord.xyz /= ShadowTexCoord.w;
        	ShadowTexCoord.w = ShadowTexCoord.z;
			ShadowTexCoord.z = float(MaxAxisID);

        	if(Filtering == 1){
        	
        		vec2 r = normalize(texture2D(RotationTexture, gl_TexCoord[1].st).rg * 2.0 - 1.0);

				mat2x2 RotationMatrix = mat2x2(r.x, r.y, -r.y, r.x);

				Shadow = 0.0;

				for(int i = 0; i < 16; i++)
				{
					Shadow += shadow2DArray(ShadowCubeMap, ShadowTexCoord + vec4(RotationMatrix * Samples[i] * ShadowTexCoord.w, -0.0009765625 * ShadowTexCoord.w, 0.0)).r;
				}

				Shadow *= 0.0625;
			}else{
				Shadow = shadow2DArray(ShadowCubeMap, ShadowTexCoord).r;
			}
			
    	}
    	
    	// ssao ---------------------------------------------------------------------------------------------------------------

		float SSAO = 1.0;
		if(Occlusion == 1){
			SSAO = texture2D(SSAOTexture, gl_TexCoord[0].st).r;
		}

		//out_Color = texture2DArray(myTextureSampler, vec3(UV, index)).rgb;
		
		
    	//lighting -------------------------------------
    	if(LightSourceType == 0){ //Directional
	    	vec3 Normal = normalize(texture2D(NormalBuffer, gl_TexCoord[0].st).rgb * 2.0 - 1.0);
	
			float LightDistance2 = dot(LightDirection, LightDirection);
			float LightDistance = sqrt(LightDistance2);
			LightDirection /= LightDistance;
			vec3 LightDirectionReflected = reflect(-LightDirection, Normal);
			
			vec3 ambient = LightSourceAmbient.rgb /**Material.ambient*/;
			float NdotLD = max(dot(LightDirection, Normal), 0.0) * Shadow;
			vec3 diffuse = LightSourceDiffuse.rgb /** Material.diffuse */ * NdotLD;
	
			float CDdotLDR = 0.0;
	
			vec4 Color = texture2D(ColorBuffer, gl_TexCoord[0].st);
	
			if(NdotLD > 0.0){
				CDdotLDR = pow(max(dot(normalize(-Position.xyz), LightDirectionReflected), 0.0), Color.a * 128/*Material.shininess*/) * Shadow;
			}
			
			vec3 spec = LightSourceSpecular.rgb /**Material.spec*/ * CDdotLDR;
	
			gl_FragColor = vec4(Color.rgb * (ambient * SSAO + diffuse) + spec, 1.0);
    	}else if(LightSourceType == 1){ //POINT
    		vec3 Normal = normalize(texture2D(NormalBuffer, gl_TexCoord[0].st).rgb * 2.0 - 1.0);
	
			float LightDistance2 = dot(LightDirection, LightDirection);
			float LightDistance = sqrt(LightDistance2);
			LightDirection /= LightDistance;
			vec3 LightDirectionReflected = reflect(-LightDirection, Normal);
			float LightIntensity = max(-dot(LightDirection, LightSourceNormal), 0.0) / (LightSourceConstantAttenuation + LightSourceLinearAttenuation * LightDistance + LightSourceQuadricAttenuation * LightDistance2);
			vec3 ambient = LightSourceAmbient.rgb /**Material.ambient*/;
			float NdotLD = max(dot(LightDirection, Normal), 0.0) * Shadow * LightIntensity;
			vec3 diffuse = LightSourceDiffuse.rgb /** Material.diffuse */ * NdotLD;
			float CDdotLDR = 0.0;
	
			vec4 Color = texture2D(ColorBuffer, gl_TexCoord[0].st);
	
			if(NdotLD > 0.0){
				CDdotLDR = pow(max(dot(normalize(-Position.xyz), LightDirectionReflected), 0.0), Color.a * 128/*Material.shininess*/) * Shadow * LightIntensity;
			}
			
			vec3 spec = LightSourceSpecular.rgb /**Material.spec*/ * CDdotLDR;
	
			gl_FragColor = vec4(Color.rgb * (ambient * SSAO + diffuse) + spec, 1.0);
    	}else if(LightSourceType == 2){ //SPOT - currently dynamic
    	    vec3 Normal = normalize(texture2D(NormalBuffer, gl_TexCoord[0].st).rgb * 2.0 - 1.0);
	
			float LightDistance2 = dot(LightDirection, LightDirection);
			float LightDistance = sqrt(LightDistance2);
			LightDirection /= LightDistance;
			vec3 LightDirectionReflected = reflect(-LightDirection, Normal);
			vec3 ambient = LightSourceAmbient.rgb /**Material.ambient*/;
			float NdotLD = max(dot(LightDirection, Normal), 0.0) * Shadow;
			vec3 diffuse = LightSourceDiffuse.rgb /** Material.diffuse */;
			float CDdotLDR = 0.0;
	
			vec4 Color = texture2D(ColorBuffer, gl_TexCoord[0].st);
			if(NdotLD > 0.0){
				CDdotLDR = pow(max(dot(normalize(-Position.xyz), LightDirectionReflected), 0.0), Color.a * 128/*Material.shininess*/) * Shadow;
			}
			vec3 spec = LightSourceSpecular.rgb /**Material.spec*/ * CDdotLDR;
			
			//------------------------------
		    vec4 spotDir = ModelViewMatrix * vec4(LightSourceSpotLightDirection, 1.0);
		    
			vec4 final_color = 
			/*(gl_FrontLightModelProduct.sceneColor * gl_FrontMaterial.ambient) + */
			(LightSourceAmbient * SSAO /** gl_FrontMaterial.ambient*/);
									
			vec3 L = normalize(LightDirection.xyz);
			vec3 D = normalize(spotDir.xyz);
			
			float cos_cur_angle = dot(-L, D);
			
			float cos_inner_cone_angle = LightSourceSpotCutoff;
			float cos_outer_cone_angle = LightSourceSpotCutoff - LightSourceSpotExponent;
			
			float cos_inner_minus_outer_angle = 
			cos_inner_cone_angle - cos_outer_cone_angle;
			vec4 specular = vec4(0.0);
			if (cos_cur_angle > cos_inner_cone_angle) {
				vec3 N = Normal;
			
				float lambertTerm = max( dot(N,L), 0.0) * Shadow;
				if(lambertTerm > 0.0){
					final_color += LightSourceDiffuse * 
					/*gl_FrontMaterial.diffuse * */
					lambertTerm;	
				
					vec3 E = normalize(-Position.xyz);
					vec3 R = reflect(-L, N);
					
					specular += pow( max(dot(R, E), 0.0), 
					/*gl_FrontMaterial.shininess*/128 ) * Shadow;
					
					specular *= LightSourceSpecular;
					
					//final_color += LightSourceSpecular * 
					/*gl_FrontMaterial.specular * */
					//specular;	
				}
			}else if( cos_cur_angle > cos_outer_cone_angle ){
				float falloff = (cos_cur_angle - cos_outer_cone_angle) / 
				cos_inner_minus_outer_angle;
				
				vec3 N = Normal;
			
				float lambertTerm = max( dot(N,L), 0.0) * Shadow;
				
				if(lambertTerm > 0.0){
					final_color += LightSourceDiffuse * 
					/*gl_FrontMaterial.diffuse * */
					lambertTerm * falloff;	
				
					vec3 E = normalize(-Position.xyz);
					vec3 R = reflect(-L, N);
					
					specular += pow( max(dot(R, E), 0.0), 
					/*gl_FrontMaterial.shininess*/128 ) * Shadow;
					
					specular *= LightSourceSpecular * falloff;
					
					//final_color += LightSourceSpecular * 
					/*gl_FrontMaterial.specular * */
					//specular * falloff;	
				}
			}
			
			gl_FragColor = Color * final_color + specular;			
    	}

		
		
		///OLD
		/*   	
		vec3 Normal = normalize(texture2D(NormalBuffer, gl_TexCoord[0].st).rgb * 2.0 - 1.0);

		float LightDistance2 = dot(LightDirection, LightDirection);
		float LightDistance = sqrt(LightDistance2);
		LightDirection /= LightDistance;
		vec3 LightDirectionReflected = reflect(-LightDirection, Normal);
		float LightIntensity = max(-dot(LightDirection, LightSourceNormal), 0.0) / 1.0;//(1.0 + 0.0625 * LightDistance + 0.03125 * LightDistance2);
		
		float NdotLD = max(dot(LightDirection, Normal), 0.0) * LightIntensity * Shadow;

		float CDdotLDR = 0.0;

		vec4 Color = texture2D(ColorBuffer, gl_TexCoord[0].st);

		if(Color.a > 0.0)
		{
			CDdotLDR = pow(max(dot(normalize(-Position.xyz), LightDirectionReflected), 0.0), Color.a * 128) * Shadow * LightIntensity;
		}

		gl_FragColor = vec4(Color.rgb * (0.33 * SSAO + 0.66 * NdotLD) + vec3(CDdotLDR), 1.0);
		*/
    }else{
    	gl_FragColor = vec4(vec3(0.0), 1.0);
    }
}