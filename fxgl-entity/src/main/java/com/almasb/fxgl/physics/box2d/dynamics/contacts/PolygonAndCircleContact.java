/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.collision.shapes.CircleShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.PolygonShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.ShapeType;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

public class PolygonAndCircleContact extends Contact {

    public PolygonAndCircleContact(IWorldPool argPool) {
        super(argPool);
    }

    public void init(Fixture fixtureA, Fixture fixtureB) {
        super.init(fixtureA, 0, fixtureB, 0);
        assert m_fixtureA.getType() == ShapeType.POLYGON;
        assert m_fixtureB.getType() == ShapeType.CIRCLE;
    }

    @Override
    public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
        pool.getCollision().collidePolygonAndCircle(manifold, (PolygonShape) m_fixtureA.getShape(),
                xfA, (CircleShape) m_fixtureB.getShape(), xfB);
    }
}
