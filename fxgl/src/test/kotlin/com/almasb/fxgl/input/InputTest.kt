/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import com.almasb.fxgl.annotation.OnUserAction
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGL.Companion.configure
import com.almasb.fxgl.app.MockApplicationModule
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputTest {

    private lateinit var input: Input

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            configure(MockApplicationModule.get())
        }
    }

    @BeforeEach
    fun setUp() {
        input = FXGL.getInstance(Input::class.java)
    }

    @Test
    fun `Test registering input does not affect mocking`() {
        assertThat(input.registerInput, `is`(true))

        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                calls = 1
            }

            override fun onActionEnd() {
                calls = -1
            }
        }, KeyCode.A)

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))

        // this must not affect mocking
        input.registerInput = false

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))
    }

    @Test
    fun `Test processing input affects mocking`() {
        assertThat(input.processInput, `is`(true))

        var calls = 0

        input.addAction(object : UserAction("Test") {
            override fun onActionBegin() {
                calls = 1
            }

            override fun onActionEnd() {
                calls = -1
            }
        }, KeyCode.A)

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(1))

        input.mockKeyRelease(KeyCode.A)
        assertThat(calls, `is`(-1))

        // this must affect mocking
        input.processInput = false

        input.mockKeyPress(KeyCode.A)
        assertThat(calls, `is`(-1))

        input.mockKeyRelease(KeyCode.A)
    }

    @Test
    fun `Mocking must not trigger isHeld`() {
        // keys
        assertThat(input.isHeld(KeyCode.A), `is`(false))

        input.mockKeyPress(KeyCode.A)

        assertThat(input.isHeld(KeyCode.A), `is`(false))

        input.mockKeyRelease(KeyCode.A)

        // buttons
        assertThat(input.isHeld(MouseButton.PRIMARY), `is`(false))

        input.mockButtonPress(MouseButton.PRIMARY, 0.0, 0.0)

        assertThat(input.isHeld(MouseButton.PRIMARY), `is`(false))

        input.mockButtonRelease(MouseButton.PRIMARY)
    }

    @Test
    fun `Test mouse cursor in-game coordinates`() {
//        assertThat(input.mouseXUI, `is`(0.0))
//        assertThat(input.mouseYUI, `is`(0.0))
//        assertThat(input.mouseXWorld, `is`(0.0))
//        assertThat(input.mouseYWorld, `is`(0.0))
//
//        input.mockButtonPress(MouseButton.PRIMARY, 100.0, 50.0)
//
//        assertThat(input.mouseXUI, `is`(0.0))
//        assertThat(input.mouseYUI, `is`(0.0))
//        assertThat(input.mouseXWorld, `is`(100.0))
//        assertThat(input.mouseYWorld, `is`(50.0))
//
//        input.mockButtonPress(MouseButton.SECONDARY)
//
//        assertThat(input.mouseXUI, `is`(0.0))
//        assertThat(input.mouseYUI, `is`(0.0))
//        assertThat(input.mouseXWorld, `is`(100.0))
//        assertThat(input.mouseYWorld, `is`(50.0))
//
//        input.mockButtonRelease(MouseButton.PRIMARY, 50.0, 30.0)
//
//        assertThat(input.mouseXUI, `is`(0.0))
//        assertThat(input.mouseYUI, `is`(0.0))
//        assertThat(input.mouseXWorld, `is`(50.0))
//        assertThat(input.mouseYWorld, `is`(30.0))
//
//        input.mockButtonRelease(MouseButton.SECONDARY);
//
//        assertThat(input.mouseXUI, `is`(0.0))
//        assertThat(input.mouseYUI, `is`(0.0))
//        assertThat(input.mouseXWorld, `is`(50.0))
//        assertThat(input.mouseYWorld, `is`(30.0))
    }

    @Test
    fun testKeyBinding() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, KeyCode.A)

        assertThat(input.bindings.keys, hasItem(action))

        val trigger = input.bindings[action]

        assertThat(trigger is KeyTrigger, `is`(true))
        assertThat((trigger as KeyTrigger).key, `is`(KeyCode.A))
    }

    @Test
    fun testMouseBinding() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, MouseButton.PRIMARY)

        assertThat(input.bindings.keys, hasItem(action))

        val trigger = input.bindings[action]

        assertThat(trigger is MouseTrigger, `is`(true))
        assertThat((trigger as MouseTrigger).button, `is`(MouseButton.PRIMARY))
    }

    @Test
    fun `Do not allow UserActions with same name`() {
        input.addAction(object : UserAction("Action1") {}, KeyCode.A)

        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Action1") {}, KeyCode.B)
        })
    }

    @Test
    fun `Do not allow bindings to same key`() {
        input.addAction(object : UserAction("Action1") {}, KeyCode.A)

        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Action2") {}, KeyCode.A)
        })
    }

    @Test
    fun `Do not allow bindings to same button`() {
        input.addAction(object : UserAction("Action1") {}, MouseButton.PRIMARY)

        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Action2") {}, MouseButton.PRIMARY)
        })
    }

    @Test
    fun `Do not allow binding to Ctrl`() {
        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Test") {}, KeyCode.CONTROL)
        })
    }

    @Test
    fun `Do not allow binding to Shift`() {
        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Test") {}, KeyCode.SHIFT)
        })
    }

    @Test
    fun `Do not allow binding to Alt`() {
        assertThrows(IllegalArgumentException::class.java, {
            input.addAction(object : UserAction("Test") {}, KeyCode.ALT)
        })
    }

    @Test
    fun `Test rebind key`() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, KeyCode.A)
        input.addAction(object : UserAction("Action2") {}, KeyCode.B)

        // binding to existing key must not succeed
        var ok = input.rebind(action, KeyCode.B)
        assertThat(ok, `is`(false))

        ok = input.rebind(action, KeyCode.C)
        assertThat(ok, `is`(true))

        val trigger = input.bindings[action]

        assertThat(trigger is KeyTrigger, `is`(true))
        assertThat((trigger as KeyTrigger).key, `is`(KeyCode.C))
    }

    @Test
    fun `Test rebind mouse button`() {
        val action = object : UserAction("Action1") {}

        input.addAction(action, MouseButton.PRIMARY)
        input.addAction(object : UserAction("Action2") {}, MouseButton.SECONDARY)

        // binding to existing button must not succeed
        var ok = input.rebind(action, MouseButton.SECONDARY)
        assertThat(ok, `is`(false))

        ok = input.rebind(action, MouseButton.MIDDLE)
        assertThat(ok, `is`(true))

        val trigger = input.bindings[action]

        assertThat(trigger is MouseTrigger, `is`(true))
        assertThat((trigger as MouseTrigger).button, `is`(MouseButton.MIDDLE))
    }

    @Test
    fun testAddInputMapping() {
        assertThat(input.bindings.size, `is`(0))

        input.addInputMapping(InputMapping("TestAction", KeyCode.A))

        input.scanForUserActions(this)

        assertThat(input.bindings.size, `is`(1))

        val trigger = input.bindings.values.single()

        assertThat(trigger is KeyTrigger, `is`(true))
        assertThat((trigger as KeyTrigger).key, `is`(KeyCode.A))
    }

    @OnUserAction(name = "TestAction", type = ActionType.ON_ACTION_BEGIN)
    fun onCall() {

    }
}
