#version 330 core

in vec2 texCoords;
in vec3 positionToFS;

layout(location = 0) out vec4 fragColor;

uniform sampler2D tex;
uniform vec2 particlePosition;
uniform vec3 particleColor;
uniform float blendFactor;

void main(){
	fragColor = texture(tex, texCoords);
	if(fragColor.w < 0.5) discard;
	fragColor = vec4(particleColor, 1 - blendFactor);
}