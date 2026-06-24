#version 330 core

in vec2 texCoords;
in vec3 positionToFS;

layout(location = 0) out vec4 fragColor;

uniform sampler2D tex;
uniform vec2 player;

void main(){
	vec4 texColor = texture(tex, texCoords);
	fragColor = texColor;

	if(fragColor == vec4(0.0, 0.0, 0.0, 1.0)) discard;

	//LIGHT AROUND THE PLAYER
	fragColor *= 20.0 / (length(player - positionToFS.xy) + 2) + 0.3;
}