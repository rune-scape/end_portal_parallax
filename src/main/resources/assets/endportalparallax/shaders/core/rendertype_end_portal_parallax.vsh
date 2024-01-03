#version 150

in vec3 Position;
in vec3 UV3D;

uniform mat4 ProjMat;
uniform mat3 IViewRotMat;

out vec3 view;
out vec3 uv3d;

vec4 ModelPos = vec4(Position, 1.0);
mat4 ICamJiggleMat = mat4(inverse(mat3(ProjMat))) * ProjMat;

void main() {
    // Strip the Z translation out
    ICamJiggleMat[2].w = 0.0;
    ICamJiggleMat[3].z = 0.0;

    gl_Position = ProjMat * ModelPos;
    view = IViewRotMat * (ICamJiggleMat * ModelPos).xyz;
    uv3d = UV3D;
}
