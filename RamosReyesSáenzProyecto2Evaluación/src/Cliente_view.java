import java.awt.EventQueue;

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
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class Cliente_view {

	// Variable turno
	public Integer turno;
	private static DataInputStream dis;
	private static DataOutputStream dos = null;

	private static String valorSimbolo = "";
	private static String valorSimboloOponente = "";
	private static String mensajeFinal = "";
	private static Socket socketDelCliente;

	// Botones interfaz
	private static JButton btn_A1;
	private static JButton btn_A2;
	private static JButton btn_A3;
	private static JButton btn_B1;
	private static JButton btn_B2;
	private static JButton btn_B3;
	private static JButton btn_C1;
	private static JButton btn_C2;
	private static JButton btn_C3;

	private JFrame frmTresEnRaya;
	private static Cliente_view window;
	private static String respuesta;
	private static String ipServidor;
	private static JLabel lb_usuario;
	private static JLabel lb_simbolo;

	private static String nombreBotonMemoria;
	private static JButton[] arraybotones = new JButton[9];
	private static String log;

	public static void main(String[] args) {
		
				// 1. SE INICIA LA INTERFAZ
				try {
					window = new Cliente_view();
					window.frmTresEnRaya.setVisible(false);

				} catch (Exception e) {
					e.printStackTrace();
				}


				ipServidor = JOptionPane.showInputDialog(null, "Introduce la IP del servidor.");				
				// 3. EL CLIENTE SE CONECTA CON EL SERVIDOR
				InetAddress direcc = null;
				if(!ipServidor.isEmpty()) {

					try {
						direcc = InetAddress.getByName(ipServidor); // la ip del servidor
						int puerto = 40000;
						try {
							socketDelCliente = new Socket(direcc, puerto);
							System.out.println("Cliente conectado");

						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (UnknownHostException uhe) {
						JOptionPane.showMessageDialog(null, "Ip introducida no válida", "Error",
								JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}else {
					JOptionPane.showMessageDialog(null, "No has introducido ninguna ip", "Error",
							JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}

				// 2. APARECE LA INTERFAZ QUE PIDE EL NOMBRE
				respuesta = JOptionPane.showInputDialog(null, "¿Cuál es tu nombre?");
				
				if (respuesta != null && !respuesta.isEmpty()) {
					window.frmTresEnRaya.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(null, "Cerrando la aplicación", "Cerrando",
							JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}

				// 4. ENVIAMOS EL NOMBRE AL SERVIDOR

				try {
					if (socketDelCliente != null) {
						dos = new DataOutputStream(socketDelCliente.getOutputStream());
						dos.writeUTF(respuesta);
						lb_usuario.setText(respuesta);
						
					}
					
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "No se ha podido conectar con el servidor", "Error",
							JOptionPane.ERROR_MESSAGE);
				}

				try {
					dis = new DataInputStream(socketDelCliente.getInputStream());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (true) {
					// 5. ESTÁ CONSTANTEMENTE ESCUCHANDO A LA INFORMACIÓN DE LOS JUGADORES
					try {

						String input = dis.readUTF();

						if (input.startsWith("1")) {
							// estamos recibiendo el simbolo para asignarselo al cliente
							valorSimbolo = (input.charAt(1) + "");
							if (valorSimbolo.equals("X")) {
								valorSimboloOponente = "O";
							} else {
								valorSimboloOponente = "X";
								bloquearBotones();
							}
							lb_simbolo.setText(valorSimbolo);
						} else if (input.startsWith("2")) {
							mensajeFinal = input.substring(1);
							String mensajeResultado = mensajeFinal.split("\n")[0];
							
							String logFinal= "";
							String[] array  = mensajeFinal.split("\n");
							for (int i = 0; i < array.length; i++) {
								if(i!=0) {
									logFinal+=array[i]+"\n";
								}
							}									
							JOptionPane.showMessageDialog(null, mensajeResultado, "Gracias por participar", JOptionPane.PLAIN_MESSAGE);
							JOptionPane.showMessageDialog(null, logFinal, "Log de partidas", JOptionPane.PLAIN_MESSAGE);
						    
							System.exit(0);
						} else if (input.startsWith("3")) {
							mensajeFinal = input.substring(1);
							JOptionPane.showMessageDialog(null, mensajeFinal,"Fin del juego", JOptionPane.INFORMATION_MESSAGE);
							// DESSELECCIONAMOS EL BOTÓN ERRONEO
							if(nombreBotonMemoria!=null) {
								deseleccionBoton();
							}
						} else if (input.startsWith("4")) {
							System.out.println(input);
							mensajeFinal = input.substring(1);
							actualizarBoton(mensajeFinal);
							desbloquearBotones();
						}else if(input.startsWith("5")) {
							jugadaPermitida();
						} else if(input.equals("6")) {
							JOptionPane.showMessageDialog(null, "Tu oponente se ha rendido", "Cobarde", JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						} else if(input.equals("7")) {
							JOptionPane.showMessageDialog(null, "Se ha creado un empate!!", "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						}else if(input.equals("0")) {
							desbloquearBotones();
						}
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(0); // para que no se quede en bucle
					}
				}
			}

			private static void jugadaPermitida() {
				for (int i = 0; i < arraybotones.length; i++) {
					if (arraybotones[i].getName().equals(nombreBotonMemoria)) {
						arraybotones[i].setEnabled(false);
						arraybotones[i].setText(valorSimbolo);
					}
				}
				nombreBotonMemoria=null;
			}

			private static void deseleccionBoton() {
				for (int i = 0; i < arraybotones.length; i++) {
					if (arraybotones[i].getName().equals(nombreBotonMemoria)) {
						arraybotones[i].setEnabled(true);
						arraybotones[i].setText("");
					}
				}
				nombreBotonMemoria=null;
			}

			private static void actualizarBoton(String mensajeFinal) {
				for (int i = 0; i < arraybotones.length; i++) {
					if(i==Integer.parseInt(mensajeFinal)) {
						arraybotones[i].setText(valorSimboloOponente);
						arraybotones[i].setEnabled(false);
					}
				}		
			}
		
	

	/**
	 * Create the application.
	 */
	public Cliente_view() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
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

		// Botones (A-C: columnas, numeros: filas)

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

	public static void enviarInfo(int posicion) {

		// SE ENVÍA LA POSICIÓN DENTRO DEL ARRAY
		try {
			dos.writeInt(posicion);
		} catch (Exception e) {
			System.out.println("No se ha enviado 'botones'");
			return;
		}
		
		 bloquearBotones();
		 
	}

	private static void bloquearBotones() {
		btn_A1.setEnabled(false); btn_A2.setEnabled(false); btn_A3.setEnabled(false);
		 btn_B1.setEnabled(false); btn_B2.setEnabled(false); btn_B3.setEnabled(false);
		 btn_C1.setEnabled(false); btn_C2.setEnabled(false); btn_C3.setEnabled(false);
	}
	
	private static void desbloquearBotones() {		
		for (int i = 0; i < arraybotones.length; i++) {
			if(arraybotones[i].getText()=="") {
				arraybotones[i].setEnabled(true);
			}			
		}
	}
}
