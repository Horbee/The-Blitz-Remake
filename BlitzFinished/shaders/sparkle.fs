#version 120

varying vec2 texCoords;
varying vec3 positionToFS;

uniform sampler2D tex;
uniform vec2 particlePosition;
uniform vec3 particleColor;
uniform float intensity;

void main(){
	float distance = length(particlePosition - positionToFS.xy);
	float attenuation = 5.0 / distance + 0.10;
	
	vec4 color = vec4(attenuation, attenuation, attenuation, pow(attenuation, 3)) * vec4(particleColor * intensity, 1.0);
	color *= 0.5;
	gl_FragColor = texture2D(tex, texCoords) * color;
}