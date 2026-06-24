#version 330 core

layout(location = 0) in vec3 position;

uniform mat4 pr_matrix;
uniform mat4 vw_matrix;
uniform mat4 ml_matrix;

out vec3 positionPASS;

void main(){
	gl_Position = pr_matrix * vw_matrix * ml_matrix * vec4(position, 1.0);
	positionPASS = vec3(vw_matrix * ml_matrix * vec4(position, 1.0));
}