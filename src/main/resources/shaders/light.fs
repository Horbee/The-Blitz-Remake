#version 120

uniform vec2 lightPosition;
uniform vec3 lightColor;
uniform float lightIntensity;
uniform float radius;

varying vec3 positionPASS;

void main(){
	float distance = length(lightPosition - positionPASS.xy);
	
	if (distance > radius) discard;
	
	float attenuation = 1.0 / distance - 0.005;
	
	vec4 color = vec4(attenuation, attenuation, attenuation, pow(attenuation, 3)) * vec4(lightColor * lightIntensity, 1.0);
	color *= 0.5;
	gl_FragColor = color; 	
}