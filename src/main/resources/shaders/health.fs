#version 330 core

in vec2 texCoords;

layout(location = 0) out vec4 fragColor;

uniform sampler2D tex;
uniform float healthIndicator;

void main(){
	fragColor = texture(tex, texCoords) * vec4(healthIndicator, healthIndicator, healthIndicator, 1.0);
}