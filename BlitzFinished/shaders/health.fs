#version 120

varying vec2 texCoords;

uniform sampler2D tex;
uniform float healthIndicator;

void main(){
	gl_FragColor = texture2D(tex, texCoords) * vec4(healthIndicator, healthIndicator, healthIndicator, 1.0);
}