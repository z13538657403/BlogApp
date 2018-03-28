package org.zzrblog.blogapp.objects;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import org.zzrblog.blogapp.data.IndexBuffer;
import org.zzrblog.blogapp.data.VertexBuffer;
import org.zzrblog.blogapp.program.BallShaderProgram;

import java.util.ArrayList;

/**
 * Created by zzr on 2018/3/23.
 */

public class Ball {
    private static final int POSITION_COORDIANTE_COMPONENT_COUNT = 3; // 每个顶点的坐标数 x y z

    private Context context;
    IndexBuffer indexBuffer;
    VertexBuffer vertexBuffer;
    BallShaderProgram ballShaderProgram;
    public float[] modelMatrix = new float[16];
    private int numElements = 0; // 记录要画多少个三角形

    public Ball(Context context){
        this.context = context;
        Matrix.setIdentityM(modelMatrix,0);
        initVertexData();
        buildProgram();
        setAttributeStatus();
    }

    private void initVertexData() {
        final int angleSpan = 5;// 将球进行单位切分的角度，此数值越小划分矩形越多，球面越趋近平滑
        final float radius = 1.0f;// 球体半径
        short offset = 0;
        ArrayList<Float> vertexList = new ArrayList<>(); // 使用list存放顶点数据
        ArrayList<Short> indexList = new ArrayList<>();// 顶点索引数组
        for (int vAngle = 0; vAngle < 180; vAngle = vAngle + angleSpan)
        {
            for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + angleSpan)
            {
                // 左上角 0
                float x0 = (float) (radius * Math.sin(Math.toRadians(vAngle)) * Math.cos(Math
                        .toRadians(hAngle)));
                float y0 = (float) (radius * Math.sin(Math.toRadians(vAngle)) * Math.sin(Math
                        .toRadians(hAngle)));
                float z0 = (float) (radius * Math.cos(Math.toRadians(vAngle)));
                vertexList.add(x0);
                vertexList.add(y0);
                vertexList.add(z0);
                // 右上角 1
                float x1 = (float) (radius * Math.sin(Math.toRadians(vAngle)) * Math.cos(Math
                        .toRadians(hAngle + angleSpan)));
                float y1 = (float) (radius * Math.sin(Math.toRadians(vAngle)) * Math.sin(Math
                        .toRadians(hAngle + angleSpan)));
                float z1 = (float) (radius * Math.cos(Math.toRadians(vAngle)));
                vertexList.add(x1);
                vertexList.add(y1);
                vertexList.add(z1);
                // 右下角 2
                float x2 = (float) (radius * Math.sin(Math.toRadians(vAngle + angleSpan)) * Math
                        .cos(Math.toRadians(hAngle + angleSpan)));
                float y2 = (float) (radius * Math.sin(Math.toRadians(vAngle + angleSpan)) * Math
                        .sin(Math.toRadians(hAngle + angleSpan)));
                float z2 = (float) (radius * Math.cos(Math.toRadians(vAngle + angleSpan)));
                vertexList.add(x2);
                vertexList.add(y2);
                vertexList.add(z2);
                // 左下角 3
                float x3 = (float) (radius * Math.sin(Math.toRadians(vAngle + angleSpan)) * Math
                        .cos(Math.toRadians(hAngle)));
                float y3 = (float) (radius * Math.sin(Math.toRadians(vAngle + angleSpan)) * Math
                        .sin(Math.toRadians(hAngle)));
                float z3 = (float) (radius * Math.cos(Math.toRadians(vAngle + angleSpan)));
                vertexList.add(x3);
                vertexList.add(y3);
                vertexList.add(z3);

                indexList.add((short)(offset + 0));
                indexList.add((short)(offset + 3));
                indexList.add((short)(offset + 2));
                indexList.add((short)(offset + 0));
                indexList.add((short)(offset + 2));
                indexList.add((short)(offset + 1));

                offset += 4; // 4个顶点的偏移
            }
        }

        numElements = indexList.size();// 记录有多少个索引点

        float[] data_vertex = new float[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++) {
            data_vertex[i] = vertexList.get(i);
        }
        vertexBuffer = new VertexBuffer(data_vertex);

        short[] data_index = new short[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            data_index[i] = indexList.get(i);
        }
        indexBuffer = new IndexBuffer(data_index);
    }

    private void buildProgram() {
        ballShaderProgram = new BallShaderProgram(context);
        ballShaderProgram.userProgram();
    }

    private void setAttributeStatus() {
        vertexBuffer.setVertexAttributePointer(
                ballShaderProgram.aPositionLocation,
                POSITION_COORDIANTE_COMPONENT_COUNT,
                0, 0 );
    }

    public void draw(float[] modelViewProjectionMatrix) {
        ballShaderProgram.userProgram();
        setAttributeStatus();
        // 将最终变换矩阵写入
        ballShaderProgram.setUniforms(modelViewProjectionMatrix);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getIndexBufferId());
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numElements, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
