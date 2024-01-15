#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float LayerOffset;

in vec3 view;
in vec3 uv3d;

vec2 angle2vec2(float radians) {
    return vec2(cos(radians), sin(radians));
}

vec2 portal_layer_uv(int layer, float depth) {
    float layerf = float(layer) * -sign(view.y);
    float rotation = (layerf * layerf * 4321.0 + layerf * 9.0) * 2.0;
    vec2 dir_vec = angle2vec2(radians(rotation));
    mat2 rotate = mat2(
        dir_vec.x, -dir_vec.y,
        dir_vec.y, dir_vec.x
    );
    return (uv3d.st - (view / view.y).xz * (uv3d.p + depth)) * rotate;
}

vec2 portal_layer_uv(int layer) {
    float depth = (15.5 - float(layer)) * -sign(view.y);
    return portal_layer_uv(layer, depth);
}

out vec4 fragColor;

void main() {
    const vec3[16] COLORS = vec3[](
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

    vec2 layer_offset = vec2(0.0, LayerOffset);

    vec3 color = texture2D(Sampler0, portal_layer_uv(0, 65.0) * 0.125).rgb * COLORS[0];
    for (int i = 1; i < 16; i++) {
        float layer_scale = i == 1 ? 0.5 : 0.0625;
        color += texture2D(Sampler1, portal_layer_uv(i) * layer_scale + layer_offset).rgb * COLORS[i];
    }
    fragColor = vec4(color, 1.0);
    //fragColor = vec4(fract(view), 1.0);
    //fragColor = vec4(fract(uv3d), 1.0);
}
