import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class Cliente_view {

	// Variables para la gestión de la partida y la conexión
	private static Cliente_view window;
	public Integer turno;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static Socket socketDelCliente;
	private static String valorSimbolo = "";
	private static String valorSimboloOponente = "";
	private static String mensajeFinal = "";
	private static String respuesta;
	private static String ipServidor;
	private static String nombreBotonMemoria;
	//private static String log;

	// Botones interfaz
	private static JButton[] arraybotones = new JButton[9];
	private static JButton btn_A1;
	private static JButton btn_A2;
	private static JButton btn_A3;
	private static JButton btn_B1;
	private static JButton btn_B2;
	private static JButton btn_B3;
	private static JButton btn_C1;
	private static JButton btn_C2;
	private static JButton btn_C3;
	private static JLabel lb_usuario;
	private static JLabel lb_simbolo;
	private JFrame frmTresEnRaya;

	public static void main(String[] args) throws IOException {

		// Se inicia la interfaz
		try {
			window = new Cliente_view();
			window.frmTresEnRaya.setVisible(false);

		} catch (Exception e) {
			e.printStackTrace();
		}
		ipServidor = JOptionPane.showInputDialog(null, "Introduce la IP del servidor.", "¿A qué servidor quieres conectarte?", JOptionPane.QUESTION_MESSAGE);

		// Se intenta que el cliente se conecte con el servidor
		InetAddress direcc = null;
		if (!ipServidor.isEmpty()) {
			try {
				direcc = InetAddress.getByName(ipServidor);
				int puerto = 40000;
				try {
					socketDelCliente = new Socket(direcc, puerto);
					System.out.println("Cliente conectado\n...");
				} catch (SocketException e) { 
					JOptionPane.showMessageDialog(null, "La IP introducida no es válida", "Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("Cliente desconectado");
					System.exit(0);
				}
			} catch (UnknownHostException uhe) { // El servidor de la IP existe o está encendido
				JOptionPane.showMessageDialog(null, "La IP introducida no es válida", "Error", JOptionPane.ERROR_MESSAGE);
				System.out.println("Cliente desconectado");
				System.exit(0);
			}
		} else {
			JOptionPane.showMessageDialog(null, "No has introducido ninguna IP", "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("Cliente desconectado");
			System.exit(0);
		}

		// Se pide el nombre al jugador
		respuesta = JOptionPane.showInputDialog(null, "¿Cuál es tu nombre?", "IP de servidor encontrada", JOptionPane.QUESTION_MESSAGE);
		if (respuesta != null && !respuesta.isEmpty()) {
			window.frmTresEnRaya.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(null, "Cerrando la aplicación", "Hasta la próxima", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("Cliente desconectado");
			System.exit(0);
		}

		// Se envía el nombre al servidor y se marca el nombre de los jugadores en sus
		// respectivas interfaces
		try {
			if (socketDelCliente != null) {
				dos = new DataOutputStream(socketDelCliente.getOutputStream());
				dis = new DataInputStream(socketDelCliente.getInputStream());
				dos.writeUTF(respuesta);
				lb_usuario.setText(respuesta);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "No se ha podido conectar con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
		}

		while (true) {
			// En este punto, el cliente se encuentra constantemente esperando un mensaje del servidor
			// Dependiendo de cuál sea el valor del primer carácter de "input", tendrá lugar un efecto u otro
			// El valor de "input" en muchos casos va a ser una concatenación de un número (un evento) y un mensajeFinal (un valor o mensaje)
			
			try {
				String input = dis.readUTF();

				// "1" - Se envía el número 1 junto al símbolo para gestionar la asignación de X e O ("1" + this.simbolo)
				if (input.startsWith("1")) {
					valorSimbolo = (input.charAt(1) + "");
					if (valorSimbolo.equals("X")) {
						valorSimboloOponente = "O";
					} else {
						valorSimboloOponente = "X";
						bloquearBotones();
					}
					lb_simbolo.setText(valorSimbolo);

				// "2" - Se gestiona el evento de fin de partida, mostrando los avisos y el log de partidas
				} else if (input.startsWith("2")) {
					mensajeFinal = input.substring(1);
					String mensajeResultado = mensajeFinal.split("\n")[0];
					String logFinal = "";
					String[] array = mensajeFinal.split("\n");
					for (int i = 0; i < array.length; i++) {
						if (i != 0) {
							logFinal += array[i] + "\n";
						}
					}
					JOptionPane.showMessageDialog(null, mensajeResultado, "Gracias por participar", JOptionPane.PLAIN_MESSAGE);
					JOptionPane.showMessageDialog(null, logFinal, "Log de partidas", JOptionPane.PLAIN_MESSAGE);
					System.out.println("Cliente desconectado");
					System.exit(0);

				/*"3" - Se gestionan aquellos eventos en los que es necesario deseleccionar un botón o los casos en los 
				        que se ha presionado un botón sin oponente y hay que desbloquear todos los botones
				        
					 	* "nombreBotonMemoria" recoge temporalmente el nombre del botón que se ha presionado
					 	   después, en servidor se comprueba si el "legal" que ese botón puede ser presionado */
				} else if (input.startsWith("3")) {
					mensajeFinal = input.substring(1);
					JOptionPane.showMessageDialog(null, mensajeFinal, "Un poco de paciencia...", JOptionPane.INFORMATION_MESSAGE);
					if (nombreBotonMemoria != null) {
						deseleccionBoton();
					}
					if (mensajeFinal.equals("No tienes oponente")) {
						desbloquearBotones();
					}
				
				/* "4" - Se recoge la posición del botón que ha presionado el oponente, se actualiza el botón del jugador con 
				         el movimiento del oponente y se  desbloquea su tablero para que el jugador pueda proceder con su turno */
				} else if (input.startsWith("4")) {
					mensajeFinal = input.substring(1);
					actualizarBoton(mensajeFinal);
					desbloquearBotones();
					
				// "5" - Se ejecuta el método "jugadaPermitida()" 
				} else if (input.startsWith("5")) {
					jugadaPermitida();
				
				// "6" - Gestiona el evento en el que el oponente se rinde y se termina el juego
				} else if (input.equals("6")) {
					JOptionPane.showMessageDialog(null, "Tu oponente se ha rendido", "Fin de partida", JOptionPane.INFORMATION_MESSAGE);
					System.out.println("Cliente desconectado");
					System.exit(0);
					
				// "7" - Gestiona el evento en el que tiene lugar un empate y termina el juego
				} else if (input.equals("7")) {
					JOptionPane.showMessageDialog(null, "Se ha creado un empate", "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
					System.out.println("Cliente desconectado");
					System.exit(0);
					
				// "8" - Simplemente de desbloquean los botones del tablero
				} else if (input.equals("0")) {
					desbloquearBotones();
				}
				
			// Si no se puede registrar el movimiento correctamente, se informa de ello y se sale del programa
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "No se ha podido registrar el movimiento\nSaliendo del programa", "Error", JOptionPane.ERROR_MESSAGE);
				System.out.println("Cliente desconectado");
				System.exit(0); // Para que no se quede en bucle
			}
		}
	}

	// Recorre el array de botones y actualiza el tablero con el valor del botón con nombre = nombreBotonMemoria
	private static void jugadaPermitida() {
		for (int i = 0; i < arraybotones.length; i++) {
			if (arraybotones[i].getName().equals(nombreBotonMemoria)) {
				arraybotones[i].setEnabled(false);
				arraybotones[i].setText(valorSimbolo);
			}
		}
		nombreBotonMemoria = null;
	}

	// Recorre el array de botones y actualiza el tablero deseleccionando el botón con nombre = nombreBotonMemoria
	private static void deseleccionBoton() {
		for (int i = 0; i < arraybotones.length; i++) {
			if (arraybotones[i].getName().equals(nombreBotonMemoria)) {
				arraybotones[i].setEnabled(true);
				arraybotones[i].setText("");
			}
		}
		nombreBotonMemoria = null;
	}

	// Recorre el array de botones y actualiza con el botón con el movimiento del oponente
	private static void actualizarBoton(String mensajeFinal) {
		for (int i = 0; i < arraybotones.length; i++) {
			if (i == Integer.parseInt(mensajeFinal)) {
				arraybotones[i].setText(valorSimboloOponente);
				arraybotones[i].setEnabled(false);
			}
		}
	}

	// Se inicializa "Cliente_view()"
	public Cliente_view() {
		initialize();
	}

	// Se inicializan los contenidos del frame
	private void initialize() {

		frmTresEnRaya = new JFrame();
		frmTresEnRaya.setTitle("TRES EN RAYA");
		frmTresEnRaya.setBounds(100, 100, 350, 350);
		frmTresEnRaya.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTresEnRaya.getContentPane().setLayout(null);
		frmTresEnRaya.setResizable(false);

		// Textos
		JLabel lb_turno = new JLabel("Jugador:");
		lb_turno.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lb_turno.setBounds(59, 259, 70, 19);
		frmTresEnRaya.getContentPane().add(lb_turno);
		lb_usuario = new JLabel("Jugador");
		lb_usuario.setForeground(new Color(255, 0, 128));
		lb_usuario.setFont(new Font("Tahoma", Font.BOLD, 15));
		lb_usuario.setBounds(124, 260, 186, 17);
		frmTresEnRaya.getContentPane().add(lb_usuario);
		JLabel lbl_recordatorio = new JLabel("Símbolo:");
		lbl_recordatorio.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_recordatorio.setBounds(59, 286, 58, 11);
		frmTresEnRaya.getContentPane().add(lbl_recordatorio);

		/* Botones (A-C: columnas, 1-3: filas) y sus "on click listeners"	
		   Cuando se presiona un botón del tablero se mete el nombre del botón en la variable
		   "nombreBotonMemoria" y se envía la información de la posición presionada [0-9]		   
		   También se indica el nombre del botón y su posición dentro del arrayBotones*/	
		 
		btn_A1 = new JButton("");
		btn_A1.setForeground(Color.BLACK);
		btn_A1.setFont(new Font("Tahoma", Font.BOLD, 30));
		btn_A1.setName("btn_A1");
		btn_A1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nombreBotonMemoria = btn_A1.getName();
				enviarInfo(0);

			}
		});
		btn_A1.setBounds(59, 20, 77, 19);
		btn_A1.setSize(70, 70);
		frmTresEnRaya.getContentPane().add(btn_A1);
		arraybotones[0] = btn_A1;

		btn_A2 = new JButton("");
		btn_A2.setFont(new Font("Tahoma", Font.BOLD, 30));
		btn_A2.setName("btn_A2");
		btn_A2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nombreBotonMemoria = btn_A2.getName();
				enviarInfo(1);

			}
		});
		btn_A2.setBounds(137, 20, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_A2);
		arraybotones[1] = btn_A2;

		btn_A3 = new JButton("");
		btn_A3.setFont(new Font("Tahoma", Font.BOLD, 30));
		btn_A3.setName("btn_A3");
		btn_A3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nombreBotonMemoria = btn_A3.getName();
				enviarInfo(2);

			}
		});
		btn_A3.setBounds(215, 20, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_A3);
		arraybotones[2] = btn_A3;

		btn_B1 = new JButton("");
		btn_B1.setFont(new Font("Tahoma", Font.BOLD, 30));
		btn_B1.setName("btn_B1");
		btn_B1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nombreBotonMemoria = btn_B1.getName();
				enviarInfo(3);

			}
		});
		btn_B1.setBounds(59, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B1);
		arraybotones[3] = btn_B1;

		btn_B2 = new JButton("");
		btn_B2.setFont(new Font("Tahoma", Font.BOLD, 30));
		btn_B2.setName("btn_B2");
		btn_B2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				nombreBotonMemoria = btn_B2.getName();
				enviarInfo(4);

			}
		});
		btn_B2.setBounds(137, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B2);
		arraybotones[4] = btn_B2;

		btn_B3 = new JButton("");
		btn_B3.setFont(new Font("Tahoma", Font.BOLD, 30));
		btn_B3.setName("btn_B3");
		btn_B3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nombreBotonMemoria = btn_B3.getName();
				enviarInfo(5);

			}
		});
		btn_B3.setBounds(215, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B3);
		arraybotones[5] = btn_B3;

		btn_C1 = new JButton("");
		btn_C1.setFont(new Font("Tahoma", Font.BOLD, 30));
		btn_C1.setName("btn_C1");
		btn_C1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				nombreBotonMemoria = btn_C1.getName();
				enviarInfo(6);

			}
		});
		btn_C1.setBounds(59, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C1);
		arraybotones[6] = btn_C1;

		btn_C2 = new JButton("");
		btn_C2.setFont(new Font("Tahoma", Font.BOLD, 30));
		btn_C2.setName("btn_C2");
		btn_C2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				nombreBotonMemoria = btn_C2.getName();
				enviarInfo(7);

			}
		});
		btn_C2.setBounds(137, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C2);
		arraybotones[7] = btn_C2;

		btn_C3 = new JButton("");
		btn_C3.setFont(new Font("Tahoma", Font.BOLD, 30));
		btn_C3.setName("btn_C3");
		btn_C3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				nombreBotonMemoria = btn_C3.getName();
				enviarInfo(8);

			}
		});
		btn_C3.setBounds(215, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C3);
		arraybotones[8] = btn_C3;

		lb_simbolo = new JLabel("Simbolo");
		lb_simbolo.setForeground(new Color(255, 0, 128));
		lb_simbolo.setFont(new Font("Tahoma", Font.BOLD, 15));
		lb_simbolo.setBounds(124, 283, 95, 17);
		frmTresEnRaya.getContentPane().add(lb_simbolo);
	}

	/* Este método envía la posición del botón al servidor y bloquea los botones del tablero del usuario.
	   Se incluye un "return" para salir del método y no bloquear los botones en caso de que no se pudieran
	   enviar los datos correctamente */	
	public static void enviarInfo(int posicion) {
		try {
			dos.writeInt(posicion);
		} catch (Exception e) {
			System.out.println("No se ha enviado 'botones'");
			return;
		}
		bloquearBotones();
	}

	// Este método bloquea todos los botones
	private static void bloquearBotones() {
		btn_A1.setEnabled(false);
		btn_A2.setEnabled(false);
		btn_A3.setEnabled(false);
		btn_B1.setEnabled(false);
		btn_B2.setEnabled(false);
		btn_B3.setEnabled(false);
		btn_C1.setEnabled(false);
		btn_C2.setEnabled(false);
		btn_C3.setEnabled(false);
	}

	// Este método desbloquea todos los botones que no contienen texto, y por tanto, no han sido pulsados
	private static void desbloquearBotones() {
		for (int i = 0; i < arraybotones.length; i++) {
			if (arraybotones[i].getText() == "") {
				arraybotones[i].setEnabled(true);
			}
		}
	}
}
