#extension GL_EXT_texture_array : enable

const int MAX_LIGHTS = 10;

uniform sampler2D ColorBuffer, NormalBuffer, DepthBuffer, SSAOTexture, RotationTexture;
uniform sampler2DArrayShadow ShadowCubeMap; ///---------------
uniform sampler2DShadow ShadowMap;
uniform mat4x4 ProjectionBiasInverse, ViewInverse, LightTexture[7];  ///---------------
uniform vec2 Samples[16];
uniform int Shadows, Filtering, Occlusion;

uniform sampler2D MaterialAmbient, MaterialDiffuse, MaterialSpecular, MaterialEmission, MaterialShininess;

//Light source
uniform vec3 LightSourcePosition[MAX_LIGHTS], LightSourceNormal;
uniform vec4 LightSourceAmbient[MAX_LIGHTS], LightSourceDiffuse[MAX_LIGHTS], LightSourceSpecular[MAX_LIGHTS];
uniform float LightSourceConstantAttenuation[MAX_LIGHTS], LightSourceLinearAttenuation[MAX_LIGHTS], LightSourceQuadricAttenuation[MAX_LIGHTS];
uniform float LightSourceSpotCutoff[MAX_LIGHTS], LightSourceSpotExponent[MAX_LIGHTS];
uniform vec3 LightSourceSpotLightDirection[MAX_LIGHTS];
uniform int LightSourceType[MAX_LIGHTS]; //0 - directional, 1 - point, 2 - spot
uniform int LightCount;
uniform int CubeShadowedLight; ///---------------
uniform int DirectionalShadowEnabled; //---------------

uniform float SkyBoxIntensity;

uniform mat3x3 NormalMatrix;
uniform mat4x4 ProjectionMatrix, ModelViewMatrix;

void main(){
	
    float Depth = texture2D(DepthBuffer, gl_TexCoord[0].st).r;
	
	vec4 SunAmbient = vec4(0,0,0,1);
	vec4 SunDiffuse = vec4(0,0,0,1);
	for(int i = 0; i < LightCount; i++){
		if(LightSourceType[i] == 0){ 
			SunAmbient = LightSourceAmbient[i];
			SunDiffuse = LightSourceDiffuse[i];
			break;
		}
	}
    
    if(Depth < 1.0){
    	vec4 Position = ProjectionBiasInverse * vec4(gl_TexCoord[0].st, Depth, 1.0);
        Position.xyz /= Position.w;
		
		    	
    	// ssao ---------------------------------------------------------------------------------------------------------------
		float SSAO = 1.0;
		if(Occlusion == 1){
			SSAO = texture2D(SSAOTexture, gl_TexCoord[0].st).r;
		}
		
		vec4 Ma = texture2D(MaterialAmbient, gl_TexCoord[0].st);
		vec4 Md = texture2D(MaterialDiffuse, gl_TexCoord[0].st);
		vec4 MSpec = texture2D(MaterialSpecular, gl_TexCoord[0].st);
		vec4 Me = texture2D(MaterialEmission, gl_TexCoord[0].st); //not using
		float MShi = texture2D(MaterialShininess, gl_TexCoord[0].st).r; //not working for some reason
		
		vec4 Color = texture2D(ColorBuffer, gl_TexCoord[0].st); //contains ambient color of the model
		vec3 Normal = normalize(texture2D(NormalBuffer, gl_TexCoord[0].st).rgb * 2.0 - 1.0);
		
		//find shadow
		// shadows ------------------------------------------------------------------------------------------------------------
		//normal

		float DirShadow = 1.0;
		float CubeShadow = 1.0;
		if(Shadows == 1){
			if(DirectionalShadowEnabled == 1){
				vec4 ShadowTexCoord = LightTexture[0] * vec4(Position.xyz, 1.0);

				if(Filtering == 1){
					vec2 r = normalize(texture2D(RotationTexture, gl_TexCoord[1].st).rg * 2.0 - 1.0);

					mat2x2 RotationMatrix = mat2x2(r.x, r.y, -r.y, r.x);

					DirShadow = 0.0;

					for(int n = 0; n < 16; n++){
						DirShadow += shadow2DProj(ShadowMap, ShadowTexCoord + vec4(RotationMatrix * Samples[n] * ShadowTexCoord.w, -0.0009765625 * ShadowTexCoord.w, 0.0)).r;
					}

					DirShadow *= 0.0625;
				}else{
					ShadowTexCoord.z -= 0.0009765625 * ShadowTexCoord.w;

					DirShadow = shadow2DProj(ShadowMap, ShadowTexCoord).r;
				};
			}

			if(CubeShadowedLight != -1){
				vec3 LightDirectionCube = LightSourcePosition[CubeShadowedLight] - Position.xyz; 
			
				int MaxAxisID = 0;

				vec3 CubeShadowLightDirection = (ViewInverse * vec4(LightDirectionCube, 0.0)).xyz;
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

				vec4 ShadowTexCoord = LightTexture[1+MaxAxisID] * vec4(Position.xyz, 1.0);
				ShadowTexCoord.xyz /= ShadowTexCoord.w;
				ShadowTexCoord.w = ShadowTexCoord.z;
				ShadowTexCoord.z = float(MaxAxisID);

				if(Filtering == 1){
					vec2 r = normalize(texture2D(RotationTexture, gl_TexCoord[1].st).rg * 2.0 - 1.0);

					mat2x2 RotationMatrix = mat2x2(r.x, r.y, -r.y, r.x);

					CubeShadow = 0.0;

					for(int i = 0; i < 16; i++)
					{
						CubeShadow += shadow2DArray(ShadowCubeMap, ShadowTexCoord + vec4(RotationMatrix * Samples[i] * ShadowTexCoord.w, -0.0009765625 * ShadowTexCoord.w, 0.0)).r;
					}

					CubeShadow *= 0.0625;
				}else{
					CubeShadow = shadow2DArray(ShadowCubeMap, ShadowTexCoord).r;
				}
			}
			
    	}
		
		float Shadow = DirShadow + CubeShadow;
		
		vec4 TotalAmbient = vec4(0);
		vec4 TotalDiffuse = vec4(0);
		vec4 TotalSpecular = vec4(0);
		int pc = -1; //point light counter
		int slc = -1; //spot light counter
		for(int i = 0; i < LightCount; i++){
			//Get parameters
			vec4 La = LightSourceAmbient[i];
			vec4 Ld = LightSourceDiffuse[i];
			vec4 LSpec = LightSourceSpecular[i];
			
			//Calculate lighting stuff
			vec3 LightDirection = LightSourcePosition[i] - Position.xyz; 
			float LightDistance2 = dot(LightDirection, LightDirection);
			float LightDistance = sqrt(LightDistance2);
			LightDirection /= LightDistance;
			
			//Diffuse
			float NdotLD =  max(dot(LightDirection, Normal), 0.0);
			vec3 diffuse = Ld.rgb * NdotLD;
			
			//Ambient
			vec3 ambient =  La.rgb;
			
			//Specular
			vec3 LightDirectionReflected = reflect(-LightDirection, Normal);
			float CDdotLDR = 0.0;
			if(NdotLD > 0.0){
				CDdotLDR = pow(max(dot(normalize(-Position.xyz), LightDirectionReflected), 0.0), MShi * 128);
			}
			vec3 spec = LSpec.rgb * CDdotLDR;
				
			float LightIntensity = 1.0;
			if(LightSourceType[i] == 1){ //POINT LIGHT ------------------------------------------------------------
				pc += 1;
				float cAtt = LightSourceConstantAttenuation[pc];
				float lAtt = LightSourceLinearAttenuation[pc];
				float qAtt = LightSourceQuadricAttenuation[pc];
				
				LightIntensity = 1 / (cAtt + lAtt * LightDistance + qAtt * LightDistance2);
			}
			
			if(LightSourceType[i] == 2){ //SPOT LIGHT -------------------------------------------------------------
				slc += 1;
				vec4 spotDir = ModelViewMatrix * vec4(LightSourceSpotLightDirection[slc] , 1.0);
				
				vec3 D = normalize(spotDir.xyz);
				
				float cos_cur_angle = dot(-LightDirection, D);
				float cos_inner_cone_angle = LightSourceSpotCutoff[slc] ;
				float cos_outer_cone_angle = LightSourceSpotCutoff[slc] - LightSourceSpotExponent[slc];
				
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
			
			TotalAmbient += vec4(ambient * LightIntensity, 1.0);
			TotalDiffuse += vec4(diffuse * LightIntensity, 1.0);
			TotalSpecular += vec4(spec * LightIntensity, 1.0);
		}
		gl_FragColor = vec4(Color * (TotalAmbient * Ma * SSAO + TotalDiffuse * Md * Shadow) + TotalSpecular * MSpec * Shadow);	
    }else{
    	gl_FragColor = vec4(texture2D(ColorBuffer, gl_TexCoord[0].st).rgb * (SunAmbient.rgb + SunDiffuse.rgb)/*SkyBoxIntensity*/, 1.0);
    }
}