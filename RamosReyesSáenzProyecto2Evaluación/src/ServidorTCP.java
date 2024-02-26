import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ServidorTCP {

	public static DataInputStream disJugador1, disJugador2;
	public static Socket socketDelCliente1, socketDelCliente2;

	public static void main(String[] args) {

		Integer turno = 0;
		Boolean jugando = true, jugador;

		// FASE INICIAL
		// Primero indicamos la dirección IP local
		try {
			System.out.println("LocalHost = " + InetAddress.getLocalHost().toString());
		} catch (UnknownHostException uhe) {
			System.err.println("No puedo saber la dirección IP local : " + uhe);
		}

		// Abrimos un "Socket de Servidor" TCP en el puerto 1234.

		try (ServerSocket socketDelServidor = new ServerSocket(40000, 2)) {
			// Se crea el socket servidor y se limita las conexiones a dos

			System.out.println("Esperando a que se conecten los usuarios...");

			System.out.println("Tic Tac Toe Server is Running...");
			var pool = Executors.newFixedThreadPool(200);
			Juego juego = new Juego();

			socketDelCliente1 = socketDelServidor.accept();
			disJugador1 = new DataInputStream(socketDelCliente1.getInputStream());
			String nombreJugador1 = disJugador1.readUTF();
			pool.execute(juego.new Jugador(nombreJugador1, socketDelCliente1, 'X'));
			juego.numeroJugadores++;

			socketDelCliente2 = socketDelServidor.accept();
			disJugador2 = new DataInputStream(socketDelCliente2.getInputStream());
			String nombreJugador2 = disJugador2.readUTF();
			pool.execute(juego.new Jugador(nombreJugador2, socketDelCliente2, 'O'));
			juego.numeroJugadores++;

		

			while (true) {
				if (juego.numeroJugadores != 2) {
					socketDelCliente1.close();
					socketDelCliente2.close();
					socketDelServidor.close();
					System.exit(0);
				}
			}

		} catch (IOException ioe) {
			System.err.println("Error al abrir el socket de servidor : " + ioe);
			System.exit(-1);
		}

	}
}

// CLASE JUEGO
class Juego {
	// Se inicializa el tablero
	static Jugador[] tablero = new Jugador[9];
	public Boolean jugando = true;
	public int turno = 0;
	static Jugador jugadorActivo;
	public static int numeroJugadores;
	public SecretKey claveSecreta;
	public Cipher cipher;

	public boolean seHaGanado(Jugador[] tablero) {
		return (tablero[0] != null && tablero[0] == tablero[1] && tablero[0] == tablero[2])
				|| (tablero[3] != null && tablero[3] == tablero[4] && tablero[3] == tablero[5])
				|| (tablero[6] != null && tablero[6] == tablero[7] && tablero[6] == tablero[8])
				|| (tablero[0] != null && tablero[0] == tablero[3] && tablero[0] == tablero[6])
				|| (tablero[1] != null && tablero[1] == tablero[4] && tablero[1] == tablero[7])
				|| (tablero[2] != null && tablero[2] == tablero[5] && tablero[2] == tablero[8])
				|| (tablero[0] != null && tablero[0] == tablero[4] && tablero[0] == tablero[8])
				|| (tablero[2] != null && tablero[2] == tablero[4] && tablero[2] == tablero[6]);
	}

	public void logDePartidas() {

	}

	public synchronized void checkJugada(int posicion, Jugador jugador) {
		if (jugador != jugadorActivo) {
			throw new IllegalStateException("No es tu turno. ");
		} else if (jugador.oponente == null) {

			throw new IllegalStateException("No tienes oponente");
		} else if (tablero[posicion] != null) {
			throw new IllegalStateException("Celda ocupada");
		}
	}

	
	// CLASE JUGADOR
	class Jugador implements Runnable {

		public DataInputStream dis;
		public DataOutputStream dos;

		String nombreJugador;
		Jugador oponente;
		Socket socketDelCliente;
		char simbolo;

		public Jugador(String nombreJugador, Socket socketDelCliente, char simbolo) {
			this.nombreJugador = nombreJugador;
			this.socketDelCliente = socketDelCliente;
			this.simbolo = simbolo;
		}

		public String getNombreJugador() {
			return nombreJugador;
		}

		public void setNombreJugador(String nombreJugador) {
			this.nombreJugador = nombreJugador;
		}

		public Socket getSocketDelCliente() {
			return socketDelCliente;
		}

		public void setSocketDelCliente(Socket socketDelCliente) {
			this.socketDelCliente = socketDelCliente;
		}

		public char getSimbolo() {
			return simbolo;
		}

		public void setSimbolo(char simbolo) {
			this.simbolo = simbolo;
		}

		public Jugador getOponente() {
			return oponente;
		}

		public void setOponente(Jugador oponente) {
			this.oponente = oponente;
		}

		@Override
		public void run() {
			try {
				dis = new DataInputStream(socketDelCliente.getInputStream());
				dos = new DataOutputStream(socketDelCliente.getOutputStream());

				// 1. Asignar el jugador activo y el oponente

				if (this.simbolo == 'X') {
					jugadorActivo = this;
				} else {
					if (this.getOponente() == null) {
						oponente = jugadorActivo;
						oponente.oponente = this;
					}else {
						oponente.dos.writeUTF("0");
					}
				}


				// 2. Envio de la marca al cliente, los dos jugadores mandan su marca a su
				// resoectivo cliente

				dos.writeUTF("1" + this.simbolo);
				
				// 1 + dato: estamos recibiendo el simbolo para asignarselo al cliente

				// El jugador va a estar a la escucha de la posicion clickada por el cliente
				// [enviarInfo()]

				while (jugando && turno<9) {
					try {


						int posicion = dis.readInt(); // AQUÍ DEBE ESPERAR A QUE EL CLIENTE PRESIONE UN BOTÓN

						
						// chequear lo jugada
						checkJugada(posicion, this);

						
						// Actualizar el tablero
						tablero[posicion] = this;

						// Se cambian los roles
						jugadorActivo = this.oponente;

						// Se envía la posicion al oponente
						oponente.dos.writeUTF("4" + posicion);
						dos.writeUTF("5");

						// Se manda el resultado al oponente
						if (seHaGanado(tablero)) {

							crearRegistroEncriptadoEnLog(this.nombreJugador, oponente.getNombreJugador());
							String resultado = getRegistrosEncriptados();
							
							// Enviar información al jugadorActivo
							dos.writeUTF("2Has ganado, " + this.getNombreJugador()+"\n"+resultado);

							// Enviar información al oponente
							oponente.dos.writeUTF("2Has perdido, " + oponente.getNombreJugador()+"\n"+resultado);

							jugando = false;
							numeroJugadores -= 2;

							dis.close();
							dos.close();
							oponente.dos.close();
							System.exit(0);

						}

					} catch (SocketException e) {
						try {
							
							oponente.dos.writeUTF("6");
							oponente.oponente = null;
							oponente = null;
							System.exit(0);
						} catch (Exception f) {
							System.exit(0);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						System.out.println("Excepcion");
						dos.writeUTF("3" + e.getMessage());
					} catch (InvalidKeyException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					turno++;
				}
				dos.writeUTF("7");
				oponente.dos.writeUTF("7");
				dis.close();
				dos.close();
				System.exit(0);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void crearRegistroEncriptadoEnLog(String nombreGanador, String nombrePerdedor)
			throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("logDePartidas.txt", true));
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        
        String fechaFormateada = fechaHoraActual.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

		String frase = "Partida con fecha " + fechaFormateada + " -> Ganador: " + nombreGanador + ", Perdedor: " + nombrePerdedor;

		String textoEncriptado = Base64.getEncoder().encodeToString(frase.getBytes());

		// Escribimos la frase en el fichero
		writer.write(textoEncriptado + "\n");
		writer.flush();
		writer.close();
	}

	public String getRegistrosEncriptados() throws NoSuchAlgorithmException, IOException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		BufferedReader reader = new BufferedReader(new FileReader("logDePartidas.txt"));
		String frase, resultado = "";

		while ((frase = reader.readLine()) != null) {

			// Descifrar el texto
			byte[] fraseDesencriptadaBytes = Base64.getDecoder().decode(frase.getBytes());
			String faseDesencriptada = new String(fraseDesencriptadaBytes);
			resultado+=faseDesencriptada+"\n";
		}

		return resultado;
	}
}
