package site.qifen.android_samples;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * 安卓opengl官网实例，目前滑动旋转有问题
 */

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView glSurfaceView = new MyGLSurfaceView(this);

        setContentView(glSurfaceView);


        Toast.makeText(this,stringFromJNI(),Toast.LENGTH_LONG).show();
    }

    public static class  MyGLSurfaceView  extends GLSurfaceView {


        private  MyGLRenderer myGLRenderer;

        MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(3);
            myGLRenderer = new MyGLRenderer();
            setRenderer(myGLRenderer);

            // Render the view only when there is a change in the drawing data.
            // To allow the triangle to rotate automatically, this line is commented out:
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        }


        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private float previousX;
        private float previousY;

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            float x = e.getX();
            float y = e.getY();

            if (e.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = x - previousX;
                float dy = y - previousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2f) {
                    dx = dx * -1;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2f) {
                    dy = dy * -1;
                }

                myGLRenderer.setAngle(
                        myGLRenderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
            }

            previousX = x;
            previousY = y;
            return true;
        }





    }

    public static class MyGLRenderer implements GLSurfaceView.Renderer {

        public volatile float mAngle;

        public float getAngle() {
            return mAngle;
        }

        public void setAngle(float angle) {
            mAngle = angle;
        }


        Triangle triangle;
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES30.glClearColor(0f,0f,0f,1f);

            triangle = new Triangle();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES30.glViewport(0,0,width,height);

            float ratio = (float) width / height;

            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            float[] scratch = new float[16];
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);


            // Set the camera position (View matrix)
            Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            // Calculate the projection and view transformation
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);



            // Create a rotation transformation for the triangle
            long time = SystemClock.uptimeMillis() % 4000L;
            float angle = 0.090f * ((int) time);
            Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, -1.0f);

            // Combine the rotation matrix with the projection and camera view
            // Note that the vPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);


            // Set the camera position (View matrix)
            Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            // Calculate the projection and view transformation
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

            // Draw shape



            //draw shape
            triangle.draw(vPMatrix);
        }
        private float[] rotationMatrix = new float[16];

        // vPMatrix is an abbreviation for "Model View Projection Matrix"
        private final float[] vPMatrix = new float[16];
        private final float[] projectionMatrix = new float[16];
        private final float[] viewMatrix = new float[16];

        public static int loadShader(int type, String shaderCode){

            // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
            int shader = GLES30.glCreateShader(type);

            // add the source code to the shader and compile it
            GLES30.glShaderSource(shader, shaderCode);
            GLES30.glCompileShader(shader);

            return shader;
        }


    }


    //定义三角形
    public static class Triangle {


//        private final String vertexShaderCode =
//                "attribute vec4 vPosition;" +
//                        "void main() {" +
//                        "  gl_Position = vPosition;" +
//                        "}";

        private final String fragmentShaderCode =
                "precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}";


        private final String vertexShaderCode =
                // This matrix member variable provides a hook to manipulate
                // the coordinates of the objects that use this vertex shader
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "void main() {" +
                        // the matrix must be included as a modifier of gl_Position
                        // Note that the uMVPMatrix factor *must be first* in order
                        // for the matrix multiplication product to be correct.
                        "  gl_Position = uMVPMatrix * vPosition;" +
                        "}";

        // Use to access and set the view transformation
        private int vPMatrixHandle;



        // number of coordinates per vertex in this array
        static final int COORDS_PER_VERTEX = 3;
        static float[] triangleCoords = {   // in counterclockwise order:
                0.0f,  0.622008459f, 0.0f, // top
                -0.5f, -0.311004243f, 0.0f, // bottom left
                0.5f, -0.311004243f, 0.0f  // bottom right
        };

        // Set color with red, green, blue and alpha (opacity) values
        float[] color = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

        private final int mProgram;
        private final FloatBuffer vertexBuffer;

        public Triangle() {
            // initialize vertex byte buffer for shape coordinates
            ByteBuffer bb = ByteBuffer.allocateDirect(
                    // (number of coordinate values * 4 bytes per float)
                    triangleCoords.length * 4);
            // use the device hardware's native byte order
            bb.order(ByteOrder.nativeOrder());

            // create a floating point buffer from the ByteBuffer
            vertexBuffer = bb.asFloatBuffer();
            // add the coordinates to the FloatBuffer
            vertexBuffer.put(triangleCoords);
            // set the buffer to read the first coordinate
            vertexBuffer.position(0);



            int vertexShader = MyGLRenderer.loadShader(GLES30.GL_VERTEX_SHADER,
                    vertexShaderCode);
            int fragmentShader = MyGLRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER,
                    fragmentShaderCode);

            // create empty OpenGL ES Program
            mProgram = GLES30.glCreateProgram();

            // add the vertex shader to program
            GLES30.glAttachShader(mProgram, vertexShader);

            // add the fragment shader to program
            GLES30.glAttachShader(mProgram, fragmentShader);

            // creates OpenGL ES program executables
            GLES30.glLinkProgram(mProgram);

        }

        private int positionHandle;
        private int colorHandle;

        private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
        private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

        public void draw(float[] mvpMatrix) {
            // Add program to OpenGL ES environment
            GLES30.glUseProgram(mProgram);

            // get handle to vertex shader's vPosition member
            positionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");

            // Enable a handle to the triangle vertices
            GLES30.glEnableVertexAttribArray(positionHandle);

            // Prepare the triangle coordinate data
            GLES30.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                    GLES30.GL_FLOAT, false,
                    vertexStride, vertexBuffer);

            // get handle to fragment shader's vColor member
            colorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");

            // get handle to shape's transformation matrix
            vPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

            // Set color for drawing the triangle
            GLES30.glUniform4fv(colorHandle, 1, color, 0);

            // Draw the triangle
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);

            // Disable vertex array
            GLES30.glDisableVertexAttribArray(positionHandle);


            // Pass the projection and view transformation to the shader
            GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

            // Draw the triangle
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);

            // Disable vertex array
            GLES30.glDisableVertexAttribArray(positionHandle);
        }



    }


    public static class Square {

        // number of coordinates per vertex in this array
        static final int COORDS_PER_VERTEX = 3;
        static float[] squareCoords = {
                -0.5f,  0.5f, 0.0f,   // top left
                -0.5f, -0.5f, 0.0f,   // bottom left
                0.5f, -0.5f, 0.0f,   // bottom right
                0.5f,  0.5f, 0.0f }; // top right

        public Square() {
            // initialize vertex byte buffer for shape coordinates
            ByteBuffer bb = ByteBuffer.allocateDirect(
                    // (# of coordinate values * 4 bytes per float)
                    squareCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());
            FloatBuffer vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(squareCoords);
            vertexBuffer.position(0);

            // initialize byte buffer for the draw list
            // order to draw vertices
            short[] drawOrder = {0, 1, 2, 0, 2, 3};
            ByteBuffer dlb = ByteBuffer.allocateDirect(
                    // (# of coordinate values * 2 bytes per short)
                    drawOrder.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            ShortBuffer drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(drawOrder);
            drawListBuffer.position(0);
        }
    }




    public native String stringFromJNI();

}