#version 120

varying vec2 texCoords;
varying vec3 positionToFS;

uniform sampler2D tex;
uniform vec2 particlePosition;
uniform vec3 particleColor;
uniform float blendFactor;

void main(){
	gl_FragColor = texture2D(tex, texCoords);
	if(gl_FragColor.w < 0.5) discard;
	gl_FragColor = vec4(particleColor, 1 - blendFactor);

}