#version 330

#if false
uniform float GameTime;
uniform vec3 CameraOffset;
uniform vec2 ScreenSize;
uniform float sphericalVertexDistance;
#endif

#define GRAVITONIUM_DIAMETER_MPC 1.0672
#define SCAN_SPEED_0 8.0
#define SCAN_SPEED 512.0

vec4 frappe_simple_pre_fragment(vec4 color, float isMaterial) {
	// Destabilizing Reality
	float delta = sphericalVertexDistance * GRAVITONIUM_DIAMETER_MPC;
	vec4 outColor = color;
	float posY0 = gl_FragCoord.y;
	posY0 += mod(GameTime * SCAN_SPEED_0, ScreenSize.y);
	float oposY0 = gl_FragCoord.y;
	oposY0 *= mod(GameTime * SCAN_SPEED_0, ScreenSize.y);
	float posY = gl_FragCoord.y;
	posY += mod(GameTime * SCAN_SPEED * delta, ScreenSize.y);
	float oposY = gl_FragCoord.y;
	oposY *= mod(GameTime * SCAN_SPEED * delta, ScreenSize.y);
	float dimmedY0 = (8.0 - mod(sphericalVertexDistance, 8.0));
	dimmedY0 *= (1.0 - mod(posY0, 5.0));
	dimmedY0 *= (1.0 - mod(oposY0, 16.0));
	dimmedY0 = min(1.0 - dimmedY0, 1.8);
	float dimmedY = (8.0 - mod(gl_FragCoord.z, 8.0));
	dimmedY *= (1.0 - mod(posY, 5.0));
	dimmedY *= (1.0 - mod(oposY, 16.0));
	dimmedY = min(1.0 - dimmedY, 1.8);
	vec4 outColor0 = outColor;
	outColor.rgb = mix(outColor.rgb * outColor.rgb, outColor.rgb, clamp(dimmedY, -0.0625, 1.0));
	outColor0.rgb = mix(outColor0.rgb * outColor0.rgb, outColor0.rgb, clamp(dimmedY0, 0.0, 1.0));
	outColor.rgb = mix(vec3(0.00508, 0.49003, 0.90842), outColor.rgb, 0.85);
	outColor = mix(outColor, outColor0, 0.5);
	return mix(color, outColor, isMaterial);
}
