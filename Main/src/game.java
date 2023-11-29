import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.*;

public class game {
 public static void main(String[] ar){
  game_Frame fms = new game_Frame();
 }
}

class game_Frame extends JFrame implements KeyListener, Runnable{ 
 
int f_width ;
int f_height ;
 
GameOverScreen gameOverScreen;
int x, y; 
 
int[] cx ={0, 0, 0}; // ��� ��ũ�� �ӵ� ����� ����
int bx = 0; // ��ü ��� ��ũ�� �� ����

boolean KeyUp = false;
boolean KeyDown = false;
boolean KeyLeft = false;
boolean KeyRight = false;
boolean KeySpace = false;



int cnt; 

int player_Speed; // ������ ĳ���Ͱ� �����̴� �ӵ��� ������ ����
int missile_Speed; // �̻����� ���󰡴� �ӵ� ������ ����
int fire_Speed; // �̻��� ���� �ӵ� ���� ����
int enemy_speed; // �� �̵� �ӵ� ����
int player_Status = 0; 
// ���� ĳ���� ���� üũ ���� 0 : ����, 1: �̻��Ϲ߻�, 2: �浹
int game_Score; // ���� ���� ���
int player_Hitpoint; // �÷��̾� ĳ������ ü��



Thread th;
 
Toolkit tk = Toolkit.getDefaultToolkit();

Image[] Player_img;
//�÷��̾� �ִϸ��̼� ǥ���� ���� �̹����� �迭�� ����
Image BackGround_img; //���ȭ�� �̹���
Image[] Leaf_img; //�����̴� ���� �̹����迭
Image[] Explo_img; //��������Ʈ�� �̹����迭

Image Missile_img;
Image Enemy_img; 
 
ArrayList Missile_List = new ArrayList();
ArrayList Enemy_List = new ArrayList();
ArrayList Explosion_List = new ArrayList();
//�ټ��� ���� ����Ʈ�� ó���ϱ� ���� �迭

Image buffImage; 
Graphics buffg;

Missile ms; 
Enemy en; 

Explosion ex; //���� ����Ʈ�� Ŭ���� ���� Ű

game_Frame(){
 init();
 start();
  
 setTitle("Ninja-Chaos");
 setSize(f_width, f_height);
 gameOverScreen = new GameOverScreen(this);
  
 Dimension screen = tk.getScreenSize();

 int f_xpos = (int)(screen.getWidth() / 2 - f_width / 2);
 int f_ypos = (int)(screen.getHeight() / 2 - f_height / 2);

 setLocation(f_xpos, f_ypos);
 setResizable(false);
 setVisible(true);
}
public void init(){ 
x = 100;
y = 100;
f_width = 1000;
f_height = 460;

Missile_img = new ImageIcon("../Main/src/ImageIcon/Missile.png").getImage();
Enemy_img = new ImageIcon("../Main/src/ImageIcon/enemy.png").getImage();
//�̹��� ����� ����� ImageIcon���� ����.

Player_img = new Image[5];
int playerWidth =120;
int playerHeight =120;
for(int i = 0 ; i < Player_img.length ; ++i){
Image playerImage = 
new ImageIcon( "../Main/src/ImageIcon/f15k_" + i + ".png").getImage();
Player_img[i] = playerImage.getScaledInstance(playerWidth, playerHeight, Image.SCALE_DEFAULT);
}
//�÷��̾� �ִϸ��̼� ǥ���� ���� �����̸��� 
//�ѹ����� ���� �迭�� ��´�.

BackGround_img = new ImageIcon("../Main/src/ImageIcon/background.jpg").getImage();
//��ü ���ȭ�� �̹����� �޽��ϴ�.

Leaf_img = new Image[3];
int newWidth = 120;
int newHeight = 120;
for(int i = 0 ; i <Leaf_img.length ; ++i){
	 Image originalImage =
new ImageIcon("../Main/src/ImageIcon/leaf_" + i + ".png").getImage();
Leaf_img[i] =originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
}
//������ 3�� ���ÿ� �׸��µ� ���ǻ� �迭�� 3���� ���ÿ� �޴´�.

Explo_img = new Image[3];
for (int i = 0; i < Explo_img.length ; ++i ){
Explo_img[i] = 
new ImageIcon("../Main/src/ImageIcon/explo_" + i + ".png").getImage();
}
//���� �ִϸ��̼� ǥ���� ���� 
//�����̸��� �ѹ����� ���� �迭�� ��´�.
//��� �̹����� Swing�� ImageIcon���� �޾� �̹��� ����,���� // ���� �ٷ� ���� �� �ְ� �Ѵ�.


game_Score = 0;//���� ���ھ� �ʱ�ȭ
player_Hitpoint = 3;//���� �÷��̾� ü��
  
player_Speed = 5; //���� ĳ���� �����̴� �ӵ� ����
missile_Speed = 11; //�̻��� ������ �ӵ� ����
fire_Speed = 15; //�̻��� ���� �ӵ� ����
enemy_speed = 7;//���� ������� �ӵ� ����

}
public void start(){
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
addKeyListener(this);

th = new Thread(this); 
th.start(); 

}

public void run(){ 
try{ 
while(true){
KeyProcess(); 
EnemyProcess();
MissileProcess(); 

ExplosionProcess();//����ó�� �޼ҵ� ����

repaint(); 

Thread.sleep(20);

if (player_Hitpoint <= 0) {
    // Player's hit points are zero, show game over screen
    showGameOverScreen();
    break;
}
cnt ++;
}
}catch (Exception e){}
}
private void showGameOverScreen() {
    // Show the game over screen
    getContentPane().removeAll();
    getContentPane().add(gameOverScreen);
    validate();
    repaint();
}
public void resetGame() {
    // Reset the game state
    player_Hitpoint = 3;
    game_Score = 0;
    x = 100;
    y = 100;

    // Remove the game over screen and start a new thread
    getContentPane().removeAll();
    start();
}


class GameOverScreen extends JPanel {
    private JButton resetButton;

    GameOverScreen(game_Frame frame) {
        setLayout(new BorderLayout());

        // Create a reset button
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset the game when the button is clicked
                frame.resetGame();
            }
        });

        // Add components to the panel
        add(new JLabel("Game Over", JLabel.CENTER), BorderLayout.NORTH);
        add(resetButton, BorderLayout.CENTER);
    }
}


public void MissileProcess(){ 
if ( KeySpace ){
player_Status = 1;
//�̻����� �߻��ϸ� �÷��̾� ĳ���� ���¸� 1�� ����.

if( ( cnt % fire_Speed ) == 0){
//�÷��̾��� �̻��� ����ӵ��� �����Ѵ�.

ms = new Missile(x+150, y+30, missile_Speed);
//�̻��� �̵� �ӵ� ���� �߰��� �޴´�
Missile_List.add(ms); 
}
}

for ( int i = 0 ; i < Missile_List.size() ; ++i){
ms = (Missile) Missile_List.get(i);
ms.move();
if ( ms.x > f_width - 20 ){
Missile_List.remove(i);
}

for (int j = 0 ; j < Enemy_List.size(); ++ j){
en = (Enemy) Enemy_List.get(j);

if (Crash(ms.x, ms.y, en.x, en.y, Missile_img, Enemy_img)) {
//�̻����� ��ǥ �� �̹�������, 
//���� ��ǥ�� �̹��� ������ �޾�
//�浹���� �޼ҵ�� �ѱ�� true,false���� 
//���� �޾� true�� �Ʒ��� �����մϴ�.

Missile_List.remove(i);
Enemy_List.remove(j);

game_Score += 10; //���� ������ +10��.

ex = new Explosion(en.x + Enemy_img.getWidth(null) / 2, en.y + Enemy_img.getHeight(null) / 2 , 0);
//���� ��ġ���ִ� ���� �߽� ��ǥ x,y ���� 
//���� ������ ���� �� ( 0 �Ǵ� 1 )�� �޽��ϴ�.
//���� ���� �� - 0 : ���� , 1 : �ܼ� �ǰ� 

Explosion_List.add(ex); 
//�浹�������� ����� ���� ��ġ�� 
//����Ʈ�� �߰��Ѵ�.

}
}
}
}

public void EnemyProcess(){

for (int i = 0 ; i < Enemy_List.size() ; ++i ){ 
en = (Enemy)(Enemy_List.get(i)); 
en.move(); 
if(en.x < -200){ 
Enemy_List.remove(i); 
}

if(Crash(x, y, en.x, en.y, Player_img[0], Enemy_img)){
//�÷��̾�� ���� �浹�� �����Ͽ�
//boolean���� ���� �޾� true�� �Ʒ��� �����մϴ�. 

player_Hitpoint --; //�÷��̾� ü���� 1����ϴ�.
Enemy_List.remove(i); //���� �����մϴ�.
game_Score += 10; 
//���ŵ� ������ ���ӽ��ھ 10 ������ŵ�ϴ�.

ex = new Explosion(en.x + Enemy_img.getWidth(null) / 2, en.y + Enemy_img.getHeight(null) / 2, 0 );
//���� ��ġ���ִ� ���� �߽� ��ǥ x,y ���� 
//���� ������ ���� �� ( 0 �Ǵ� 1 )�� �޽��ϴ�.
//���� ���� �� - 0 : ���� , 1 : �ܼ� �ǰ� 

Explosion_List.add(ex); 
//���ŵ� ����ġ�� ���� ����Ʈ�� �߰��մϴ�.

ex = new Explosion(x, y, 1 );
//���� ��ġ���ִ� ���� �߽� ��ǥ x,y ���� 
//���� ������ ���� �� ( 0 �Ǵ� 1 )�� �޽��ϴ�.
//���� ���� �� - 0 : ���� , 1 : �ܼ� �ǰ� 

Explosion_List.add(ex);
//�浹�� �÷��̾��� ��ġ�� �浹�� ����Ʈ�� �߰�.

}
}
if ( cnt % 200 == 0 ){ 
en = new Enemy(f_width + 100, 100, enemy_speed);
Enemy_List.add(en); 
en = new Enemy(f_width + 100, 200, enemy_speed);
Enemy_List.add(en);
en = new Enemy(f_width + 100, 300, enemy_speed);
Enemy_List.add(en);
en = new Enemy(f_width + 100, 400, enemy_speed);
Enemy_List.add(en);
en = new Enemy(f_width + 100, 500, enemy_speed);
Enemy_List.add(en);
//�� ������ �ӵ��� �߰��� �޾� ���� �����Ѵ�.

}
}

 public void ExplosionProcess(){
  // ���� ����Ʈ ó���� �޼ҵ�
  
  for (int i = 0 ;  i < Explosion_List.size(); ++i){
   ex = (Explosion) Explosion_List.get(i);
   ex.effect();
   //����Ʈ �ִϸ��̼��� ��Ÿ��������
   //����Ʈ ó�� �߰��� �߻��ϸ� �ش� �޼ҵ带 ȣ��.
   
  }
 }


public boolean Crash(int x1, int y1, int x2, int y2, Image img1, Image img2){
//���� �浹 ���� �ҽ��� �����մϴ�.
//���� �̹��� ������ �ٷ� �޾� �ش� �̹����� ����, ���̰���
//�ٷ� ����մϴ�.

boolean check = false;

if ( Math.abs( ( x1 + img1.getWidth(null) / 2 )  
- ( x2 + img2.getWidth(null) / 2 ))  
< ( img2.getWidth(null) / 2 + img1.getWidth(null) / 2 )
 && Math.abs( ( y1 + img1.getHeight(null) / 2 )  
- ( y2 + img2.getHeight(null) / 2 ))  
< ( img2.getHeight(null)/2 + img1.getHeight(null)/2 ) ){
//�̹��� ����, ���̰��� �ٷ� �޾� ����մϴ�.


check = true;//�� ���� true�� check�� true�� �����մϴ�.
}else{ check = false;}

return check; //check�� ���� �޼ҵ忡 ���� ��ŵ�ϴ�.

 }


public void paint(Graphics g){
buffImage = createImage(f_width, f_height); 
buffg = buffImage.getGraphics();

update(g);
}

public void update(Graphics g){

Draw_Background(); //��� �̹��� �׸��� �޼ҵ� ����
Draw_Player(); //�÷��̾ �׸��� �޼ҵ� �̸� ����

Draw_Enemy(); 
Draw_Missile();

Draw_Explosion();//��������Ʈ�׸��� �޼ҵ� ����
Draw_StatusText();//���� ǥ�� �ؽ�Ʈ�� �׸��� �޼ҵ� ����

g.drawImage(buffImage, 0, 0, this); 
}

public void Draw_Background() {
    buffg.clearRect(
   
0, 0, f_width, f_height);

    int numCopies = (f_width / BackGround_img.getWidth(null)) + 2;

    for (int i = 0; i < numCopies; i++) {
        int xPosition = bx + i * BackGround_img.getWidth(null);
        buffg.drawImage(BackGround_img, xPosition, 0, this);
    }

    for (int i = 0; i < cx.length; ++i) {
        if (cx[i] < 1400) {
            cx[i] += 5 + i * 3;
        } else {
            cx[i] = 0;
        }

        buffg.drawImage(Leaf_img[i], 1200 - cx[i], 50 + i * 200, this);
    }
}
public void Draw_Player(){ 

switch (player_Status){ 

case 0 : // ����
if((cnt / 5 %2) == 0){ 
buffg.drawImage(Player_img[1], x, y, this);

}else { buffg.drawImage(Player_img[2], x, y, this); }
//�����ʿ��� ���� �մ� �̹����� ������ �׷��ش�.

break;

case 1 : // �̻��Ϲ߻�
if((cnt / 5 % 2) == 0){ 
buffg.drawImage(Player_img[3], x, y, this);
}else { buffg.drawImage(Player_img[4], x, y, this); }
//�̻����� ��µ��� �̹����� ������ �׷��ش�.

player_Status = 0;
//�̻��� ��Ⱑ ������ �÷��̾� ���¸� 0���� ������. 

break;

case 2 : // �浹
break;

}

}

public void Draw_Missile(){
for (int i = 0 ; i < Missile_List.size()  ; ++i){
ms = (Missile) (Missile_List.get(i)); 
buffg.drawImage(Missile_img, ms.x, ms.y, this); 
}
}

public void Draw_Enemy(){ 
for (int i = 0 ; i < Enemy_List.size() ; ++i ){
en = (Enemy)(Enemy_List.get(i));
buffg.drawImage(Enemy_img, en.x, en.y, this);
}
}

public void Draw_Explosion(){
//���� ����Ʈ�� �׸��� �κ� �Դϴ�.

for (int i = 0 ; i < Explosion_List.size() ; ++i ){
ex = (Explosion)Explosion_List.get(i);
//���� ����Ʈ�� ���� ������ üũ�Ͽ� ����Ʈ�� ����.

if (ex.damage == 0){
// �������� 0 �̸� ���߿� �̹��� �׸���

if ( ex.ex_cnt < 7  ) {
buffg.drawImage( Explo_img[0], ex.x - 
Explo_img[0].getWidth(null) / 2, ex.y - 
Explo_img[0].getHeight(null) / 2, this);
}else if ( ex.ex_cnt < 14 ) {
buffg.drawImage(Explo_img[1], ex.x - 
Explo_img[1].getWidth(null) / 2, ex.y - 
Explo_img[1].getHeight(null) / 2, this);
}else if ( ex.ex_cnt < 21 ) {
buffg.drawImage(Explo_img[2], ex.x - 
Explo_img[2].getWidth(null) / 2, ex.y -
Explo_img[2].getHeight(null) / 2, this);
}else if( ex.ex_cnt > 21 ) {
Explosion_List.remove(i);
ex.ex_cnt = 0;
//������ ���� ī���͸� ����Ͽ�
//�̹����� ���������� �׸�.
}
}else { //�������� 1�̸� �ܼ� �ǰݿ� �̹��� �׸���
if ( ex.ex_cnt < 7  ) {
buffg.drawImage(Explo_img[0], ex.x + 120,
ex.y + 15, this);
}else if ( ex.ex_cnt < 14 ) {
buffg.drawImage(Explo_img[1], ex.x + 60, 
ex.y + 5, this);
}else if ( ex.ex_cnt < 21 ) {
buffg.drawImage(Explo_img[0], ex.x + 5,
ex.y + 10, this);
}else if( ex.ex_cnt > 21 ) {
Explosion_List.remove(i);
ex.ex_cnt = 0;
//�ܼ� �ǰ� ���� ���������� �̹����� �׸�����
//������ ���� �ణ �ٸ� ������� �׸��ϴ�.

}
}
}
}

public void Draw_StatusText(){ //���� üũ��  �ؽ�Ʈ�� �׸��ϴ�.

buffg.setFont(new Font("Defualt", Font.BOLD, 20));
//��Ʈ ������ �մϴ�.  �⺻��Ʈ, ����, ������ 20

int xCoordinate = 20;
int yCoordinate = 30;

buffg.drawString("SCORE: " + game_Score, xCoordinate, yCoordinate+ 20);
//��ǥ x : 1000, y : 70�� ���ھ ǥ���մϴ�.

buffg.drawString("HitPoint: " + player_Hitpoint, xCoordinate, yCoordinate + 40);
//��ǥ x : 1000, y : 90�� �÷��̾� ü���� ǥ���մϴ�.

buffg.drawString("Missile Count: " + Missile_List.size(), xCoordinate, yCoordinate + 60);
//��ǥ x : 1000, y : 110�� ��Ÿ�� �̻��� ���� ǥ���մϴ�.

buffg.drawString("Enemy Count: " + Enemy_List.size(), xCoordinate, yCoordinate + 80);
//��ǥ x : 1000, y : 130�� ��Ÿ�� ���� ���� ǥ���մϴ�.

}
public void KeyProcess(){
if(KeyUp == true) {
if( y > 20 ) y -= 5;
//ĳ���Ͱ� �������� ȭ�� ���� �� �Ѿ�� �մϴ�.

player_Status = 0;
//�̵�Ű�� �������� �÷��̾� ���¸� 0���� �����ϴ�.
}

if(KeyDown == true) {
if( y+ Player_img[0].getHeight(null) < f_height ) y += 5;
//ĳ���Ͱ� �������� ȭ�� �Ʒ��� �� �Ѿ�� �մϴ�.

player_Status = 0;
//�̵�Ű�� �������� �÷��̾� ���¸� 0���� �����ϴ�.
}

if(KeyLeft == true) {
if ( x > 0 ) x -= 5;
//ĳ���Ͱ� �������� ȭ�� �������� �� �Ѿ�� �մϴ�.

player_Status = 0;
//�̵�Ű�� �������� �÷��̾� ���¸� 0���� �����ϴ�.
}

if(KeyRight == true) {
if ( x + Player_img[0].getWidth(null) < f_width ) x += 5;
//ĳ���Ͱ� �������� ȭ�� ���������� �� �Ѿ�� �մϴ�.

player_Status = 0;
//�̵�Ű�� �������� �÷��̾� ���¸� 0���� �����ϴ�.
}
}

public void keyPressed(KeyEvent e){
  
switch(e.getKeyCode()){
case KeyEvent.VK_UP :
KeyUp = true;
break;
case KeyEvent.VK_DOWN :
KeyDown = true;
break;
case KeyEvent.VK_LEFT :
KeyLeft = true;
break;
case KeyEvent.VK_RIGHT :
KeyRight = true;
break;

case KeyEvent.VK_SPACE :
KeySpace = true;
break;
}
}
public void keyReleased(KeyEvent e){
  
switch(e.getKeyCode()){
case KeyEvent.VK_UP :
KeyUp = false;
break;
case KeyEvent.VK_DOWN :
KeyDown = false;
break;
case KeyEvent.VK_LEFT :
KeyLeft = false;
break;
case KeyEvent.VK_RIGHT :
KeyRight = false;
break;

case KeyEvent.VK_SPACE :
KeySpace = false;
break;

}
}
public void keyTyped(KeyEvent e){}



}

class Missile{
int x;
int y; 

int speed; // �̻��� ���ǵ� ������ �߰�.
 
Missile(int x, int y, int speed) {
this.x = x; 
this.y = y;

this.speed = speed;
// ��ü ������ �ӵ� ���� �߰��� �޽��ϴ�.

}
public void move(){
x += speed; // �̻��� ���ǵ� �ӵ� ��ŭ �̵�
}
}

class Enemy{ 
int x;
int y;

int speed; // �� �̵� �ӵ� ������ �߰�

Enemy(int x, int y, int speed ) {
this.x = x;
this.y = y;

this.speed = speed;
// ��ü ������ �ӵ� ���� �߰��� �޽��ϴ�.

}
public void move(){ 
x -= speed;// ���̵��ӵ���ŭ �̵�
}
}

class Explosion{ 
// �������� ���� �̹����� �׸������� Ŭ������ �߰��Ͽ� ��ü���� 

int x; //�̹����� �׸� x ��ǥ
int y; //�̹����� �׸� y ��ǥ
int ex_cnt; //�̹����� ���������� �׸��� ���� ī����
int damage; //�̹��� ������ �����ϱ� ���� ������

Explosion(int x, int y, int damage){
this.x = x;
this.y = y;
this.damage = damage;
ex_cnt = 0;
}
public void effect(){
ex_cnt ++; //�ش� �޼ҵ� ȣ�� �� ī���͸� +1 ��Ų��.
}
}
