#version 330

#if false
uniform float GameTime;
uniform vec3 CameraOffset;
uniform vec2 ScreenSize;
uniform float sphericalVertexDistance;
#endif

#define SPATION_DIAMETER 0.0000008
#define MEANING_OF_LIFE 42.0
#define SOMETHING_NICE 0.214285714286
#define FABRIC_OF_REALITY int(MEANING_OF_LIFE * SOMETHING_NICE)

vec4 frappe_simple_pre_fragment(vec4 color, float isMaterial) {
	// Disintegrating Reality
	float omega_avg = float(1 << FABRIC_OF_REALITY); // average spation density
	float delta = (gl_FragCoord.z * omega_avg) * sphericalVertexDistance * SPATION_DIAMETER;
	delta *= delta;
	vec4 outColor = color;
	float posY = gl_FragCoord.y;
	posY *= mod(delta * 8.0 * ScreenSize.y, 1.0);
	posY += cos(delta);
	float posX = gl_FragCoord.x;
	posX *= mod(delta * 8.0 * ScreenSize.x, 1.0);
	posX += sin(delta);
	float dimmedY = (1.0 - mod(posY, 8.0));
	dimmedY *= (1.0 - mod(posY, 5.0));
	dimmedY = min(1.0 - dimmedY, 1.8);
	float dimmedX = (1.0 - mod(posX, 8.0));
	dimmedX *= (1.0 - mod(posX, 5.0));
	dimmedX = min(1.0 - dimmedX, 1.8);
	outColor.rgb = mix(outColor.rgb * outColor.rgb, outColor.rgb, mod(dimmedY * dimmedX, 1.8 * (gl_FragCoord.z + 0.5)));
	return mix(color, outColor, isMaterial);
}
