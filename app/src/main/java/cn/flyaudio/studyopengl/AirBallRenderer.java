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
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by necorchen on 18-6-28.
 */

public class AirBallRenderer implements GLSurfaceView.Renderer {
    float[] tablevertices = {
            0f, 0f,
            0f, 14f,
            9f, 14f,
            9f, 0f
    };
    float[] tableVerticesWithTriangles = {
            //左边三角形
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
            //右边三角形
            -0.5f, -0.5f,
            0.5f, 0.5f,
            0.5f, -0.5f,

            //两个点连成直线
            -0.5f,0f,
            0.5f,0f,

            //两个小点
            0f,-0.25f,
            0f,0.25f
    };

    private Context mContext;
    private int program;
    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private static  final  int BYTES_PER_FLOAT =4;
    private static  final  int POSITION_COMPNENT_COUNT =2;
    private int uColorLocation;
    private int aPositionLocation;
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
        uColorLocation = glGetUniformLocation(program,U_COLOR);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation,POSITION_COMPNENT_COUNT,GL_FLOAT,
                false,0,vertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniform4f(uColorLocation,1f,1f,1f,1f);
        glDrawArrays(GL_TRIANGLES,0,6);
        glUniform4f(uColorLocation,1f,0f,0f,1f);
        glDrawArrays(GL_LINES,6,2);
        glUniform4f(uColorLocation,0f,0f,1f,1f);
        glDrawArrays(GL_POINTS,8,1);
        glUniform4f(uColorLocation,1f,0f,0f,1f);
        glDrawArrays(GL_POINTS,9,1);
    }
}
