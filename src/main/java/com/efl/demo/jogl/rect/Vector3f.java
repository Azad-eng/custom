package com.efl.demo.jogl.rect;

/**
 * @author: EFL-ryl
 */
public class Vector3f {
    public float x, y, z;
    //3D零向量表示为 (0, 0, 0)，它是唯一一个长度为0的向量
    public final static Vector3f ZERO = new Vector3f(0, 0, 0);

    public Vector3f() {
        x = y = z = 0;
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * 求负向量
     * 把向量的各个分量求负，可得其负向量。简单地说就是 x + (-x) = 0。
     * 负向量可以看作与原向量长度相同，但是方向相反的向量。
     * @return 不会改变原向量，而是返回一个新的向量，它是原向量的负向量
     */
    public Vector3f negate() {
        return new Vector3f(-x, -y, -z);
    }

    /**
     * @return 会改变原向量自身的值，并返回该向量本身
     */
    public Vector3f negateLocal() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    /**
     * 开根号是一个相当昂贵的运算。如果只是想比较两个向量的大小，可以直接比较不开根号的值。
     * 若要判断向量长度是否为 1 或 0，也不需要开根号，因为1和0的平方等于它们本身
     * @return 返回向量的长度
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * 所以，一般在3D数学库中，会提供这样一个方法，来返回未开根号的平方和：
     * @return 返回向量长度的平方
     */
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * 标准化向量
     * 对于许多向量，我们只关心它的方向而不关心其大小。如：“我面向的是什么方向？”，这样的情况下使用单位向量更为简单。
     * 单位向量就是长度为1的向量。单位向量经常也被称作标准化向量或更简单地称为 “法线”。
     *
     * 一般会把与三个坐标轴平行的单位向量定义为常量:
     */
    public final static Vector3f UNIT_X = new Vector3f(1, 0, 0);
    public final static Vector3f UNIT_Y = new Vector3f(0, 1, 0);
    public final static Vector3f UNIT_Z = new Vector3f(0, 0, 1);

    /**
     * 对于任意非零向量v，都可以计算出一个与它方向相同的单位向量。这个过程称作向量的“标准化”。要标准化向量，将向量除以它的大小（模）即可
     * 求单位向量
     */
    public Vector3f normalize() {
        float length = x * x + y * y + z * z;
        if (length != 1f && length != 0f) {
            length = (float) (1.0 / Math.sqrt(length));
            return new Vector3f(x * length, y * length, z * length);
        }
        return new Vector3f(x, y, z);
    }

    /**
     * 求单位向量
     */
    public Vector3f normalizeLocal() {
        float length = x * x + y * y + z * z;
        if (length != 1f && length != 0f) {
            length = (float) (1.0 / Math.sqrt(length));
            x *= length;
            y *= length;
            z *= length;
        }
        return this;
    }

    /**
     * 向量的加法和减法
     * 向量的加法运算法则很简单：两个向量相加，将对应的分量相加即可。减法解释为负向量，a-b=a+(-b)。
     *
     * 向量不能与标量或维数不同的向量相加减
     * 和标量乘法一样，向量加法满足交换律，但是向量减法不满足交换律。永远有 a+b=b+a，但 a-b=-(b-a)，仅当a=b时，a-b=b-a。
     *
     * 向量加法
     */
    public Vector3f add(Vector3f v) {
        return new Vector3f(x + v.x, y + v.y, z + v.z);
    }


    /**
     * 向量加法
     *
     * 向量a和b相加的几何解释为：平移向量，使向量a的头连接向量b的尾，接着从a的尾向b的头画一个向量。这就是向量加法的“三角形法则”。
     */
    public Vector3f addLocal(Vector3f v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    /**
     * 向量减法
     */
    public Vector3f subtract(Vector3f v) {
        return new Vector3f(x - v.x, y - v.y, z - v.z);
    }

    /**
     * 向量减法
     * @return
     */
    public Vector3f subtract(Vector3f v, Vector3f result) {
        if(result == null) {
            result = new Vector3f();
        }
        result.x = x - v.x;
        result.y = y - v.y;
        result.z = z - v.z;
        return result;
    }

    /**
     * 向量减法
     */
    public Vector3f subtractLocal(Vector3f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    /**
     * 标量乘法
     *
     * 标量与向量的乘法非常直接，将向量的每个分量都与标量相乘即可。
     */
    public Vector3f mult(float scalor) {
        return new Vector3f(x * scalor, y * scalor, z * scalor);
    }

    /**
     * 标量乘法
     *
     * @param scalor
     * @return
     */
    public Vector3f mult(float scalor, Vector3f store) {
        if (store == null) {
            store = new Vector3f();
        }
        store.set(x * scalor, y * scalor, z * scalor);
        return store;
    }

    /**
     * 标量乘法
     *
     */
    public Vector3f multLocal(float scalor) {
        x *= scalor;
        y *= scalor;
        z *= scalor;
        return this;
    }

    /**
     * 向量乘法
     *
     * @param v
     * @return
     */
    public Vector3f mult(Vector3f v) {
        return new Vector3f(x * v.x, y * v.y, z * v.z);
    }

    /**
     * 向量乘法
     * @param vec
     * @param store
     * @return
     */
    public Vector3f mult(Vector3f vec, Vector3f store) {
        if (store == null) store = new Vector3f();
        return store.set(x * vec.x, y * vec.y, z * vec.z);
    }

    /**
     * 向量乘法
     *
     * @param v
     * @return
     */
    public Vector3f multLocal(Vector3f v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
        return this;
    }

    /**
     * 标量除法
     * @param scalor
     * @return
     */
    public Vector3f divide(float scalor) {
        scalor = 1 / scalor;
        return new Vector3f(x * scalor, y * scalor, z * scalor);
    }

    /**
     * 标量除法
     * @param scalor
     * @return
     */
    public Vector3f divideLocal(float scalor) {
        scalor = 1 / scalor;
        x *= scalor;
        y *= scalor;
        z *= scalor;
        return this;
    }

    /**
     * 向量除法
     * @param v
     * @return
     */
    public Vector3f divide(Vector3f v) {
        return new Vector3f( x / v.x, y / v.y, z / v.z);
    }

    /**
     * 向量除法
     * @param v
     * @return
     */
    public Vector3f divideLocal(Vector3f v) {
        x /= v.x;
        y /= v.y;
        z /= v.z;
        return this;
    }

    /**
     * 向量点乘（内积）
     * 向量的点乘也称作内积。向量点乘就是对应分量乘积的和，其结果是一个标量
     *
     * 点乘等于向量长度与向量夹角的cos值的积。
     *
     * a dot b = |a|*|b|*cos(θ)
     * 这个公式很有用，如果a、b都是单位向量，点乘就可以直接算出它们之间的 cos 值。根据cos值，即可以计算两个向量之间的夹角：
     *
     * 若 cos > 0。说明a和b的夹角 0° <= θ < 90°；
     * 若 cos = 0，说明a和b正交（垂直），即 θ ≈ 90°；
     * 若 cos < 0，说明a和b的夹角 90° < θ <= 180°。
     * 向量的大小并不影响点乘结果的符号，所以根据cos的符号就可以判断a和b的大致方向。
     */
    public float dot(Vector3f v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * 利用点乘，很容易求得两个单位向量间的夹角。
     * 求两个向量之间的夹角（弧度制）
     * 注意：参与运算的两个向量都应该是单位向量
     */
    public float angleBetween(Vector3f v) {
        float dotProduct = x * v.x + y * v.y + z * v.z;
        float angle = (float) Math.acos(dotProduct);
        return angle;
    }

    /**
     * 向量投影
     *
     * 已知两个向量a、b，可以把a分解成两个向量，一个垂直于b，另一个平行于b。平行于b的那个向量称为a在b上的投影。
     *
     * 根据向量点乘公式，易得两个向量夹角的 cos 值，根据 cos 值可以进一步求得向量的投影
     */
    public Vector3f project(Vector3f v){
        float n = x * v.x + y * v.y + z * v.z; // A . B
        float d = v.lengthSquared(); // |B|^2
        float scalor = n / d;
        return new Vector3f(v.x * scalor, v.y * scalor, v.z * scalor);
    }

    /**
     * 向量投影
     */
    public Vector3f projectLocal(Vector3f v){
        float n = this.dot(v); // A . B
        float d = v.lengthSquared(); // |B|^2
        float scalor = n / d;
        x = v.x * scalor;
        y = v.y * scalor;
        z = v.z * scalor;
        return this;
    }

    /**
     * 向量叉乘（外积）
     *
     * 叉乘将会得到一个新的向量，它垂直于原来的两个向量，其长度 |a×b| 正好是以向量a、b位两边的平行四边形的面积。
     * 叉乘的这种特性，经常用于求三角形的表面法线
     *
     *@return 返回一个新的向量，它垂直于当前两个向量。
     */
    public Vector3f cross(Vector3f v) {
        float rx = y * v.z - z * v.y;
        float ry = z * v.x - x * v.z;
        float rz = x * v.y - y * v.x;
        return new Vector3f(rx, ry, rz);
    }

    /**
     * 向量叉乘（外积）
     *
     * @param v
     * @param result
     * @return
     */
    public Vector3f cross(Vector3f v, Vector3f result) {
        if (result == null) result = new Vector3f();
        float resX = ((y * v.x) - (z * v.y));
        float resY = ((z * v.x) - (x * v.z));
        float resZ = ((x * v.y) - (y * v.x));
        result.set(resX, resY, resZ);
        return result;
    }

    /**
     * 向量叉乘（外积）
     */
    public Vector3f crossLocal(Vector3f v) {
        float tempX = y * v.z - z * v.y;
        float tempY = z * v.x - x * v.z;
        z = x * v.y - y * v.x;
        x = tempX;
        y = tempY;
        return this;
    }

    /**
     * 设置向量的三个分量。
     *
     * @param x
     * @param y
     * @param z
     */
    public Vector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * 设置向量的三个分量
     * @param v
     */
    public Vector3f set(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        return this;
    }
}
