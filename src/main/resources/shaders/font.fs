#version 330 core

in vec2 texCoords;

layout(location = 0) out vec4 fragColor;

uniform sampler2D tex;
uniform vec3 colorIN;
uniform float blend;

void main(){
	vec4 color = texture(tex, texCoords);
	fragColor = color;
	if(fragColor.w < 0.1) discard;
	fragColor = vec4(colorIN, blend);
}