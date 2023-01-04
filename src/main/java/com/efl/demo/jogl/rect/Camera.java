package com.efl.demo.jogl.rect;

import org.joml.Matrix4f;

/**
 * @author: EFL-ryl
 */
public class Camera {
    // 一、摄像机属性——观察的位置和角度
    //1.观察位置。初始位置位于Z轴的正方向，离世界空间中心点10个单位距离
    private Vector3f location = new Vector3f(0, 0, 10);
    //2.观察方向
    //2.1 观察物体的方向，默认为Z轴负方向（-z）
    private Vector3f direction = new Vector3f(0, 0, -1);
    //2.2 围绕观察方向旋转的方向，默认为向上的方向，即y轴正方向（+y)
    private Vector3f up = new Vector3f(0, 1, 0);
    //3.摄像机的UVN系统
    private Vector3f uAxis = new Vector3f(1, 0, 0);
    private Vector3f vAxis = new Vector3f(0, 1, 0);
    private Vector3f nAxis = new Vector3f(0, 0, 1);
    //4.观察变换矩阵
    private Matrix4f viewMatrix = new Matrix4f();
    /** “观察位置”只需要一个3D向量就可以记录。“观察角度”需要根据“视线”的方向向量和一个向上的方向向量来确定。根据这个两个向量，
     * 就可以计算出被称为“UVN系统”的一组基底，它们是互相正交的三个单位向量，分别指向相机右方、上方和后方，正好构成一个OpenGL右手坐标系*/

    // 二、摄像机属性——观察的范围（除此之外，摄像机还应该定义观察范围，或者说“视锥”。这些参数将用于计算投影变换矩阵）
    //1.视锥。组成视锥的六个平面
    private float near   = 0.1f;    // 近平面距离
    private float far    = 100f; // 远平面距离
    private float left;           // 左平面距离
    private float right;          // 右平面距离
    private float top;            // 上平面距离
    private float bottom;         // 下平面距离
    //2.视野范围。默认70°
    private float fov = (float) Math.toRadians(45);
    private float aspect;// 屏幕高宽比 width / height
    //3.是否平行投影
    private boolean parallel = false;
    //4.投影变换矩阵
    private Matrix4f projectionMatrix = new Matrix4f();

    // 三、变换矩阵——观察-投影
    private Matrix4f viewProjectionMatrix = new Matrix4f();

    // 四、视口——最后，在绘图时还需要把投影后的顶点正确显示到宽为width、高为height的屏幕上。这需要一个称为屏幕空间变换矩阵的东西，或者叫做视口变换矩阵
    //1.屏幕的宽度和高度
    private int width;
    private int height;
    //2.视口变换矩阵
    private Matrix4f viewportMatrix = new Matrix4f();

    // ******************** 构造器 **************************************

    /**
     * 初始化摄像机
     *
     * 现在构造方法中只需要初始化 width和height即可，因为这与用户实际创建的窗口分辨率有关。根据这两个变量就可以算出屏幕的宽高比（aspect），用于计算投影变换矩阵
     * @param width 屏幕宽度
     * @param height 屏幕高度
     */
    public Camera(int width, int height) {
        this.width = width;
        this.height = height;
        this.aspect = (float) width / height;
    }

    // ******************** 变换矩阵 *************************************

    /**
     * 观察-投影 变换矩阵
     */
    public void updateViewProjectionMatrix() {
        updateViewMatrix();
        updateProjectionMatrix();
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }

    /**
     * 观察变换矩阵
     */
    public void updateViewMatrix() {
        // 计算摄像机的旋转矩阵
        direction.cross(up, uAxis);
        uAxis.cross(direction, vAxis);
        nAxis.set(-direction.x, -direction.y, -direction.z);

        // 计算摄像机旋转后的平移变换
        float x = uAxis.dot(location);
        float y = vAxis.dot(location);
        float z = nAxis.dot(location);

        // 计算观察变换矩阵
        float m00 = uAxis.x, m01 = uAxis.y, m02 = uAxis.z, m03 = -x;
        float m10 = vAxis.x, m11 = vAxis.y, m12 = vAxis.z, m13 = -y;
        float m20 = nAxis.x, m21 = nAxis.y, m22 = nAxis.z, m23 = -z;
        float m30 = 0f,      m31 = 0f,      m32 = 0f,      m33 = 1f;
        viewMatrix.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    /**
     * 投影变换矩阵
     */
    public void updateProjectionMatrix() {
        if (!parallel) {
            setPerspective(fov, aspect, near, far);
        } else {
            // 正交投影
            left = -0.5f;
            right = 0.5f;
            top = 0.5f;
            bottom = -0.5f;
            setOrthographic(left, right, bottom, top, near, far);
        }
    }

    /**
     * 视口变换矩阵
     *
     * 视口变换也称为屏幕空间变换，目的是把通过Model-View-Projection变换的物体，正确绘制到屏幕上。由于经过变换后物体的x、y坐标范围
     * 都是[-1, 1]，需要通过放大才能画到宽为width、高为height的屏幕上
     *
     * 下面这个矩阵，对齐次坐标做了2个变换。其一是把原点平移到屏幕中心(width/2, height/2, 0)，见矩阵的第四列；
     * 其二是水平放大 width/2 倍，垂直放大 height/2倍，Z坐标不变，见矩阵左上角3x3矩阵的对角线元素
     */
    public void updateViewportMatrix() {
        float w = width * 0.5f;
        float h = height * 0.5f;

        // 把模型移到屏幕中心，并且按屏幕比例放大。
        float m00 = w, m01 = 0,  m02 = 0,  m03 = w;
        float m10 = 0, m11 = -h, m12 = 0,  m13 = h;
        float m20 = 0, m21 = 0,  m22 = 1f, m23 = 0;
        float m30 = 0, m31 = 0,  m32 = 0,  m33 = 1;

        viewportMatrix.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    // ******************** GETTER AND SETTER *************************************

    /**
     * 获取位置
     * @return
     */
    public Vector3f getLocation() {
        return location;
    }

    /**
     * 设置观察位置
     * @param location
     */
    public void setLocation(Vector3f location) {
        this.location.set(location);
        updateViewMatrix();
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }

    /**
     * 获取观察方向
     * @return
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * 设置观察方向
     * @param direction
     */
    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
        updateViewMatrix();
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }

    /**
     * 获取观察方向的正右方向量
     * @return
     */
    public Vector3f getRightVector() {
        return uAxis;
    }

    /**
     * 获取观察方向的正上方向量
     * @return
     */
    public Vector3f getUpVector() {
        return vAxis;
    }

    /**
     * 是否平行投影
     * @return
     */
    public boolean isParallel() {
        return parallel;
    }

    /**
     * 设置平行投影
     * @param parallel
     */
    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    /**
     * 获取观察变换矩阵
     * @return
     */
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    /**
     * 获取投影变换矩阵
     * @return
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * 获取观察投影变换矩阵
     * @return
     */
    public Matrix4f getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }

    /**
     * 获得视口变换矩阵
     * @return
     */
    public Matrix4f getViewportMatrix() {
        return viewportMatrix;
    }

    // ******************** OTHER METHOD *************************************

    /**
     * 透视投影
     * @param fov 视野范围（弧度制）
     * @param aspect 视锥平面的宽高比（w/h）
     * @param near 近平面距离
     * @param far 远平面距离
     */
    public void setPerspective(float fov, float aspect, float near, float far) {
        // X方向的缩放比
        float zoomX = 1f / (float)Math.tan(fov * 0.5f);
        // Y方向的缩放比
        float zoomY = zoomX * aspect;

        float m00 = zoomX, m01 = 0,     m02 = 0,                      m03 = 0;
        float m10 = 0,     m11 = zoomY, m12 = 0,                      m13 = 0;
        float m20 = 0,     m21 = 0,     m22 = -(far+near)/(far-near), m23 = -2*far*near/(far-near);
        float m30 = 0,     m31 = 0,     m32 = -1,                     m33 = 0;

        projectionMatrix.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }


    /**
     * 透视投影（无穷远）
     * @param fov 视野范围（弧度制）
     * @param aspect 视锥平面的宽高比（w/h）
     * @param near 近平面距离
     */
    public void setPerspective(float fov, float aspect, float near) {
        // X方向的缩放比
        float zoomX = 1f / (float)Math.tan(fov * 0.5f);
        // Y方向的缩放比
        float zoomY = zoomX * aspect;

        float m00 = zoomX, m01 = 0,     m02 = 0,  m03 = 0;
        float m10 = 0,     m11 = zoomY, m12 = 0,  m13 = 0;
        float m20 = 0,     m21 = 0,     m22 = -1, m23 = -2 * near;
        float m30 = 0,     m31 = 0,     m32 = -1, m33 = 0;

        projectionMatrix.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    /**
     * 正交投影
     */
    public void setOrthographic(float left, float right, float bottom, float top, float near, float far) {

        float w = right - left;
        float h = top - bottom;
        float depth = far - near;

        // 计算矩阵
        float m00 = 2 / w, m01 = 0,     m02 = 0,        m03 = -(right + left)/w;
        float m10 = 0,     m11 = 2 / h, m12 = 0,        m13 = -(top + bottom)/h;
        float m20 = 0,     m21 = 0,     m22 = -2/depth, m23 = -(far + near)/depth;
        float m30 = 0,     m31 = 0,     m32 = 0,        m33 = 1;

        projectionMatrix.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    // ******************** 摄像机控制方法 *************************************

    // 五、摄像机属性——控制器（用来操纵摄像机在场景中运动）
    //1.调整摄像机姿态
    //1.1 旋转
    /**
     * 使摄像机按欧拉角旋转（弧度制）
     */
    public void rotate(float xAngle, float yAngle, float zAngle) {
        // 计算旋转后的uvn系统。绕uvn系统的三轴旋转
        Quaternion rot = new Quaternion(uAxis, xAngle);
        rot.multLocal(new Quaternion(vAxis, yAngle));
        rot.multLocal(new Quaternion(nAxis, zAngle));
        // 计算旋转后的视线方向
        rot.multLocal(direction);
        direction.normalizeLocal();

        updateViewMatrix();
        //projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }

    //1.2 lookAt（lookAt方法可以让摄像机“凝视”指定的坐标。根据target和location可以计算出direction，然后重新计算viewMatrix即可）
    /**
     * 使摄像机观察指定位置
     */
    public void lookAt(Vector3f target, Vector3f up) {
        target.subtract(location, direction);
        direction.normalizeLocal();

        this.up.set(up);
        this.up.normalizeLocal();

        updateViewMatrix();
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }

    /**
     * 使摄像机观察指定位置
     */
    public void lookAt(Vector3f location, Vector3f target, Vector3f up) {
        this.location.set(location);
        target.subtract(location, direction);
        this.direction.normalizeLocal();

        this.up.set(up);
        this.up.normalizeLocal();

        updateViewMatrix();
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }

    //1.3 lookDirection 根据用户设置的direction来更新viewMatrix
    /**
     * 使摄像机观察指定方向
     * @param direction
     * @param up
     */
    public void lookAtDirection(Vector3f direction, Vector3f up) {
        this.direction.set(direction);
        this.direction.normalizeLocal();

        this.up.set(up);
        this.up.normalizeLocal();

        updateViewMatrix();
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }

    /**
     * 使摄像机观察指定方向
     * @param location
     * @param direction
     * @param up
     */
    public void lookAtDirection(Vector3f location, Vector3f direction, Vector3f up) {
        this.location.set(location);

        this.direction.set(direction);
        this.direction.normalizeLocal();

        this.up.set(up);
        this.up.normalizeLocal();

        updateViewMatrix();
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }
}
