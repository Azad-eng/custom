#version 330 core

layout (location = 0) in vec3 position;

uniform mat4 uniform_Projection;

void main()
{
    gl_Position = uniform_Projection * vec4(position, 1.0f);
}