
package demos.j2d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.*;
import java.awt.geom.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.j2d.*;

import demos.common.*;
import demos.util.*;
import java.awt.MouseInfo;
import java.awt.Point;
import javax.swing.JFrame;

/** Shows how to place 2D text in 3D using the TextRenderer. */


public class JOGLTextCubeDemo extends JFrame implements GLEventListener, KeyListener, MouseListener {
  private float xAng;
  private float yAng;
  private GLU glu = new GLU();
  private Time time;
  private TextRenderer renderer;
  private FPSCounter fps;
  private float textScaleFactor;
  private Point pickPoint = new Point();
  private boolean mousePressed = false;
  
public static class drawingTranslations
  {
    public float x; 
    public float y;  
    public float z; 
    
    public drawingTranslations(float x, float y, float z)
    {
        this.x = x;
        this.y= y;
        this.z= z;
    }
  };
static drawingTranslations[] dTranslation = { 
        //first face------------------------------
        new drawingTranslations( 0,0,0 ),
        new drawingTranslations( 1.1f,0,0),
        new drawingTranslations( 1.1f,0,0 ),
        new drawingTranslations( 0,1.1f,0),
        new drawingTranslations( -1.1f,0,0 ),
        new drawingTranslations( -1.1f,0,0),
        new drawingTranslations( 0,1.1f,0 ),
        new drawingTranslations( 1.1f,0,0),
        new drawingTranslations( 1.1f,0,0),
        //second face----------------------------
        new drawingTranslations( 0,0,1.1f ),
        new drawingTranslations( -1.1f,0,0),
        new drawingTranslations( -1.1f,0,0 ),
        new drawingTranslations( 0,-1.1f,0),
        new drawingTranslations( 1.1f,0,0 ),
        new drawingTranslations( 1.1f,0,0),
        new drawingTranslations( 0,-1.1f,0 ),
        new drawingTranslations( -1.1f,0,0),
        new drawingTranslations( -1.1f,0,0),
        //third face-----------------------------
        new drawingTranslations( 0,0,1.1f ),
        new drawingTranslations( 1.1f,0,0),
        new drawingTranslations( 1.1f,0,0 ),
        new drawingTranslations( 0,1.1f,0),
        new drawingTranslations( -1.1f,0,0 ),
        new drawingTranslations( -1.1f,0,0),
        new drawingTranslations( 0,1.1f,0 ),
        new drawingTranslations( 1.1f,0,0),
        new drawingTranslations( 1.1f,0,0),
    };

  
  public static drawingTranslations [] dTranslations;

  public static void main(String[] args) {
    Frame frame = new Frame("Text Cube");
    frame.setLayout(new BorderLayout());

    GLCanvas canvas = new GLCanvas();
    final JOGLTextCubeDemo demo = new JOGLTextCubeDemo();

    canvas.addGLEventListener(demo);
    frame.add(canvas, BorderLayout.CENTER);
    canvas.addKeyListener(demo);
    canvas.addMouseListener(demo);

    frame.setSize(1024, 1024);
    
    final Animator animator = new Animator(canvas);
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          // Run this on another thread than the AWT event queue to
          // make sure the call to Animator.stop() completes before
          // exiting
          new Thread(new Runnable() {
              public void run() {
                animator.stop();
                System.exit(0);
              }
            }).start();
        }
      });
    frame.show();
    animator.start();
  }

  public void init(GLAutoDrawable drawable) {
    renderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 72));
    GL gl = drawable.getGL();
    gl.glEnable(GL.GL_DEPTH_TEST);

    // Compute the scale factor of the largest string which will make
    // them all fit on the faces of the cube
    Rectangle2D bounds = renderer.getBounds("Bottom");
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    textScaleFactor = 0*1.0f / (w * 1.1f);
    fps = new FPSCounter(drawable, 36);

    time = new SystemTime();
    ((SystemTime) time).rebase();
    gl.setSwapInterval(0);
    
    
  }

  public void display(GLAutoDrawable drawable) {
    GL gl = drawable.getGL();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    
    //gl.glMatrixMode(GL.GL_MODELVIEW);
    //gl.glLoadIdentity();
    //glu.gluLookAt(40, 60, 100,
    //              0, 0, 0,
    //              0, 1, 0);
    //glu.gluPerspective(45, 1, -5, 100);
    
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(60,1,0.5,50);
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glLoadIdentity();
    glu.gluLookAt(4,6,10, 0,0,0, 0,1,0);
    
    // Base translation of cube
    //gl.glTranslatef(0,0,-50);
    
    // Base rotation of cube
    gl.glRotatef(xAng, 1, 0, 0);
    gl.glRotatef(yAng, 0, 1, 0);

    for (int i = 0; i < 27; i++)
    {
        gl.glTranslatef(dTranslation[i].x, dTranslation[i].y,dTranslation[i].z);
        // Six faces of cube2
        // Top face  
        gl.glPushMatrix();
        gl.glRotatef(-90, 1, 0, 0);
        drawFace(gl, 1.0f, 0.2f, 0.2f, 0.8f, "Top");
        gl.glPopMatrix();
        // Front face
        drawFace(gl, 1.0f, 0.8f, 0.2f, 0.2f, "Front");
        // Right face
        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        drawFace(gl, 1.0f, 0.2f, 0.8f, 0.2f, "Right");
        // Back face    
        gl.glRotatef(90, 0, 1, 0);
        drawFace(gl, 1.0f, 0.8f, 0.8f, 0.2f, "Back");
        // Left face    
        gl.glRotatef(90, 0, 1, 0);
        drawFace(gl, 1.0f, 0.2f, 0.8f, 0.8f, "Left");
        gl.glPopMatrix();
        // Bottom face
        gl.glPushMatrix();
        gl.glRotatef(90, 1, 0, 0);
        drawFace(gl, 1.0f, 0.8f, 0.2f, 0.8f, "Bottom");
        gl.glPopMatrix();
    }
    
    fps.draw();
    if(mousePressed){
        double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
        double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
        yAng += (float) +mouseX - pickPoint.x;
        xAng += (float) +mouseY - pickPoint.y;
    }
    pickPoint.x = (int)MouseInfo.getPointerInfo().getLocation().getX();
    pickPoint.y = (int)MouseInfo.getPointerInfo().getLocation().getY();  
    time.update();
   // xAng += 50 * (float) time.deltaT();
   // yAng += 40 * (float) time.deltaT();
  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL gl = drawable.getGL();
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(15, (float) width / (float) height, 5, 15);
  }

  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

  private void drawFace(GL gl,
                        float faceSize,
                        float r, float g, float b,
                        String text) {
    float halfFaceSize = faceSize / 2;
    // Face is centered around the local coordinate system's z axis,
    // at a z depth of faceSize / 2
    gl.glColor3f(r, g, b);
    gl.glBegin(GL.GL_QUADS);
    gl.glVertex3f(-halfFaceSize, -halfFaceSize, halfFaceSize);
    gl.glVertex3f( halfFaceSize, -halfFaceSize, halfFaceSize);
    gl.glVertex3f( halfFaceSize,  halfFaceSize, halfFaceSize);
    gl.glVertex3f(-halfFaceSize,  halfFaceSize, halfFaceSize);
    gl.glEnd();

    // Now draw the overlaid text. In this setting, we don't want the
    // text on the backward-facing faces to be visible, so we enable
    // back-face culling; and since we're drawing the text over other
    // geometry, to avoid z-fighting we disable the depth test. We
    // could plausibly also use glPolygonOffset but this is simpler.
    // Note that because the TextRenderer pushes the enable state
    // internally we don't have to reset the depth test or cull face
    // bits after we're done.
    renderer.begin3DRendering();
    gl.glDisable(GL.GL_DEPTH_TEST);
    gl.glEnable(GL.GL_CULL_FACE);
    // Note that the defaults for glCullFace and glFrontFace are
    // GL_BACK and GL_CCW, which match the TextRenderer's definition
    // of front-facing text.
    Rectangle2D bounds = renderer.getBounds(text);
    float w = (float) bounds.getWidth();
    float h = (float) bounds.getHeight();
    renderer.draw3D(text,
                    w / -2.0f * textScaleFactor,
                    h / -2.0f * textScaleFactor,
                    halfFaceSize,
                    textScaleFactor);
    renderer.end3DRendering();
  }
  public void keyTyped(KeyEvent e) {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void keyPressed(KeyEvent e) {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void keyReleased(KeyEvent e) {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseClicked(MouseEvent e) {
     //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mousePressed(MouseEvent mouse) {
        mousePressed = true;
    }

    public void mouseReleased(MouseEvent mouse) {
        mousePressed = false;
        
    }

    public void mouseEntered(MouseEvent mouse) {
        if(mouse.getButton() == MouseEvent.BUTTON1){
            pickPoint = mouse.getPoint();
            System.out.println("x = " + pickPoint.x +" / y = " + pickPoint.y );
            xAng += 1 * (float) -pickPoint.x + mouse.getX();
            yAng += 1 * (float) -pickPoint.y + mouse.getY();
        }
    }
    public void mouseExited(MouseEvent e) {
      //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
