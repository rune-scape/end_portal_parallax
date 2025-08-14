#version 150

in vec3 Position;
in vec2 UV;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform vec3 CameraPos;

out vec3 view;
out vec3 uv3d;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    // strip the Z translation out
    // hope someone appreciates how weird this was to figure out
    mat4 ProjMatNoZ = ProjMat;
    ProjMatNoZ[3].z = 0.0;
    ProjMatNoZ[2].w = 0.0;
    mat4 ICamJiggleMat = mat4(inverse(mat3(ProjMatNoZ * ModelViewMat))) * ProjMatNoZ * ModelViewMat;

    view = IViewRotMat * (ICamJiggleMat * vec4(Position, 1.0)).xyz;
    uv3d.xyz = (IViewRotMat * Position).xzy;
    uv3d.xyz += (vec4(CameraPos, 1.0)).xzy;
    // you might think using fract(uv3d.z) is the same as this,
    // but it produces z fighting on the top and bottom, and strange distortions on the side
    uv3d.z = UV.y;
}
