#version 330 core

in vec2 texCoords;

layout(location = 0) out vec4 fragColor;

uniform sampler2D tex;

void main(){
	fragColor = texture(tex, texCoords);
	if(fragColor.w < 0.1) discard;
	fragColor *= vec4(0.5, 0.1, 0.1, 1.0);
}