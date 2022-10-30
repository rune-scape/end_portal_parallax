#version 120

in vec3 Position;

uniform mat4 IViewRotMat;
uniform vec3 CameraPos;
uniform float PortalY;

varying vec3 view;
varying vec3 uv3d;

mat3 inverse(mat3 m) {
    float a00 = m[0][0], a01 = m[0][1], a02 = m[0][2];
    float a10 = m[1][0], a11 = m[1][1], a12 = m[1][2];
    float a20 = m[2][0], a21 = m[2][1], a22 = m[2][2];

    float b01 = a22 * a11 - a12 * a21;
    float b11 = -a22 * a10 + a12 * a20;
    float b21 = a21 * a10 - a11 * a20;

    float det = a00 * b01 + a01 * b11 + a02 * b21;

    return mat3(b01, (-a22 * a01 + a02 * a21), (a12 * a01 - a02 * a11),
    b11, (a22 * a00 - a02 * a20), (-a12 * a00 + a02 * a10),
    b21, (-a21 * a00 + a01 * a20), (a11 * a00 - a01 * a10)) / det;
}

vec4 ModelPos = gl_ModelViewMatrix * vec4(Position, 1.0);
mat4 ICamJiggleMat = mat4(inverse(mat3(gl_ModelViewMatrix))) * gl_ModelViewMatrix;

void main() {
    gl_Position = gl_ProjectionMatrix * ModelPos;
    view = (ICamJiggleMat * vec4(Position, 1.0)).xyz;
    uv3d = (CameraPos + Position).xzy;
    uv3d.p -= PortalY;
}
