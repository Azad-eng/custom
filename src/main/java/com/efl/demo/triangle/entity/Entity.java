package com.efl.demo.triangle.entity;

import com.efl.demo.triangle.mesh.Model;
import com.efl.demo.triangle.render.Renderer;
import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;

public class Entity {
    private Model model;
    private Transform transform;

    public Entity(Model model, Transform transform) {
        this.model = model;
        this.transform = transform;
    }

    public void render(GL2 gl, Matrix4f camera, Matrix4f projection) {
        Renderer.render( gl,this, camera, projection);
    }

    public Model getModel() {
        return model;
    }

    public Transform getTransform() {
        return transform;
    }
}
