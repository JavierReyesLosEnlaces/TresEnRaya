import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class Cliente1_view {
	
	// Variable turno 	
	public Integer turno; 
	
	// BotonesCliente	
	BotonCliente bc_A1 = new BotonCliente();
	BotonCliente bc_A2 = new BotonCliente();
	BotonCliente bc_A3 = new BotonCliente();
	BotonCliente bc_B1 = new BotonCliente();
	BotonCliente bc_B2 = new BotonCliente();
	BotonCliente bc_B3 = new BotonCliente();
	BotonCliente bc_C1 = new BotonCliente();
	BotonCliente bc_C2 = new BotonCliente();
	BotonCliente bc_C3 = new BotonCliente();
	
	private JFrame frmTresEnRaya;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				// Se inicia la interfaz
				try {
					Cliente1_view window = new Cliente1_view();
					window.frmTresEnRaya.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// Se conecta 
				InetAddress direcc = null;
				try {
					direcc = InetAddress.getByName("localhost");					
				} catch (UnknownHostException uhe) {
					System.err.println("Host no encontrado : " + uhe);
					System.exit(-1);
				}
				
				// Recibe información
				
				// Se reciben los nueve objetos
				// Se recibe la variable "turno" 
			}						
		});
	}

	/**
	 * Create the application.
	 */
	public Cliente1_view() {
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
		
		JButton btn_A1 = new JButton("");
		btn_A1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_A1.setBstate(true);
				bc_A1.setSimbolo("X");
				btn_A1.setText(bc_A1.getSimbolo());
				btn_A1.setEnabled(false);
				enviar();
			}
		});
		btn_A1.setBounds(59, 20, 77, 19);
		btn_A1.setSize(70, 70);
		frmTresEnRaya.getContentPane().add(btn_A1);
		
		JButton btn_A2 = new JButton("");
		btn_A2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_A2.setBstate(true);
				bc_A2.setSimbolo("X");
				btn_A2.setText(bc_A2.getSimbolo());
				btn_A2.setEnabled(false);
				enviar();
			}
		});
		btn_A2.setBounds(137, 20, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_A2);
		
		JButton btn_A3 = new JButton("");
		btn_A3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_A3.setBstate(true);
				bc_A3.setSimbolo("X");
				btn_A3.setText(bc_A3.getSimbolo());
				btn_A3.setEnabled(false);
				enviar();
			}
		});
		btn_A3.setBounds(215, 20, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_A3);
		
		JButton btn_B1 = new JButton("");
		btn_B1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_B1.setBstate(true);
				bc_B1.setSimbolo("X");
				btn_B1.setText(bc_B1.getSimbolo());
				btn_B1.setEnabled(false);
				enviar();
			}
		});
		btn_B1.setBounds(59, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B1);
		
		JButton btn_B2 = new JButton("");
		btn_B2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_B2.setBstate(true);
				bc_B2.setSimbolo("X");
				btn_B2.setText(bc_B2.getSimbolo());
				btn_B2.setEnabled(false);
				enviar();
			}
		});
		btn_B2.setBounds(137, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B2);
		
		JButton btn_B3 = new JButton("");
		btn_B3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_B3.setBstate(true);
				bc_B3.setSimbolo("X");
				btn_B3.setText(bc_B3.getSimbolo());
				btn_B3.setEnabled(false);
				enviar();
			}
		});
		btn_B3.setBounds(215, 98, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_B3);
		
		JButton btn_C1 = new JButton("");
		btn_C1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_C1.setBstate(true);
				bc_C1.setSimbolo("X");
				btn_C1.setText(bc_C1.getSimbolo());
				btn_C1.setEnabled(false);
				enviar();
			}
		});
		btn_C1.setBounds(59, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C1);
		
		JButton btn_C2 = new JButton("");
		btn_C2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_C2.setBstate(true);
				bc_C2.setSimbolo("X");
				btn_C2.setText(bc_C2.getSimbolo());
				btn_C2.setEnabled(false);
				enviar();
			}
		});
		btn_C2.setBounds(137, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C2);
		
		JButton btn_C3 = new JButton("");
		btn_C3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bc_C3.setBstate(true);
				bc_C3.setSimbolo("X");
				btn_C3.setText(bc_C3.getSimbolo());
				btn_C3.setEnabled(false);
				enviar();
			}
		});
		btn_C3.setBounds(215, 176, 70, 70);
		frmTresEnRaya.getContentPane().add(btn_C3);
	}

	
	public void enviar() {
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
