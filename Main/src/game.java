import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.*;

public class game {
	public static void main(String[] ar) {
		game_Frame fms = new game_Frame(); // fms에 객체 할당
	}
}

//JFrame 클래스를 상속하고 KeyListener, Runnable 인터페이스를 구현
class game_Frame extends JFrame implements KeyListener, Runnable {
 
    int f_width; // 프레임 가로
	int f_height; // 프레임 세로

	int x, y;

	int[] cx = { 0, 0, 0 }; // 배경 스크롤 속도 제어용 변수
	int bx = 0; // 전체 배경 스크롤 용 변수

	boolean KeyUp = false; // 위쪽 방향키 입력 여부를 나타내는 변수
	boolean KeyDown = false; // 아래쪽 방향키 입력 여부를 나타내는 변수
	boolean KeyLeft = false; // 왼쪽 방향키 입력 여부를 나타내는 변수
	boolean KeyRight = false; // 오른쪽 방향키 입력 여부를 나타내는 변수
	boolean KeySpace = false; // 스페이스바 입력 여부를 나타내는 변수

	int cnt; // 카운터 변수

	int player_Speed; // 유저의 캐릭터가 움직이는 속도를 조절할 변수
	int missile_Speed; // 미사일이 날라가는 속도 조절할 변수
	int fire_Speed; // 미사일 연사 속도 조절 변수
	int enemy_speed; // 적 이동 속도 설정
	int player_Status = 0;
	// 유저 캐릭터 상태 체크 변수 0 : 평상시, 1: 미사일발사, 2: 충돌
	int game_Score; // 게임 점수 계산
	int player_Hitpoint; // 플레이어 캐릭터의 체력

	Thread th; // 게임의 주요 기능을 수행할 스레드를 나타내는 변수

	Toolkit tk = Toolkit.getDefaultToolkit(); // GUI 컴포넌트 및 그래픽 객체를 다루기 위한 툴팃 객체

	Image[] Player_img;
	//플레이어 애니메이션 표현을 위해 이미지를 배열로 받음
	Image BackGround_img; // 배경화면 이미지
	Image[] Leaf_img; // 움직이는 배경용 이미지배열
	Image[] Explo_img; // 폭발이펙트용 이미지배열

	Image Missile_img; // 미사일의 이미지를 나타내는 객체
	Image Enemy_img; // 적의 이미지를 나타내는 객체

	ArrayList Missile_List = new ArrayList(); // 미사일 객체를 저장하기 위한 ArrayList
	ArrayList Enemy_List = new ArrayList(); // 적 객체를 저장하기 위한 ArrayLsit
	ArrayList Explosion_List = new ArrayList(); // 폭발 효과를 저장하기 위한 ArrayList
	//다수의 폭발 이펙트를 처리하기 위한 배열

	Image buffImage;
	Graphics buffg;

	Missile ms; // 미사일 객체
	Enemy en; // 적 객체

	Explosion ex; // 폭발 이펙트용 클래스 접근 키

	game_Frame() {
		init(); // 초기화 메소드 호출
		start(); // 스레드 시작 메소드 호출

		setTitle("Ninja-Chaos"); // 프레임 제목 설정
		setSize(f_width, f_height); // 프레임 크기 설정

		Dimension screen = tk.getScreenSize(); // 화면 크기 받아옴.

		int f_xpos = (int) (screen.getWidth() / 2 - f_width / 2); // 프레임 x위치 계산
		int f_ypos = (int) (screen.getHeight() / 2 - f_height / 2); // 프레임 y위치 계싼

		setLocation(f_xpos, f_ypos); // 계산된 위치로 프레임 이동
		setResizable(false); // 프레임 크기 고정
		setVisible(true); // 프레임을 화면에 표시
	}

	public void init() {
		x = 100; // 초기 x좌표 설정
		y = 100; // 초기 y좌표 설정
		f_width = 1000; // 프레임 가로 크기 설정
		f_height = 460; // 프레임 세로 크기 설정

		Missile_img = new ImageIcon("../Main/src/ImageIcon/Missile.png").getImage();
		Enemy_img = new ImageIcon("../Main/src/ImageIcon/enemy.png").getImage();
		//이미지 만드는 방식을 ImageIcon으로 변경한 후 이미지를 불러옴

		Player_img = new Image[5]; // 플레이어 애니메이션을 위한 이미지 배열
		int playerWidth = 120; // 플레이어 이미지 가로 크기
		int playerHeight = 120; // 플레이어 이미지 세로 크기
		for (int i = 0; i < Player_img.length; ++i) {
			Image playerImage = new ImageIcon("../Main/src/ImageIcon/f15k_" + i + ".png").getImage();
			Player_img[i] = playerImage.getScaledInstance(playerWidth, playerHeight, Image.SCALE_DEFAULT);
		}
		//플레이어 애니메이션 표현을 위해 파일이름을 
		//넘버마다 나눠 배열로 담음
		//플레이어 이미지 배열을 반복하며 움직임 구현

		BackGround_img = new ImageIcon("../Main/src/ImageIcon/background.jpg").getImage();
		//전체 배경화면 이미지를 받음

		Leaf_img = new Image[3]; // 나뭇잎 애니매이션을 위한 이미지 배열
		int newWidth = 120; // 나뭇잎 가로
		int newHeight = 120; // 나뭇잎 세로
		for (int i = 0; i < Leaf_img.length; ++i) {
			Image originalImage = new ImageIcon("../Main/src/ImageIcon/leaf_" + i + ".png").getImage();
			Leaf_img[i] = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
		}
		//나뭇잎 애니메이션 표현을 위해 파일이름을
		//넘버마다 나눠 배열로 담는다.
		//나뭇잎 이미지 배열을 반복하며 움직임 구현

		Explo_img = new Image[3];
		for (int i = 0; i < Explo_img.length; ++i) {
			Explo_img[i] = new ImageIcon("../Main/src/ImageIcon/explo_" + i + ".png").getImage();
		}
		//폭발 애니메이션 표현을 위해 
		//파일이름을 넘버마다 나눠 배열로 담는다.
		//Swing의 ImageIcon으로 받아 이미지 넓이,높이 값을 바로 얻을 수 있게 한다.

		game_Score = 0;// 게임 스코어 초기화
		player_Hitpoint = 3;// 최초 플레이어 체력

		player_Speed = 5; // 유저 캐릭터 움직이는 속도 설정
		missile_Speed = 11; // 미사일 움직임 속도 설정
		fire_Speed = 15; // 미사일 연사 속도 설정
		enemy_speed = 7;// 적이 날라오는 속도 설정

	}

	// start 메서드	 
	public void start() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(this); // 현재 프레임에 KeyListener 추가

		th = new Thread(this); // 클래스 스래드 생성, 객체 초기화
		th.start(); // 스레드 시작
	}

	/*
	 * run 메소드 - 게임의 메인 루프를 담당하는 메소드 - 주기적으로 키 이벤트, 적 처리, 미사일 처리, 폭발 이펙트 처리 등을 수행하며
	 * 게임 상태를 갱신 - 특정 점수 달성 시 적의 속도를 조절 - 플레이어의 체력이 0 이하일 때 게임 오버 화면을 표시하고 루프를 종료
	 */
	public void run() {
		try {
			while (player_Hitpoint > -1) {
				KeyProcess(); // 키 이벤트 처리 메소드 호출
				EnemyProcess(); // 적 처리 메소드 호출
				MissileProcess(); // 미사일 처리 메소드 호출
				ExplosionProcess(); // 폭발 이펙트 처리 메소드 호출
				repaint(); // 화면 갱신을 위한 repaint 호출
				Thread.sleep(20); // 일시 정지 (20밀리초)

				// 게임 점수에 따른 적 속도 조절
				if (game_Score == 200) {
					enemy_speed = 12;
				} else if (game_Score == 300) {
					enemy_speed = 15;
				} else if (game_Score == 400) {
					enemy_speed = 18;
				}

				cnt++; // 카운터 증가

				// 플레이어 체력이 0 이하이면 게임 오버 처리 후 루프 종료
				if (player_Hitpoint <= 0) {
					showGameOver(); // 게임 오버 화면 표시
					break; // 루프 종료
				}
			}
		} catch (Exception e) { // 예외처리
			e.printStackTrace();
		}
	}

	public void showGameOver() {
		// 게임 종료를 알리는 팝업 메시지를 표시
		JOptionPane.showMessageDialog(this, "Game Over!", "Game Over", JOptionPane.INFORMATION_MESSAGE);

		// 시스템 정상 종료
		System.exit(0);
	}

	public void MissileProcess() {
		if (KeySpace) {
			player_Status = 1;
			//미사일을 발사하면 플레이어 캐릭터 상태를 1로 변경.

			if ((cnt % fire_Speed) == 0) {
				//플레이어의 미사일 연사속도를 조절한다.

				ms = new Missile(x + 150, y + 30, missile_Speed);
				//미사일 이동 속도 값을 추가로 받는다
				Missile_List.add(ms);
			}
		}

		for (int i = 0; i < Missile_List.size(); ++i) {
			// 미사일 리스트에서 미사일 객체를 가져옴
			ms = (Missile) Missile_List.get(i);

			// 미사일을 이동
			ms.move();

			// 미사일이 화면 오른쪽 끝에 도달했다면 리스트에서 제거
			if (ms.x > f_width - 20) {
				Missile_List.remove(i);
			}

			for (int j = 0; j < Enemy_List.size(); ++j) {
				//적 리스트에서 적 객체를 가져옴
				en = (Enemy) Enemy_List.get(j);

				//미사일과 적이 충돌했는지 확인하는 메소드 호출
				if (Crash(ms.x, ms.y, en.x, en.y, Missile_img, Enemy_img)) {
					//미사일의 좌표 및 이미지파일, 
					//적의 좌표및 이미지 파일을 받아
					//충돌판정 메소드로 넘기고 true,false값을 
					//리턴 받아 true면 아래를 실행

					Missile_List.remove(i); // 충돌 시 미사일과 적을 리스트에서 제거
					Enemy_List.remove(j);

					game_Score += 10; // 게임 점수를 +10점.

					ex = new Explosion(en.x + Enemy_img.getWidth(null) / 2, en.y + Enemy_img.getHeight(null) / 2, 0);
					//적이 위치해있는 곳의 중심 좌표 x,y 값과 
					//폭발 설정을 받은 값 ( 0 또는 1 )을 받음
					//폭발 설정 값 - 0 : 폭발 , 1 : 단순 피격 

					Explosion_List.add(ex);
					//충돌판정으로 사라진 적의 위치에 
					//이펙트를 추가

				}
			}
		}
	}

	public void EnemyProcess() {

		for (int i = 0; i < Enemy_List.size(); ++i) {
			en = (Enemy) (Enemy_List.get(i)); // 적 객체를 리스트에서 가져옴
			en.move(); // 적을 이동
			if (en.x < -200) { // 적이 화면을 벗어나면 해당 적을 리스트에서 제거
				Enemy_List.remove(i);
			}

			if (Crash(x, y, en.x, en.y, Player_img[0], Enemy_img)) {
				//플레이어와 적의 충돌을 판정하여
				//boolean값을 리턴 받아 true면 아래를 실행

				player_Hitpoint--; // 플레이어 체력 1 감소
				Enemy_List.remove(i); // 적을 제거
				game_Score += 10;
				//적이 제거되면 게임스코어를 10 증가

				ex = new Explosion(en.x + Enemy_img.getWidth(null) / 2, en.y + Enemy_img.getHeight(null) / 2, 0);
				//적이 위치해있는 곳의 중심 좌표 x,y 값과 
				//폭발 설정을 받은 값 ( 0 또는 1 )을 받음
				//폭발 설정 값 - 0 : 폭발 , 1 : 단순 피격 

				Explosion_List.add(ex);
				//제거된 적위치에 폭발 이펙트를 추가

				ex = new Explosion(x, y, 1);
				//적이 위치해있는 곳의 중심 좌표 x,y 값과 
				//폭발 설정을 받은 값 ( 0 또는 1 )을 받음
				//폭발 설정 값 - 0 : 폭발 , 1 : 단순 피격 

				Explosion_List.add(ex);
				//충돌시 플레이어의 위치에 충돌용 이펙트를 추가

			}
		}
		// 주기(200프레임)마다 새로운 적(Enemy)을 생성하여 리스트에 추가
		// 생성된 적은 화면의 오른쪽 바깥쪽에 위치하며, 각각 다른 y 좌표로 생성
		// 이동 속도는 `enemy_speed` 변수로 설정
		if (cnt % 200 == 0) {
			en = new Enemy(f_width + 100, 65, enemy_speed);
			Enemy_List.add(en);
			en = new Enemy(f_width + 100, 135, enemy_speed);
			Enemy_List.add(en);
			en = new Enemy(f_width + 100, 210, enemy_speed);
			Enemy_List.add(en);
			en = new Enemy(f_width + 100, 285, enemy_speed);
			Enemy_List.add(en);
			en = new Enemy(f_width + 100, 350, enemy_speed);
			Enemy_List.add(en);
			//적 움직임 속도를 추가로 받아 적을 생성

		}
	}

	public void ExplosionProcess() {
		// 폭발 이펙트 처리용 메소드

		for (int i = 0; i < Explosion_List.size(); ++i) {
			ex = (Explosion) Explosion_List.get(i);
			ex.effect();
			// 이펙트 애니메이션을 나타내기위해
			// 이펙트 처리 추가가 발생하면 해당 메소드를 호출

		}
	}

	/**
	 * 두 이미지 간의 충돌 여부를 확인하는 메소드 이미지의 중심 좌표와 크기를 이용하여 충돌 여부를 판단
	 *
	 * @param x1   이미지 1의 x 좌표
	 * @param y1   이미지 1의 y 좌표
	 * @param img1 이미지 1
	 * @param x2   이미지 2의 x 좌표
	 * @param y2   이미지 2의 y 좌표
	 * @param img2 이미지 2
	 * @return 이미지 간의 충돌 여부 (true: 충돌, false: 미충돌)
	 */

	public boolean Crash(int x1, int y1, int x2, int y2, Image img1, Image img2) {
		//기존 충돌 판정 소스를 변경
		//이제 이미지 변수를 바로 받아 해당 이미지의 넓이, 높이값 계산
		

		//이미지의 중심 좌표와 크기를 이용하여 충돌 여부 판단
		boolean check = false;

		if (Math.abs((x1 + img1.getWidth(null) / 2) - (x2 + img2.getWidth(null) / 2)) < (img2.getWidth(null) / 2
				+ img1.getWidth(null) / 2)
				&& Math.abs((y1 + img1.getHeight(null) / 2)
						- (y2 + img2.getHeight(null) / 2)) < (img2.getHeight(null) / 2 + img1.getHeight(null) / 2)) {
			//이미지 넓이, 높이값을 바로 받아 계산

			check = true;// 위 값이 true면 check에 true를 전달
		} else {
			check = false;
		}

		return check; // check의 값을 메소드에 리턴 시킵니다.

	}

	/**
	 * 화면을 그리는 메소드 화면 크기에 맞게 버퍼 이미지를 생성하고 업데이트된 내용을 그림
	 *
	 * @param g Graphics 객체
	 */

	public void paint(Graphics g) {
		buffImage = createImage(f_width, f_height);
		buffg = buffImage.getGraphics();

		update(g);
	}

	public void update(Graphics g) {

		Draw_Background(); // 배경 이미지 그리기 메소드 실행
		Draw_Player(); // 플레이어를 그리는 메소드 이름 변경

		Draw_Enemy(); //적 이미지 그리기 메서드 실생
		Draw_Missile(); //미사일 이미지 그리기 메서드 실행

		Draw_Explosion();// 폭발이펙트그리기 메소드 실행
		Draw_StatusText();// 상태 표시 텍스트를 그리는 메소드 실행

		g.drawImage(buffImage, 0, 0, this);
	}

	
	/**
	 * 배경을 그리는 메소드. 화면을 지우고 배경 이미지를 반복하여 그림.
	 * 또한, 일정한 간격으로 이동하는 요소(cx)를 처리.
	 */
	public void Draw_Background() { 
		buffg.clearRect(0, 0, f_width, f_height); //화면을 지움

		//배경 이미지를 반복하여 그림
		int numCopies = (f_width / BackGround_img.getWidth(null)) + 2;
		
		//무한 스크롤 구현
		for (int i = 0; i < numCopies; i++) {
			//현재 배경 이미지의 x좌표 위치 계산
			int xPosition = bx + i * BackGround_img.getWidth(null);
			//x좌표에 배경이미지 생성
			buffg.drawImage(BackGround_img, xPosition, 0, this);
		}

		//나뭇잎 이미지를 배열에 따라 일정한 간격으로 이동하면서 그립
		for (int i = 0; i < cx.length; ++i) {
			if (cx[i] < 1400) {
				//일정한 간격으로 이동
				cx[i] += 5 + i * 3;
			} else {
				//화면을 벗어난 경우 초기 위치로 설정
				cx[i] = 0;
			}
			//잎사귀 이미지를 화면에 그림
			buffg.drawImage(Leaf_img[i], 1200 - cx[i], 50 + i * 200, this);
		}
	}

	/**
	 * 플레이어의 상태에 따라 적절한 이미지를 그림
	 * 상태 0: 평상시 - 일정 주기로 이미지를 번갈아가며 그림
	 * 상태 1: 닌자 창 발사 - 일정 주기로 창을 쏘는 듯한 이미지를 번갈아가며 그림
	 * 상태 2: 충돌
	 */
	public void Draw_Player() {
	    switch (player_Status) {
	        case 0: // 평상시
	            if ((cnt / 5 % 2) == 0) {
	                buffg.drawImage(Player_img[1], x, y, this);
	            } else {
	                buffg.drawImage(Player_img[2], x, y, this);
	            }
	            break;

	        case 1: // 닌자 창 발사
	            if ((cnt / 5 % 2) == 0) {
	                buffg.drawImage(Player_img[3], x, y, this);
	            } else {
	                buffg.drawImage(Player_img[4], x, y, this);
	            }
	            // 창을 쏘는 듯한 이미지를 번갈아 그림
	            player_Status = 0;
	            // 칭 쏘기가 끝나면 플레이어 상태를 0으로 돌림
	            break;

	        case 2: // 충돌 
	            break;
	    }
	}
	
	//창 생성
	public void Draw_Missile() {
		for (int i = 0; i < Missile_List.size(); ++i) {
			ms = (Missile) (Missile_List.get(i));
			buffg.drawImage(Missile_img, ms.x, ms.y, this);
		}
	}
	//적 생성
	public void Draw_Enemy() {
		for (int i = 0; i < Enemy_List.size(); ++i) {
			en = (Enemy) (Enemy_List.get(i));
			buffg.drawImage(Enemy_img, en.x, en.y, this);
		}
	}

	/**
	 * Draw_Explosion 메서드는 폭발 이펙트를 그리는 역할
	 * Explosion_List에 있는 각각의 Explosion 객체에 대해 폭발 이펙트를 그림
	 * 일정 조건을 충족할 경우 해당 객체를 리스트에서 제거
	 */
	public void Draw_Explosion() {
    //폭발 이펙트를 생성

		for (int i = 0; i < Explosion_List.size(); ++i) {
			ex = (Explosion) Explosion_List.get(i);
			//폭발 이펙트의 존재 유무를 체크하여 리스트를 받음.

			if (ex.damage == 0) {
				// 설정값이 0 이면 폭발용 이미지 그리기

				if (ex.ex_cnt < 7) {
					//폭발 효과 카운트가 7이하이면 첫번째 이미지를 출력
					buffg.drawImage(Explo_img[0], ex.x - Explo_img[0].getWidth(null) / 2,
							ex.y - Explo_img[0].getHeight(null) / 2, this);
				} else if (ex.ex_cnt < 14) {
					//폭발 효과 카운트가 7이상 14 미만이면 두 번쨰 폭발 이미지를 출력
					buffg.drawImage(Explo_img[1], ex.x - Explo_img[1].getWidth(null) / 2,
							ex.y - Explo_img[1].getHeight(null) / 2, this);
				} else if (ex.ex_cnt < 21) {
					//폭발 효과 카운트가 14이상 21미만이면 세 번째 폭발 이미지를 출력
					buffg.drawImage(Explo_img[2], ex.x - Explo_img[2].getWidth(null) / 2,
							ex.y - Explo_img[2].getHeight(null) / 2, this);
				} else if (ex.ex_cnt > 21) {
					//폭발 효과 카운트가 21이상이면 폭발 리스트에서 해당 폭발 객체를 제거하고 카운터를 초기화
					Explosion_List.remove(i);
					ex.ex_cnt = 0;
					//폭발은 따로 카운터를 계산하여
					//이미지를 순차적으로 그림.
				}
			} else { // 설정값이 1이면 단순 피격용 이미지 그리기
				if (ex.ex_cnt < 7) {
					buffg.drawImage(Explo_img[0], ex.x + 120, ex.y + 15, this); //120, 15 좌표 상에 출력
				} else if (ex.ex_cnt < 14) {
					buffg.drawImage(Explo_img[1], ex.x + 60, ex.y + 5, this); //60, 5 좌표 상에 출력
				} else if (ex.ex_cnt < 21) {
					buffg.drawImage(Explo_img[0], ex.x + 5, ex.y + 10, this); //5, 10 좌표 상에 출력
				} else if (ex.ex_cnt > 21) {
					Explosion_List.remove(i);
					ex.ex_cnt = 0;
					

				}
			}
		}
	}

	public void Draw_StatusText() { // 상태 체크용 텍스트를 그림

		buffg.setFont(new Font("Defualt", Font.BOLD, 20));
		//폰트 설정: 기본폰트, 굵게, 사이즈 20

		int xCoordinate = 20;
		int yCoordinate = 30;

		buffg.drawString("SCORE: " + game_Score, xCoordinate, yCoordinate + 20);
		//좌표 x : 1000, y : 70에 스코어를 표시

		buffg.drawString("HitPoint: " + player_Hitpoint, xCoordinate, yCoordinate + 40);
		//좌표 x : 1000, y : 90에 플레이어 체력을 표시

		buffg.drawString("Missile Count: " + Missile_List.size(), xCoordinate, yCoordinate + 60);
		//좌표 x : 1000, y : 110에 나타난 미사일 수를 표시

		buffg.drawString("Enemy Count: " + Enemy_List.size(), xCoordinate, yCoordinate + 80);
		//좌표 x : 1000, y : 130에 나타난 적의 수를 표시

		buffg.drawString("Space bar를 눌러 표창을 던지세요. " + Enemy_List.size(), xCoordinate, yCoordinate + 100);

	}

	public void KeyProcess() {
		if (KeyUp == true) {
			if (y > 20)
				y -= 5;
			//캐릭터가 보여지는 화면 위로 못 넘어가게 제한하는 메서드

			player_Status = 0;
			//이동키가 눌려지면 플레이어 상태를 0으로 돌림.
		}

		if (KeyDown == true) {
			if (y + Player_img[0].getHeight(null) < f_height)
				y += 5;
			//캐릭터가 보여지는 화면 아래로 못 넘어가게 제한

			player_Status = 0;
			//이동키가 눌려지면 플레이어 상태를 0으로 돌림.
		}

		if (KeyLeft == true) {
			if (x > 0)
				x -= 5;
			//캐릭터가 보여지는 화면 왼쪽으로 못 넘어가게 제한

			player_Status = 0;
			//이동키가 눌려지면 플레이어 상태를 0으로 돌림.
		}

		if (KeyRight == true) {
			if (x + Player_img[0].getWidth(null) < f_width)
				x += 5;
			//캐릭터가 보여지는 화면 오른쪽으로 못 넘어가게 제한

			player_Status = 0;
			//이동키가 눌려지면 플레이어 상태를 0으로 돌림.
		}
	}

	
	/**
	 * 키보드의 특정 키를 눌렀을 때 호출되는 메서드로, 각 키에 대한 누름 여부를 설정
	 * @param e KeyEvent 객체를 통해 전달된 키 이벤트
	 */
	
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			KeyUp = true;	// 위쪽 화살표 키가 눌렸을 때 KeyUp을 true로 설정
			break;
		case KeyEvent.VK_DOWN:	
			KeyDown = true;	// 아래쪽 화살표 키가 눌렸을 때 KeyDown을 true로 설정
			break;
		case KeyEvent.VK_LEFT:
			KeyLeft = true;	// 왼쪽 화살표 키가 눌렸을 때 KeyLeft을 true로 설정
			break;
		case KeyEvent.VK_RIGHT:
			KeyRight = true;	// 오른쪽 화살표 키가 눌렸을 때 KeyRight을 true로 설정
			break;

		case KeyEvent.VK_SPACE:
			KeySpace = true;	// 스페이스바가 눌렸을 때 KeySpace를 true로 설정
			break;
		}
	}

	/**
	 * keyReleased 메서드는 키보드의 특정 키를 놓았을 때 호출되는 메서드
	 * 전달된 KeyEvent 객체를 통해 놓인 키의 KeyCode를 확인하고, 해당하는 키에 대한 놓음 여부를 설정
	 * 
	 * @param e KeyEvent 객체를 통해 전달된 키 이벤트
	 */
	
	public void keyReleased(KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			KeyUp = false;	// 위쪽 화살표 키가 놓였을 때 KeyUp을 false로 설정
			break;
		case KeyEvent.VK_DOWN:
			KeyDown = false;	// 아래쪽 화살표 키가 놓였을 때 KeyDown을 false로 설정
			break;
		case KeyEvent.VK_LEFT:
			KeyLeft = false;	// 왼쪽 화살표 키가 놓였을 때 KeyLeft을 false로 설정
			break;
		case KeyEvent.VK_RIGHT:
			KeyRight = false;	// 오른쪽 화살표 키가 놓였을 때 KeyRight을 false로 설정
			break;

		case KeyEvent.VK_SPACE:
			KeySpace = false;	// 스페이스바가 놓였을 때 KeySpace를 false로 설정
			break;

		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}

class Missile {
	int x;
	int y;

	int speed; // 미사일 스피드 변수를 추가.

	Missile(int x, int y, int speed) {
		this.x = x;
		this.y = y;

		this.speed = speed;
		// 객체 생성시 속도 값을 추가로 받음.

	}

	public void move() {
		x += speed; // 미사일 스피드 속도 만큼 이동.
	}
}

class Enemy {
	int x;
	int y;

	int speed; // 적 이동 속도 변수를 추가.

	Enemy(int x, int y, int speed) {
		this.x = x;
		this.y = y;

		this.speed = speed;
		// 객체 생성시 속도 값을 추가로 받음.

	}

	public void move() {
		x -= speed;// 적이동속도만큼 이동.
	}
}

class Explosion {
// 여러개의 폭발 이미지를 그리기위해 클래스를 추가하여 객체관리 

	int x; // 이미지를 그릴 x 좌표
	int y; // 이미지를 그릴 y 좌표
	int ex_cnt; // 이미지를 순차적으로 그리기 위한 카운터
	int damage; // 이미지 종류를 구분하기 위한 변수값

	 /**
     * Explosion 클래스의 생성자
     * @param x 이미지를 그릴 x 좌표
     * @param y 이미지를 그릴 y 좌표
     * @param damage 이미지 종류를 구분하기 위한 변수값
     */
	
	Explosion(int x, int y, int damage) {
		this.x = x;
		this.y = y;
		this.damage = damage;
		ex_cnt = 0;
	}

	public void effect() {
		ex_cnt++; // 해당 메소드 호출 시 카운터 +1
	}
}
