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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class Cliente_view {
	
	// Variable turno 	
	public Integer turno; 
	
	private static DataInputStream dis;
	private static DataOutputStream dos = null; 
	
	private static String valorSimbolo = "";
	private static Socket socketDelCliente;
	
	
	// Botones interfaz
	private	static JButton btn_A1;
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
	private static JLabel lb_usuario;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// 1. SE INICIA LA INTERFAZ
				try {
					window = new Cliente_view();
					window.frmTresEnRaya.setVisible(false);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// 2. APARECE LA INTERFAZ QUE PIDE EL NOMBRE
				respuesta = JOptionPane.showInputDialog(null, "¿Cuál es tu nombre?");
				
				if(respuesta!=null && !respuesta.isEmpty()) {
					window.frmTresEnRaya.setVisible(true);
				}else {
					JOptionPane.showMessageDialog(null, "Cerrando la aplicación", "Cerrando", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);				
				}
							
				
				// 3. EL CLIENTE SE CONECTA CON EL SERVIDOR
				InetAddress direcc = null;
				try {
					direcc = InetAddress.getByName("localhost"); // la ip del servidor 	
					int puerto = 40000; 					
					try{
						socketDelCliente = new Socket(direcc, puerto);							
						System.out.println("Cliente conectado");
											
					}catch(Exception e){						
						System.out.println("Cliente no conectado");
					}
				} catch (UnknownHostException uhe) {
					System.err.println("Servidor no encontrado : " + uhe);
					System.exit(-1);
				}	
				
				// 4. ENVIAMOS EL NOMBRE AL SERVIDOR
				
				try {
					if(socketDelCliente!=null) {
						dos = new DataOutputStream(socketDelCliente.getOutputStream());
						dos.writeUTF(respuesta);
						lb_usuario.setText(respuesta);
					}
					JOptionPane.showMessageDialog(null, "Se ha enviado tu nombre al servidor", "Enviando", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}						
		});
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
		
		// Textos
		
		JLabel lb_turno = new JLabel("Es el turno de ");
		lb_turno.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lb_turno.setBounds(59, 259, 108, 19);
		frmTresEnRaya.getContentPane().add(lb_turno);
		
		lb_usuario = new JLabel("Usuario ");
		lb_usuario.setForeground(new Color(255, 0, 128));
		lb_usuario.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lb_usuario.setBounds(159, 260, 95, 17);
		frmTresEnRaya.getContentPane().add(lb_usuario);
		
		JLabel lbl_recordatorio = new JLabel("X: Usuario1    O: Usuario2");
		lbl_recordatorio.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lbl_recordatorio.setBounds(59, 286, 171, 11);
		frmTresEnRaya.getContentPane().add(lbl_recordatorio);
		
		// Botones (A-C: columnas, numeros: filas)
		
		btn_A1 = new JButton("");
		btn_A1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_A1.setEnabled(false);
				enviarInfo(0);
				
			}
		});
		btn_A1.setBounds(59, 20, 77, 19);
		btn_A1.setSize(70, 70);
		frmTresEnRaya.getContentPane().add(btn_A1);
		
		btn_A2 = new JButton("");
		btn_A2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_A2.setEnabled(false);
				enviarInfo(1);
				
			}
		});
		btn_A2.setBounds(137, 20, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_A2);
		
		btn_A3 = new JButton("");
		btn_A3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_A3.setEnabled(false);
				enviarInfo(2);
				
			}
		});
		btn_A3.setBounds(215, 20, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_A3);
		
		btn_B1 = new JButton("");
		btn_B1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_B1.setEnabled(false);
				enviarInfo(3);
				
			}
		});
		btn_B1.setBounds(59, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B1);
		
		btn_B2 = new JButton("");
		btn_B2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_B2.setEnabled(false);
				enviarInfo(4);
				
			}
		});
		btn_B2.setBounds(137, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B2);
		
		btn_B3 = new JButton("");
		btn_B3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_B3.setEnabled(false);
				enviarInfo(5);
				
			}
		});
		btn_B3.setBounds(215, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B3);
		
		btn_C1 = new JButton("");
		btn_C1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_C1.setEnabled(false);
				enviarInfo(6);
				
			}
		});
		btn_C1.setBounds(59, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C1);
		
		btn_C2 = new JButton("");
		btn_C2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_C2.setEnabled(false);
				enviarInfo(7);
				
			}
		});
		btn_C2.setBounds(137, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C2);
		
		btn_C3 = new JButton("");
		btn_C3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btn_C3.setEnabled(false);
				enviarInfo(8);
				
			}
		});
		btn_C3.setBounds(215, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C3);
	}

	
	public static void enviarInfo(int posicion) {
				
		// SE ENVÍA LA POSICIÓN DENTRO DEL ARRAY
		try {
			dos.writeInt(posicion);
		} catch (Exception e) {
			System.out.println("No se ha enviado 'botones'");
		}
		// LOS BOTONES DEL TABLERO SE BLOQUEAN
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
