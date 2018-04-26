#version 120

varying vec2 texCoords;

uniform sampler2D tex;
uniform vec3 colorIN;
uniform float blend;

void main(){
	vec4 color = texture2D(tex, texCoords);
	//if (color == vec4(1.0, 0.0, 1.0, 1.0)) color = vec4(0.0, 0.0, 0.0, 0.0);
	gl_FragColor = color;
	if(gl_FragColor.w < 0.1) discard;
	gl_FragColor = vec4(colorIN, blend);
}