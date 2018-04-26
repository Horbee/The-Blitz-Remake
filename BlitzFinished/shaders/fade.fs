#version 120

uniform float time;
uniform float OUT;

void main(){

	if(OUT == 1) {
		gl_FragColor = vec4(0.0, 0.0, 0.0, (time * 0.01));
	} else if (OUT == 0) {
		gl_FragColor = vec4(0.0, 0.0, 0.0, (1 - time * 0.01));
	}
}