#version 120

varying vec2 texCoords;

uniform sampler2D tex;

void main(){
	gl_FragColor = texture2D(tex, texCoords);
	if(gl_FragColor.w < 0.1) discard;
	gl_FragColor *= vec4(0.5f, 0.1f, 0.1f, 1.0f);
}