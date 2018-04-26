#version 120

attribute vec3 position;

uniform mat4 pr_matrix;
uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);

varying vec3 positionPASS;

void main(){
	gl_Position = pr_matrix * vw_matrix * ml_matrix * vec4(position, 1.0);
	positionPASS = vec3(vw_matrix * ml_matrix * vec4(position, 1.0));	
}