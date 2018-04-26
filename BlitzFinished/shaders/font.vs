#version 120

attribute vec3 position;
attribute vec2 tc;

varying vec2 texCoords;

uniform mat4 pr_matrix;
uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);
uniform vec3 scale = vec3(1.0);

void main(){
	texCoords = tc * vec2(1.0, -1.0);
	vec4 transformedVertexPos = vec4(position, 1.0) * vec4(scale, 1.0);
	gl_Position = pr_matrix * vw_matrix * ml_matrix * transformedVertexPos;	
}