import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
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
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class Cliente_view {
	
	// Variable turno 	
	public Integer turno; 
	
	private static DataInputStream dis;
	private DataOutputStream dos = null; 
	private static ObjectInputStream ois=null;
	private static String valorSimbolo = "";
	private static ObjectOutputStream oos=null;
	private static Socket socketDelCliente;
	
	private static ArrayList<BotonCliente> botones=new ArrayList<BotonCliente>();
	
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
	
	// BotonesCliente	
	private static BotonCliente bc_A1 = new BotonCliente();
	private static BotonCliente bc_A2 = new BotonCliente();
	private static BotonCliente bc_A3 = new BotonCliente();
	private static BotonCliente bc_B1 = new BotonCliente();
	private static BotonCliente bc_B2 = new BotonCliente();
	private static BotonCliente bc_B3 = new BotonCliente();
	private static BotonCliente bc_C1 = new BotonCliente();
	private static BotonCliente bc_C2 = new BotonCliente();
	private static BotonCliente bc_C3 = new BotonCliente();
	
	private JFrame frmTresEnRaya;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// SE INICIA LA INTERFAZ
				try {
					Cliente_view window = new Cliente_view();
					window.frmTresEnRaya.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// EL CLIENTE SE CONECTA CON EL SERVIDOR
				InetAddress direcc = null;
				try {
					direcc = InetAddress.getByName("localhost"); // la ip del servidor 	
					int puerto = 40000; 					
					try{
						socketDelCliente = new Socket(direcc, puerto);
						oos = new ObjectOutputStream(socketDelCliente.getOutputStream());		
						
						System.out.println("Cliente conectado");
						
						
						// RECIBE EL BOOLEANO
						dis = new DataInputStream(socketDelCliente.getInputStream());
						
						if(dis.readBoolean()) {
							valorSimbolo = "X";
						}else {
							valorSimbolo = "O";
							enviarInfo();
						}
						
					}catch(Exception e){						
						System.out.println("Cliente no conectado");
					}
				} catch (UnknownHostException uhe) {
					System.err.println("Servidor no encontrado : " + uhe);
					System.exit(-1);
				}
				
				// EL CLIENTE RECIBE INFORMACIÓN 
				// Recibe el booleano y asigna su símbolo
				
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
		
		JLabel lb_usuario = new JLabel("Usuario ");
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
				bc_A1.setBstate(true);
				bc_A1.setSimbolo(valorSimbolo);
				btn_A1.setText(bc_A1.getSimbolo());
				btn_A1.setEnabled(false);
				enviarInfo();
				try {
					recibirJugada();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_A1.setBounds(59, 20, 77, 19);
		btn_A1.setSize(70, 70);
		frmTresEnRaya.getContentPane().add(btn_A1);
		
		btn_A2 = new JButton("");
		btn_A2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_A2.setBstate(true);
				bc_A2.setSimbolo(valorSimbolo);
				btn_A2.setText(bc_A2.getSimbolo());
				btn_A2.setEnabled(false);
				enviarInfo();
				try {
					recibirJugada();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_A2.setBounds(137, 20, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_A2);
		
		btn_A3 = new JButton("");
		btn_A3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_A3.setBstate(true);
				bc_A3.setSimbolo(valorSimbolo);
				btn_A3.setText(bc_A3.getSimbolo());
				btn_A3.setEnabled(false);
				enviarInfo();
				try {
					recibirJugada();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_A3.setBounds(215, 20, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_A3);
		
		btn_B1 = new JButton("");
		btn_B1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_B1.setBstate(true);
				bc_B1.setSimbolo(valorSimbolo);
				btn_B1.setText(bc_B1.getSimbolo());
				btn_B1.setEnabled(false);
				enviarInfo();
				try {
					recibirJugada();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_B1.setBounds(59, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B1);
		
		btn_B2 = new JButton("");
		btn_B2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_B2.setBstate(true);
				bc_B2.setSimbolo(valorSimbolo);
				btn_B2.setText(bc_B2.getSimbolo());
				btn_B2.setEnabled(false);
				enviarInfo();
				try {
					recibirJugada();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_B2.setBounds(137, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B2);
		
		btn_B3 = new JButton("");
		btn_B3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_B3.setBstate(true);
				bc_B3.setSimbolo(valorSimbolo);
				btn_B3.setText(bc_B3.getSimbolo());
				btn_B3.setEnabled(false);
				enviarInfo();
				try {
					recibirJugada();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_B3.setBounds(215, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B3);
		
		btn_C1 = new JButton("");
		btn_C1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_C1.setBstate(true);
				bc_C1.setSimbolo(valorSimbolo);
				btn_C1.setText(bc_C1.getSimbolo());
				btn_C1.setEnabled(false);
				enviarInfo();
				try {
					recibirJugada();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_C1.setBounds(59, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C1);
		
		btn_C2 = new JButton("");
		btn_C2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_C2.setBstate(true);
				bc_C2.setSimbolo(valorSimbolo);
				btn_C2.setText(bc_C2.getSimbolo());
				btn_C2.setEnabled(false);
				enviarInfo();
				try {
					recibirJugada();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_C2.setBounds(137, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C2);
		
		btn_C3 = new JButton("");
		btn_C3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_C3.setBstate(true);
				bc_C3.setSimbolo(valorSimbolo);
				btn_C3.setText(bc_C3.getSimbolo());
				btn_C3.setEnabled(false);
				enviarInfo();
				try {
					recibirJugada();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_C3.setBounds(215, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C3);
	}

	
	public static void enviarInfo() {
		
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
		
		// METES EL ESTADO DE CADA BOTÓN DENTRO DE 'botones'
		botones.add(bc_A1);
		botones.add(bc_A2);
		botones.add(bc_A3);
		botones.add(bc_B1);
		botones.add(bc_B2);
		botones.add(bc_B3);
		botones.add(bc_C1);
		botones.add(bc_C2);
		botones.add(bc_C3);
		
		// SE ENVÍA 'botones'
		try {
			oos.writeObject(botones);
			System.out.println("Se ha enviado 'botones'");
		} catch (IOException e) {
			System.out.println("No se ha enviado 'botones'");
		}
	}
	@SuppressWarnings("unchecked")
	public static void recibirJugada() throws ClassNotFoundException, IOException {
		ois = new ObjectInputStream(socketDelCliente.getInputStream());	
		botones.clear();
		botones=(ArrayList<BotonCliente>)ois.readObject();
		
		btn_A1.setText(botones.get(0).getSimbolo()); if(botones.get(0).getBstate()) {btn_A1.setEnabled(false);}
		btn_A2.setText(botones.get(1).getSimbolo()); if(botones.get(1).getBstate()) {btn_A2.setEnabled(false);}
		btn_A3.setText(botones.get(2).getSimbolo()); if(botones.get(2).getBstate()) {btn_A3.setEnabled(false);}
		btn_B1.setText(botones.get(3).getSimbolo()); if(botones.get(3).getBstate()) {btn_B1.setEnabled(false);}
		btn_B2.setText(botones.get(4).getSimbolo()); if(botones.get(4).getBstate()) {btn_B2.setEnabled(false);}
		btn_B3.setText(botones.get(5).getSimbolo()); if(botones.get(5).getBstate()) {btn_B3.setEnabled(false);}
		btn_C1.setText(botones.get(6).getSimbolo()); if(botones.get(6).getBstate()) {btn_C1.setEnabled(false);}
		btn_C2.setText(botones.get(7).getSimbolo()); if(botones.get(7).getBstate()) {btn_C2.setEnabled(false);}
		btn_C3.setText(botones.get(8).getSimbolo()); if(botones.get(8).getBstate()) {btn_C3.setEnabled(false);}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
