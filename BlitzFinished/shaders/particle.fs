#version 120

varying vec3 pixelPos;
varying vec2 texCoords;

uniform sampler2D tex;
uniform float life;
uniform float time;
uniform float praticleType;

uniform vec2 particlePosition;

void main(){	

	if(praticleType == 1) { // lightning particle
		vec4 text = texture2D(tex, texCoords);
		float color = life / (time * 0.5);
		gl_FragColor = text * vec4(color, 0.2 * color, 0.2 * color, 1.0);
		if (gl_FragColor.w < 0.01) discard;
		gl_FragColor.w = gl_FragColor.w-time * 0.02;
		
	}else if(praticleType == 0) { // simple particle		
		float color = life / (time * 0.5);
		gl_FragColor = vec4(color, 0.2 * color, 0.2 * color, (1-(time * 0.01))); 	
		//gl_FragColor *= 20.0 / (length(particlePosition - pixelPos.xy) + 0.2) + 0.1;
	
	}else if(praticleType == 2) { // spark particle
		vec4 text = texture2D(tex, texCoords);
		
		vec3 lightColor = vec3(0.8, 0.4, 0.0);
		float attenuation = 1.0 / (length(particlePosition - pixelPos.xy) + 0.2) + 1;
		vec4 sparkColor = vec4(attenuation, attenuation, attenuation, pow(attenuation, 3)) * vec4(lightColor * 10, 1.0);
		
		gl_FragColor = text * sparkColor;
	//	gl_FragColor *= sparkColor;
		//if (gl_FragColor == vec4(0.0f, 0.0f, 0.0f, 1.0f)) discard;
		
	}else if(praticleType == 3) { // smoke particle
		vec4 text = texture2D(tex, texCoords);
		gl_FragColor = text;
		if (gl_FragColor.w < 0.01) discard;
		gl_FragColor.w = gl_FragColor.w-time * 0.04;
	}
}