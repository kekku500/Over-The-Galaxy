varying vec4 vColor;

uniform sampler2D texture;

void main(){
	vec4 texColor = texture2D(texture, gl_TexCoord[0].st);
	
	gl_FragColor = vec4(tecColor.rgb * vColor.rgb, tecColor.a * vColor.a);
	}