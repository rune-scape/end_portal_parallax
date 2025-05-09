#version 150

in vec3 Position;
in vec2 UV;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 CameraPos;

out vec3 view;
out vec3 uv3d;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    mat4 ProjMatNoZ = ProjMat;
    ProjMatNoZ[3].z = 0.0;
    ProjMatNoZ[2].w = 0.0;
    mat4 ICamJiggleMat = mat4(inverse(mat3(ProjMatNoZ * ModelViewMat))) * ProjMatNoZ * ModelViewMat;
    view = (ICamJiggleMat * vec4(Position, 1.0)).xyz;
    uv3d.st = Position.xz;
    uv3d.st += CameraPos.xz;
    // you might think using fract(uv3d.z) is the same as this, but it produces z fighting on the edges
    uv3d.z = UV.y;
}
