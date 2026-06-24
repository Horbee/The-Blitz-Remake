#version 330 core

layout(location = 0) out vec4 fragColor;

uniform float time;
uniform float OUT;

void main(){
	if(OUT == 1) {
		fragColor = vec4(0.0, 0.0, 0.0, (time * 0.01));
	} else if (OUT == 0) {
		fragColor = vec4(0.0, 0.0, 0.0, (1 - time * 0.01));
	}
}