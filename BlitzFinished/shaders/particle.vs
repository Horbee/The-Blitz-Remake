#version 120

attribute vec3 position;
attribute vec2 tc;

varying vec3 pixelPos;
varying vec2 texCoords;

uniform mat4 pr_matrix;
uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);
uniform vec3 scale = vec3(1.0);

void main(){
	texCoords = tc;
	vec4 transformedVertexPos = vec4(position, 1.0) * vec4(scale, 1.0);
	gl_Position = pr_matrix * vw_matrix * ml_matrix * transformedVertexPos;
	pixelPos = vec3(vw_matrix * ml_matrix * vec4(position, 1.0));	
}