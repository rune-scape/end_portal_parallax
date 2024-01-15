#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float LayerOffset;

in vec3 view;
in vec3 uv3d;

vec2 angle2vec2(float radians) {
    return vec2(cos(radians), sin(radians));
}

vec2 portal_layer_uv(float rotation, float view_depth) {
    vec2 dir_vec = angle2vec2(radians(rotation));
    mat2 rotate = mat2(
        dir_vec.x, -dir_vec.y,
        dir_vec.y, dir_vec.x
    );
    return (uv3d.st - (view / view.y).xz * view_depth) * rotate;
}

out vec4 fragColor;

void main() {
    const vec4[16] COLORS = vec4[](
        vec4(0.022087, 0.098399, 0.110818, 1.0),
        vec4(0.011892, 0.095924, 0.089485, 1.0),
        vec4(0.027636, 0.101689, 0.100326, 1.0),
        vec4(0.046564, 0.109883, 0.114838, 1.0),
        vec4(0.064901, 0.117696, 0.097189, 1.0),
        vec4(0.063761, 0.086895, 0.123646, 1.0),
        vec4(0.084817, 0.111994, 0.166380, 1.0),
        vec4(0.097489, 0.154120, 0.091064, 1.0),
        vec4(0.106152, 0.131144, 0.195191, 1.0),
        vec4(0.097721, 0.110188, 0.187229, 1.0),
        vec4(0.133516, 0.138278, 0.148582, 1.0),
        vec4(0.070006, 0.243332, 0.235792, 1.0),
        vec4(0.196766, 0.142899, 0.214696, 1.0),
        vec4(0.047281, 0.315338, 0.321970, 1.0),
        vec4(0.204675, 0.390010, 0.302066, 1.0),
        vec4(0.060716, 0.236115, 0.496118, 1.0)
    );

    vec4 color = texture2D(Sampler0, portal_layer_uv(0.0, uv3d.p - (65.0 * sign(view.y))) * 0.125) * COLORS[0];

    vec2 uv_offset = vec2(0.0, LayerOffset);
    for (int i = 1; i < 16; i++) {
        for (int layer_sign = -1; layer_sign <= 1; layer_sign += 2) {
            float layer_num = float(i * layer_sign) * -sign(view.y);
            float layer_rotation = (layer_num * layer_num * 4321.0 + layer_num * 9.0) * 2.0;
            float layer_scale = i == 1 ? 0.5 : 0.0625;
            float layer_depth = (15.5 - float(i)) * float(layer_sign) * -sign(view.y);
            float layer_view_depth = uv3d.p + layer_depth;
            if (sign(layer_view_depth) != sign(view.y)) {
                color += texture2D(Sampler1, portal_layer_uv(layer_rotation, layer_view_depth) * layer_scale + uv_offset) * COLORS[i];
            }
        }
    }
    fragColor = vec4(color.rgb, 1.0);
}
