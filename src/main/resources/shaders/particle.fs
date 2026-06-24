#version 330 core

in vec3 pixelPos;
in vec2 texCoords;

layout(location = 0) out vec4 fragColor;

uniform sampler2D tex;
uniform float life;
uniform float time;
uniform float praticleType;

uniform vec2 particlePosition;

void main(){

	if(praticleType == 1) { // lightning particle
		vec4 text = texture(tex, texCoords);
		float color = life / (time * 0.5);
		fragColor = text * vec4(color, 0.2 * color, 0.2 * color, 1.0);
		if (fragColor.w < 0.01) discard;
		fragColor.w = fragColor.w-time * 0.02;

	}else if(praticleType == 0) { // simple particle
		float color = life / (time * 0.5);
		fragColor = vec4(color, 0.2 * color, 0.2 * color, (1-(time * 0.01)));

	}else if(praticleType == 2) { // spark particle
		vec4 text = texture(tex, texCoords);

		vec3 lightColor = vec3(0.8, 0.4, 0.0);
		float attenuation = 1.0 / (length(particlePosition - pixelPos.xy) + 0.2) + 1;
		vec4 sparkColor = vec4(attenuation, attenuation, attenuation, pow(attenuation, 3)) * vec4(lightColor * 10, 1.0);

		fragColor = text * sparkColor;

	}else if(praticleType == 3) { // smoke particle
		vec4 text = texture(tex, texCoords);
		fragColor = text;
		if (fragColor.w < 0.01) discard;
		fragColor.w = fragColor.w-time * 0.04;
	}
}