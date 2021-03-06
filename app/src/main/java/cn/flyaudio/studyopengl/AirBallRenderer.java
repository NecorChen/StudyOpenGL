package cn.flyaudio.studyopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by necorchen on 18-6-28.
 */

public class AirBallRenderer implements GLSurfaceView.Renderer {
    float[] tableVerticesWithTriangles = {
//            //左边三角形
//            -0.5f, -0.5f,
//            0.5f, 0.5f,
//            -0.5f, 0.5f,
//            //右边三角形
//            -0.5f, -0.5f,
//            0.5f, 0.5f,
//            0.5f, -0.5f,
//两个点连成直线
//            -0.5f,0f,
//            0.5f,0f,
//
//            //两个小点
//            0f,-0.25f,
//            0f,0.25f
//            0f,0f,1f,1f,1f,
//            -0.5f,-0.5f,0.7f,0.7f,0.7f,
//            0.5f,-0.5f,0.7f,0.7f,0.7f,
//            0.5f,0.5f,0.7f,0.7f,0.7f,
//            -0.5f,0.5f,0.7f,0.7f,0.7f,
//            -0.5f,-0.5f,0.7f,0.7f,0.7f,
            -0.25f,0f,0.7f,0.7f,0.7f,
            -0.5f,-0.8f,0.7f,0.7f,0.7f,
            0f,-0.8f,0.7f,0.7f,0.7f,
            0f,0.8f,0.7f,0.7f,0.7f,
            -0.5f,0.8f,0.7f,0.7f,0.7f,
            -0.5f,-0.8f,0.7f,0.7f,0.7f,

            0f,0.25f,0.7f,0.7f,0.7f,
            0f,-0.8f,0.7f,0.7f,0.7f,
            0.5f,-0.8f,0.7f,0.7f,0.7f,
            0.5f,0.8f,0.7f,0.7f,0.7f,
            0f,0.8f,0.7f,0.7f,0.7f,
            0f,-0.8f,0.7f,0.7f,0.7f,

            //两个点连成直线
            -0.5f,0f,1f,0f,0f,
            0.5f,0f,1f,0f,0f,

            //两个小点
            0f,-0.25f,0f,0f,1f,
            0f,0.25f,1f,0f,0f
    };

    private Context mContext;
    private int program;
    private static final String A_COLOR = "a_Color";
    private static final String A_POSITION = "a_Position";
    private static final String U_MATRIX = "u_Matrix";
    private static  final  int BYTES_PER_FLOAT =4;
    private static  final  int POSITION_COMPNENT_COUNT =2;
    private static  final  int COLOR_COMPNENT_COUNT =3;
    private   int uMatrixLocation ;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private int aColorLocation;
    private int aPositionLocation;
    private static final int STRIDE = (POSITION_COMPNENT_COUNT+COLOR_COMPNENT_COUNT)*BYTES_PER_FLOAT;
    private FloatBuffer vertexData = null;

    private String vertexShaderSource ;
    private String fragmentShaderSource ;

    public AirBallRenderer(Context context) {
        this.mContext =context;

        vertexShaderSource = TextResourceReader.
                readTextFileFromResorce(mContext,R.raw.simple_vertex_shader);
        fragmentShaderSource = TextResourceReader.
                readTextFileFromResorce(mContext,R.raw.simple_fragment_shader);


        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length*BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }



    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);
        int vertexShader = ShaderHelper.
                compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.
                compileFragmentShader(fragmentShaderSource);
        program = ShaderHelper.linkProgram(vertexShader,fragmentShader);
        if(LoggerConfig.ON){
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);

        aColorLocation = glGetAttribLocation(program,A_COLOR);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation,POSITION_COMPNENT_COUNT,GL_FLOAT,
                false,STRIDE,vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPNENT_COUNT);
        glVertexAttribPointer(aColorLocation,COLOR_COMPNENT_COUNT,GL_FLOAT,
                false,STRIDE,vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
//        final float aspectRation = width>height ?
//                                (float)width/(float) height:
//                                (float)height/(float) width;
//
//        if(width>height){
//            orthoM(projectionMatrix,0,-aspectRation,aspectRation,-1f,1f,-1f,1f);
//        }else {
//            orthoM(projectionMatrix,0,-1f,1f,-aspectRation,aspectRation,-1f,1f);
//        }

        MatrixHelper.perspectiveM(projectionMatrix,45,
                (float)width/(float) height,1f,10f);
        setIdentityM(modelMatrix,0);
        translateM(modelMatrix,0,0f,0f,-2.5f);
        rotateM(modelMatrix,0,-60f,1f,0f,0f);

        final  float[] temp = new float[16];
        multiplyMM(temp,0,projectionMatrix,0,modelMatrix,0);
        System.arraycopy(temp,0,projectionMatrix,0,temp.length);


    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0);
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
        glDrawArrays(GL_TRIANGLE_FAN,7,6);
        glDrawArrays(GL_LINES,12,2);
        glDrawArrays(GL_POINTS,14,1);
        glDrawArrays(GL_POINTS,15,1);
    }
}
