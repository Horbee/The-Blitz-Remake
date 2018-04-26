#version 120

attribute vec3 position;
attribute vec2 tc;

varying vec2 texCoords;

uniform mat4 pr_matrix;
uniform mat4 ml_matrix = mat4(1.0);

void main(){
	texCoords = tc;
	gl_Position = pr_matrix * ml_matrix * vec4(position, 1.0);	
}