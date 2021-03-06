package demos.j2d;

import java.util.Random;
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
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

import demos.common.*;
import demos.util.*;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JFrame;
import sun.audio.AudioStream;
import sun.audio.*;
import java.io.*;
import javax.swing.Timer;
/** Shows how to place 2D text in 3D using the TextRenderer. */


public class RubiksCubeGenius extends JFrame implements GLEventListener, KeyListener, MouseListener {
  private float xAng;
  private float yAng;
  private GLU glu = new GLU();
  private Time time;
  private TextRenderer renderer;
  private FPSCounter fps;
  private float textScaleFactor;
  private Point pickPoint = new Point();
  private boolean mousePressed = false;
  static private int pontos;
  Random rand = new Random();
  private static int[] seq = new int[100];
  private static int state = 0; //0 = inicio; 1 =sequenciacomp; 2 = resposta usuario; 3=score+fim de jogo
  private static boolean aceso = false; //dificuldade atual
  private static int level = 1;  //fase atual, quantos vai acender no total
  private static int indice = 0;//indice de quem vai ser o proximo a ser aceso
  private static int colorToShine = 0;
  
  
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
        new drawingTranslations( -2.2f,0,0 ),
        new drawingTranslations( 0,1.1f,0),
        new drawingTranslations( 1.1f,0,0 ),
        new drawingTranslations( 1.1f,0,0),
        new drawingTranslations( 0,-2.2f,0 ),
        new drawingTranslations( -1.1f,0,0),
        new drawingTranslations( -1.1f,0,0),
        //second face----------------------------
        new drawingTranslations( 0,0,1.1f ),
        new drawingTranslations( 1.1f,0,0),
        new drawingTranslations( 1.1f,0,0 ),
        new drawingTranslations( 0,1.1f,0),
        new drawingTranslations( -1.1f,0,0 ),
        new drawingTranslations( -1.1f,0,0),
        new drawingTranslations( 0,1.1f,0 ),
        new drawingTranslations( 1.1f,0,0),
        new drawingTranslations( 1.1f,0,0),
        //third face-----------------------------
        new drawingTranslations( 0,0,-2.2f ),
        new drawingTranslations( -1.1f,0,0),
        new drawingTranslations( -1.1f,0,0 ),
        new drawingTranslations( 0,-1.1f,0),
        new drawingTranslations( 1.1f,0,0 ),
        new drawingTranslations( 1.1f,0,0),
        new drawingTranslations( 0,-1.1f,0 ),
        new drawingTranslations( -1.1f,0,0),
        new drawingTranslations( -1.1f,0,0),
    };

  
  public static drawingTranslations [] dTranslations;
  public static String[] gongFile = new String[6];
  public static AudioStream audioStream;
  public static Timer genius;
  public static Timer userTimer;
  
  public static void main(String[] args) throws Exception{
    Frame frame = new Frame("Rubik's Cube Genius");
    frame.setLayout(new BorderLayout());

    GLCanvas canvas = new GLCanvas();
    final RubiksCubeGenius demo = new RubiksCubeGenius();

    canvas.addGLEventListener(demo);
    frame.add(canvas, BorderLayout.CENTER);
    canvas.addKeyListener(demo);
    
    canvas.addMouseListener(demo);    
    
    ActionListener iteracaoComp = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aceso = !aceso;
                if (aceso) //� para acender
                {
                    //baixar e dar play na musica
                    colorToShine = seq[indice];
                try{
                    audioStream = new AudioStream(new FileInputStream(gongFile[colorToShine-1]));
                    AudioPlayer.player.start(audioStream);
                }
                catch(Exception ex)
                {}                    
                }
                else
                {
                    System.out.println(seq[indice]);
                    colorToShine = 0;                                        
                    indice++;
                    System.out.println(indice+" "+level);
                    
                    if (indice >= level)
                    {
                        System.out.println("entrei vacilao");
                        state = 2;
                        indice = 0; //indice passa a ser do usu�rio
                        genius.stop();
                    }
                    
                }
            }
        };
    
    genius = new Timer(1000, iteracaoComp);
    userTimer = new Timer(1000, new ActionListener(){
        public void actionPerformed(ActionEvent evt)
        {
            if (colorToShine == seq[indice])
            {
                pontos+=10;
                indice++;
                if (indice >= level)
                {
                    indice = 0;
                    level++;
                    state = 1;
                    genius.start();
                }
            }
            else
            {
                state = 3; //game over!!!
            }            
            colorToShine = 0;
            userTimer.stop();
        }
    });
    
    
    
    frame.setSize(800, 800);
    
    
    
    
//    genius.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() 
//            {
//                if (state == 0 || state == 3)
//            }
//    }, 4*60*5, 4*60*5);
    
    gongFile[0] = "sounds\\01.wav";
    gongFile[1] = "sounds\\02.wav";
    gongFile[2] = "sounds\\03.wav";
    gongFile[3] = "sounds\\04.wav";
    gongFile[4] = "sounds\\05.wav";
    gongFile[5] = "sounds\\06.wav";
    // create an audiostream from the inputstream

    // play the audio clip with the audioplayer class
    
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
    
    // Gerando sequencia de jogo
    seq[0] = 0;
    for(indice = 0; indice < 100; indice++){
        seq[indice] = rand.nextInt(6) + 1;
    }
    indice = 0;
  }

  public void display(GLAutoDrawable drawable){
    GL gl = drawable.getGL();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    
    if(state != 3){
        // Texto
        TextRenderer textRenderer = new TextRenderer(new Font("Verdana", Font.BOLD, 15));
        textRenderer.beginRendering(900, 700);
        // AMARELO
        textRenderer.setColor(Color.YELLOW);
        textRenderer.setSmoothing(true);
        textRenderer.draw("Amarelo", (int) (30), (int) (50));
        textRenderer.draw("1", (int) (65), (int) (30));
        // Vermelho
        textRenderer.setColor(Color.RED);
        textRenderer.setSmoothing(true);
        textRenderer.draw("Vermelho", (int) (105), (int) (50));
        textRenderer.draw("2", (int) (145), (int) (30));
        // Azul
        textRenderer.setColor(Color.BLUE);
        textRenderer.setSmoothing(true);
        textRenderer.draw("Azul", (int) (190), (int) (50));
        textRenderer.draw("3", (int) (205), (int) (30));
        // Verde
        textRenderer.setColor(Color.GREEN);
        textRenderer.setSmoothing(true);
        textRenderer.draw("Verde", (int) (235), (int) (50));
        textRenderer.draw("4", (int) (255), (int) (30));
        // Rosa
        textRenderer.setColor(Color.PINK);
        textRenderer.setSmoothing(true);
        textRenderer.draw("Rosa", (int) (290), (int) (50));
        textRenderer.draw("5", (int) (308), (int) (30));
        // WHITE
        textRenderer.setColor(Color.WHITE);
        textRenderer.setSmoothing(true);
        textRenderer.draw("Branco", (int) (340), (int) (50));
        textRenderer.draw("6", (int) (355), (int) (30));
        // Instrucoes
        textRenderer.draw("Instru��es:", (int) (30), (int) (620));
        textRenderer.draw("-> Pressione a tecla 'P' para iniciar o jogo", (int) (30), (int) (600));
        textRenderer.draw("-> Use os numeros de 1-6 no teclado para", (int) (30), (int) (580));
        textRenderer.draw("     selecionar as cores, siga a sequencia do cubo.", (int) (30), (int) (560));
        textRenderer.draw("-> Clique e arraste para movimentar o cubo e",(int)(30), (int)(540));
        textRenderer.draw("     enchergar as demais faces.", (int) (30), (int)(520));
        
        textRenderer.endRendering();
        // Texto
        TextRenderer textRenderer2 = new TextRenderer(new Font("Verdana", Font.BOLD, 25));
        textRenderer2.beginRendering(900, 700);
        textRenderer2.draw("Pontos : " + pontos, (int) (700), (int) (50));
        textRenderer2.draw("Rubik's Cube GENIUS", (int) (30), (int) (650));
        textRenderer2.endRendering();
    }
    
    
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(60,1,0.5,50);
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glLoadIdentity();
    glu.gluLookAt(4,6,10, 0,0,0, 0,1,0);
    
    // Base rotation of cube
    gl.glRotatef(xAng, 1, 0, 0);
    gl.glRotatef(yAng, 0, 1, 0);

    for (int i = 0; i < 27; i++)
    {
        if(state != 3){
            gl.glTranslatef(dTranslation[i].x, dTranslation[i].y,dTranslation[i].z);
            // Top face  
            gl.glPushMatrix();
            gl.glRotatef(-90, 1, 0, 0);
            if(colorToShine == 3)
            {
                drawFace(gl, 1.0f, 0.2f, 0.2f, 1.0f, "DBlue");      
            }
            else
                drawFace(gl, 1.0f, 0.4f, 0.4f, 0.8f, "DBlue");
            gl.glPopMatrix();
            // Front face
            if(colorToShine == 2)
            {   
                drawFace(gl, 1.0f, 1.0f, 0.2f, 0.2f, "Red");
            }
            else
                drawFace(gl, 1.0f, 0.8f, 0.4f, 0.4f, "Red");
            // Right face
            gl.glPushMatrix();
            gl.glRotatef(90, 0, 1, 0);
            if(colorToShine == 4)
            {
                drawFace(gl, 1.0f, 0.2f, 1.0f, 0.2f, "Green");
            }
            else
                drawFace(gl, 1.0f, 0.4f, 0.7f, 0.4f, "Green");
            // Back face    
            gl.glRotatef(90, 0, 1, 0);
            if(colorToShine == 1)
            {
                drawFace(gl, 1.0f, 0.8f, 0.8f, 0.2f, "Yellow");
            }
            else
                drawFace(gl, 1.0f, 0.7f, 0.7f, 0.4f, "Yellow");
            // Left face    
            gl.glRotatef(90, 0, 1, 0);
            if(colorToShine == 6)
            {
                drawFace(gl, 1.0f, 1.0f, 1.0f, 1.0f, "White");
            }
            else
                drawFace(gl, 1.0f, 0.65f, 0.65f, 0.65f,"White");
            gl.glPopMatrix();
            // Bottom face
            gl.glPushMatrix();
            gl.glRotatef(90, 1, 0, 0);
            if(colorToShine == 5)
            {    
                drawFace(gl, 1.0f, 0.8f, 0.2f, 0.8f, "Pink");  
            }
            else
                drawFace(gl, 1.0f, 0.8f, 0.5f, 0.8f, "Pink");
            gl.glPopMatrix();
        }
        else
        {
            TextRenderer textRenderer3 = new TextRenderer(new Font("Verdana", Font.BOLD, 20));
            textRenderer3.beginRendering(900, 700);
            // AMARELO
            textRenderer3.setColor(Color.WHITE);
            textRenderer3.setSmoothing(true);
            textRenderer3.draw("GAME OVER", (int) (400), (int) (500));
            textRenderer3.draw("Pontos : " +pontos, (int) (400), (int) (450));
            textRenderer3.endRendering();
        }
    }
    
    fps.draw();
    if(mousePressed){        
        double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
        double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
        yAng += 0.5f * ((float) +mouseX - pickPoint.x);
        xAng += 0.5f * ((float) +mouseY - pickPoint.y);
    }
    pickPoint.x = (int)MouseInfo.getPointerInfo().getLocation().getX();
    pickPoint.y = (int)MouseInfo.getPointerInfo().getLocation().getY();  
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
        if (state == 2)
        {
            colorToShine = e.getKeyCode()-48; //pega a cor
            if (colorToShine < 0 || colorToShine > 7)
            {
                colorToShine = 0;
            }
            else
            {
                try{
                    audioStream = new AudioStream(new FileInputStream(gongFile[colorToShine-1]));
                    AudioPlayer.player.start(audioStream);
                }
                catch(Exception ex)
                {}  
                System.out.println(colorToShine+"FOI O USU�RIO");
                userTimer.start();
            }
        }
         if (e.getKeyCode() == 80 && state == 0)
         {
             System.out.println("come�ou!");
             state = 1;
             genius.start();
         }   
    }

    public void keyReleased(KeyEvent e) {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseClicked(MouseEvent e) {
     //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mousePressed(MouseEvent mouse){
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
