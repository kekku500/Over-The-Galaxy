#version 120

uniform sampler2D SSAOTexture, DepthBuffer;
uniform float sy, sy2, sy3, fs, fd;

void main()
{
	float Depth = texture2D(DepthBuffer, gl_TexCoord[0].st).r;

	float Factor = fs - fd * Depth;

	float SSAO = texture2D(SSAOTexture, gl_TexCoord[0].st).r * 4.0;
	float Sum = 4.0;

	vec2 TexCoord = gl_TexCoord[0].st;

	TexCoord.t -= sy3;

	float weight = 1.0 - step(Factor, abs(Depth - texture2D(DepthBuffer, TexCoord).r));
	SSAO += texture2D(SSAOTexture, TexCoord).r * weight;
	Sum += weight;

	TexCoord.t += sy;

	weight = 2.0 * (1.0 - step(Factor, abs(Depth - texture2D(DepthBuffer, TexCoord).r)));
	SSAO += texture2D(SSAOTexture, TexCoord).r * weight;
	Sum += weight;

	TexCoord.t += sy;

	weight = 3.0 * (1.0 - step(Factor, abs(Depth - texture2D(DepthBuffer, TexCoord).r)));
	SSAO += texture2D(SSAOTexture, TexCoord).r * weight;
	Sum += weight;

	TexCoord.t += sy2;

	weight = 3.0 * (1.0 - step(Factor, abs(Depth - texture2D(DepthBuffer, TexCoord).r)));
	SSAO += texture2D(SSAOTexture, TexCoord).r * weight;
	Sum += weight;

	TexCoord.t += sy;

	weight = 2.0 * (1.0 - step(Factor, abs(Depth - texture2D(DepthBuffer, TexCoord).r)));
	SSAO += texture2D(SSAOTexture, TexCoord).r * weight;
	Sum += weight;

	TexCoord.t += sy;

	weight = 1.0 - step(Factor, abs(Depth - texture2D(DepthBuffer, TexCoord).r));
	SSAO += texture2D(SSAOTexture, TexCoord).r * weight;
	Sum += weight;

	gl_FragColor = vec4(vec3(SSAO / Sum), 1.0);
}
