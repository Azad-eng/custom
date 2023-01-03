package com.efl.demo.jogl.rect;

import org.joml.Matrix4f;

/**
 * @author: EFL-ryl
 */
public class Camera {
    // 相机属性
    public Vector3f position = new Vector3f(0.0f, 0.0f, 3.0f);
    public Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    public Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    public Vector3f right = new Vector3f();
    public Vector3f worldUp = new Vector3f();
    // 欧拉角
    private float yaw = -90.0f;
    private float pitch = 0.0f;
    public float zoom = 45.0f;

    // 向量构造器
    public Camera(Vector3f position) {
        this.position = position;
        worldUp = up;
        updateCameraVectors();
    }

    // 标量构造器
    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ){
        position = new Vector3f(posX, posY, posZ);
        worldUp = new Vector3f(upX, upY, upZ);
        updateCameraVectors();
    }

    public Matrix4f getViewMatrix(Matrix4f matrix4f){
        return matrix4f.lookAt(position.x, position.y, position.z, //Camera is at (0,0,-1), in World Space
                // where you want to look at, in world space
                position.add(front).x, position.add(front).y, position.add(front).z, //and looks at the origin
                // probably glm::vec3(0,1,0), but (0,-1,0) would make you looking upside-down, which can be great too
                up.x, up.y, up.z);
    }

    public void ProcessKeyboard(int direction, float deltaTime) {
        // 相机选项
        float movementSpeed = 3.0f;
        float velocity = movementSpeed * deltaTime;
        //前
        if (direction == 0){
            Vector3f mult = front.mult(velocity);
            position = position.add(mult);
        //后
        } else if (direction == 1){
            Vector3f mult = front.mult(velocity);
            position = position.subtract(mult);
        //左
        } else if (direction == 2){
            Vector3f mult = right.mult(velocity);
            position = position.subtract(mult);
        //右
        } else if (direction == 3){
            Vector3f mult = right.mult(velocity);
            position = position.add(mult);
        }
    }

    public void ProcessMouseMovement(float dx, float dy, boolean constrainPitch) {
        float mouseSensitivity = 0.25f;
        dx *= mouseSensitivity;
        dy *= mouseSensitivity;
        yaw += dx;
        pitch += dy;
        // Make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch) {
            if (pitch > 89.0f) {
                pitch = 89.0f;
            }
            if (pitch < -89.0f) {
                pitch = -89.0f;
            }
        }
        // Update Front, Right and Up Vectors using the updated Eular angles
        updateCameraVectors();
    }

    public void ProcessMouseScroll(float dy) {
        if (zoom > 1.0f && zoom < 45.0f){
            zoom *= dy;
        } else if (dy <= 1.0f){
            zoom = 1.0f;
        } else if (dy >= 45.0f){
            zoom = 45.0f;
        }
    }

    private void updateCameraVectors(){
        Vector3f front = new Vector3f();
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        this.front = front.normalize();
        right = this.front.cross(worldUp).normalize();
        up = right.cross(this.front).normalize();
    }
}
