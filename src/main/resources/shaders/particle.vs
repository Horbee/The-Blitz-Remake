#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tc;

out vec3 pixelPos;
out vec2 texCoords;

uniform mat4 pr_matrix;
uniform mat4 vw_matrix;
uniform mat4 ml_matrix;
uniform vec3 scale;

void main(){
	texCoords = tc;
	vec4 transformedVertexPos = vec4(position, 1.0) * vec4(scale, 1.0);
	gl_Position = pr_matrix * vw_matrix * ml_matrix * transformedVertexPos;
	pixelPos = vec3(vw_matrix * ml_matrix * vec4(position, 1.0));
}