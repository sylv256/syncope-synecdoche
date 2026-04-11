#version 330

#if false
uniform float GameTime;
uniform vec3 CameraOffset;
uniform vec2 ScreenSize;
#endif

float syncopate_random (vec2 st) {
	return fract(sin(dot(st.xy,
	vec2(12.9898,78.233)))*
	43758.5453123);
}

vec4 frappe_simple_pre_fragment(vec4 color, float isMaterial) {
	// Syncopating Reality
	vec4 outColor = color;
	vec2 st = gl_FragCoord.xy / ScreenSize.xy;
	float rnd = syncopate_random(st + mod(GameTime, 1.0));
	outColor -= rnd / 4.0;
	float rnd1 = syncopate_random(st + 0.13 * mod(GameTime, 1.0));
	outColor += rnd1 / 4.0;
	return mix(color, outColor, isMaterial);
}
