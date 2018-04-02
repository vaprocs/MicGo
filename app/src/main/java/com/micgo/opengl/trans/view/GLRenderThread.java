package com.micgo.opengl.trans.view;//package com.changba.record.recording.view.view;

import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;

import com.micgo.R;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by liuhongtian on 17/12/23.
 */

public class GLRenderThread extends Thread {
    private AtomicBoolean mShouldRender;
    private AtomicBoolean mShouldPause;
    private SurfaceTexture mSurfaceTexture;
    private GLRenderer mRenderer;

    private EGL10 mEgl;
    private EGLDisplay mEglDisplay = EGL10.EGL_NO_DISPLAY;
    private EGLContext mEglContext = EGL10.EGL_NO_CONTEXT;
    private EGLSurface mEglSurface = EGL10.EGL_NO_SURFACE;

    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final int EGL_OPENGL_ES2_BIT = 4;

    public interface GLRenderer {
        void drawFirstFrame();
        void drawFrame();
    }

    public GLRenderThread(SurfaceTexture surfaceTexture, GLRenderer renderer, AtomicBoolean shouldRender)
    {
        mSurfaceTexture = surfaceTexture;
        mRenderer = renderer;
        mShouldRender = shouldRender;
        mShouldPause = new AtomicBoolean(false);
    }

    private void initGL()
    {
        mEgl = (EGL10)EGLContext.getEGL();

        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetdisplay failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int []version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int []configAttribs = {
                EGL10.EGL_BUFFER_SIZE, 32,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT,
                EGL10.EGL_NONE
        };

        int []numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, configAttribs, configs, 1, numConfigs)) {
            throw new RuntimeException("eglChooseConfig failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int []contextAttribs = {
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], EGL10.EGL_NO_CONTEXT, contextAttribs);
        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], mSurfaceTexture, null);
        if (mEglSurface == EGL10.EGL_NO_SURFACE || mEglContext == EGL10.EGL_NO_CONTEXT) {
            int error = mEgl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                throw new RuntimeException("eglCreateWindowSurface returned  EGL_BAD_NATIVE_WINDOW. " );
            }
            throw new RuntimeException("eglCreateWindowSurface failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed : " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

    }

    public void setShouldRender(boolean shouldRender) {
        if (mShouldRender != null) {
            mShouldRender.set(shouldRender);
        }
    }

    public void setShouldPause(boolean shouldPause) {
        if (mShouldPause != null) {
            mShouldPause.set(shouldPause);
        }
    }

    private void destoryGL()
    {
        mEgl.eglDestroyContext(mEglDisplay, mEglContext);
        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
        mEglContext = EGL10.EGL_NO_CONTEXT;
        mEglSurface = EGL10.EGL_NO_SURFACE;
    }

    public void run()
    {
        initGL();

        if (mRenderer != null) {
            ((GLRendererImpl)mRenderer).initGL(R.drawable.record_song_bg_1); // 为了加载提速
            drawFirstFrame();
            ((GLRendererImpl)mRenderer).loadTexture(new int[]{R.drawable.record_song_bg_2, R.drawable.record_song_bg_3});
        }

        while (mShouldRender != null && mShouldRender.get() != false) {
            if (mShouldPause != null && mShouldPause.get() == false) {
                draw();
            }
            try {
                sleep(60);
            } catch(InterruptedException e) {

            }
        }

        destoryGL();
    }

    private void drawFirstFrame() {
        if (mRenderer != null)
            mRenderer.drawFirstFrame();
        mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
    }

    private void draw() {
        if (mRenderer != null)
            mRenderer.drawFrame();
        mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
    }
}
