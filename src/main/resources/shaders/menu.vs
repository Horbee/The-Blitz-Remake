#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tc;

out vec2 texCoords;

uniform mat4 pr_matrix;
uniform mat4 ml_matrix;

void main(){
	texCoords = tc;
	gl_Position = pr_matrix * ml_matrix * vec4(position, 1.0);
}