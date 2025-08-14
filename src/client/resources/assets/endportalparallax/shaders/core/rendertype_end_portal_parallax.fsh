#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float LayerOffset;

in vec3 view;
in vec3 uv3d;

out vec4 fragColor;

const vec3[16] LAYER_COLORS = vec3[](
    vec3(0.022087, 0.098399, 0.110818),
    vec3(0.011892, 0.095924, 0.089485),
    vec3(0.027636, 0.101689, 0.100326),
    vec3(0.046564, 0.109883, 0.114838),
    vec3(0.064901, 0.117696, 0.097189),
    vec3(0.063761, 0.086895, 0.123646),
    vec3(0.084817, 0.111994, 0.166380),
    vec3(0.097489, 0.154120, 0.091064),
    vec3(0.106152, 0.131144, 0.195191),
    vec3(0.097721, 0.110188, 0.187229),
    vec3(0.133516, 0.138278, 0.148582),
    vec3(0.070006, 0.243332, 0.235792),
    vec3(0.196766, 0.142899, 0.214696),
    vec3(0.047281, 0.315338, 0.321970),
    vec3(0.204675, 0.390010, 0.302066),
    vec3(0.060716, 0.236115, 0.496118)
);

const float[16] LAYER_SCALES = float[](
    0.125,
    0.5,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625,
    0.0625
);

mat2 angle_to_mat2(float rad) {
    float c = cos(rad);
    float s = sin(rad);
    return mat2(
        c, -s,
        s, c
    );
}

vec3 portal_layer_uvd(float rot, float depth) {
    mat2 rotate = angle_to_mat2(radians(rot));
    vec2 scaled_pre_uv = (view.xz / view.y) * (uv3d.z + depth);
    vec2 layer_uv = (uv3d.xy - scaled_pre_uv) * rotate;
    float coord_distance = length((view.xz - scaled_pre_uv));
    return vec3(layer_uv, coord_distance);
}

void main() {
    // recreates the original end portal parallax effect
    // also fades away at large horizontal distances to make end gateways look nicer
    vec2 layer_offset = vec2(0.0, LayerOffset);
    vec3 color = texture2D(Sampler0, portal_layer_uvd(0.0, 65.0).xy * LAYER_SCALES[0]).rgb * LAYER_COLORS[0];
    for (int i = 1; i < 16; i++) {
        float layer_dir = -sign(view.y);
        float layerf = float(i) * layer_dir;
        float layer_rot = (layerf * layerf * 4321.0 + layerf * 9.0) * 2.0;
        float layer_depth = (15.5 - float(i)) * layer_dir;
        vec3 layer_uvd = portal_layer_uvd(layer_rot, layer_depth);
        layer_uvd.xy = layer_uvd.xy * LAYER_SCALES[i] + layer_offset;
        float distance_dropoff = clamp(25.0 / layer_uvd.z, 0.0, 1.0);
        color += texture2D(Sampler1, layer_uvd.xy).rgb * LAYER_COLORS[i] * distance_dropoff;
    }
    fragColor = vec4(color, 1.0);
    //fragColor = vec4(fract(view), 1.0);
    //fragColor = vec4(fract(uv3d), 1.0);
}
