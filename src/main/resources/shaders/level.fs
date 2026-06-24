#version 120

varying vec2 texCoords;
varying vec3 positionToFS;

uniform sampler2D tex;
uniform vec2 player;

void main(){
	vec4 texture = texture2D(tex, texCoords);
	gl_FragColor = texture;

	if(gl_FragColor == vec4(0.0, 0.0, 0.0, 1.0)) discard;
	//if(gl_FragColor.w < 1.0) discard;

	//LIGHT AROUND THE PLAYER
	gl_FragColor *= 20.0 / (length(player - positionToFS.xy) + 2) + 0.3;

}