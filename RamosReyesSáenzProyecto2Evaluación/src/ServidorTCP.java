import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ServidorTCP {

	// Variables para la gestión de las conexiones con los clientes
	public static DataInputStream disJugador1, disJugador2;
	public static Socket socketDelCliente1, socketDelCliente2;

	public static void main(String[] args) {

		// Se consigue la dirección IP del servidor y se muestra por consola
		try {
			System.out.println("LocalHost = " + InetAddress.getLocalHost().toString());
		} catch (UnknownHostException uhe) {
			System.err.println("No puedo saber la dirección IP local : " + uhe);
		}

		// Se intenta abrir un socket de servidor TCP en el puerto 40000, limitando el número de conexiones a dos		
		try (ServerSocket socketDelServidor = new ServerSocket(40000, 2)) {
			
			System.out.println("Esperando a que se conecten los usuarios...");
			System.out.println("Estado del servidor: ACTIVO\n...");
			
			// Se crea la variable "pool" que permite crear hasta 20 hilos de forma concurrente
			var pool = Executors.newFixedThreadPool(20);
			
			// Se crea un objeto de la clase "Juego"
			Juego juego = new Juego();

			// Se crean los sockets y los flujos de lectura de los clientes
			socketDelCliente1 = socketDelServidor.accept();
			disJugador1 = new DataInputStream(socketDelCliente1.getInputStream());
			
			// El servidor queda a la espera de recibir el nombre de usuario de sus jugadores
			String nombreJugador1 = disJugador1.readUTF();
			
			// Si todo ha ido bien, "pool" abrirá un hilo que permitirá unir un jugador al juego 
			// Se aumenta el número de jugadores a uno
			pool.execute(juego.new Jugador(nombreJugador1, socketDelCliente1, 'X'));
			juego.numeroJugadores++;

			// Ocurre lo mismo con el otro cliente
			socketDelCliente2 = socketDelServidor.accept();
			disJugador2 = new DataInputStream(socketDelCliente2.getInputStream());
			String nombreJugador2 = disJugador2.readUTF();
			pool.execute(juego.new Jugador(nombreJugador2, socketDelCliente2, 'O'));
			juego.numeroJugadores++;

			/* A partir de ahora, si el número de jugadores es menor de dos, significa que uno de ellos se ha
			   rendido o ha perdido la conexión, por lo que se cierran los sockets y la ejecución del servidor*/
			while (true) {
				if (juego.numeroJugadores != 2) {
					socketDelCliente1.close();
					socketDelCliente2.close();
					socketDelServidor.close();
					System.out.println("Estado del servidor: INACTIVO");
					System.exit(0);
				}
			}
		} catch (IOException ioe) {
			System.err.println("Error al abrir el socket de servidor : " + ioe);
			System.out.println("Estado del servidor: INACTIVO");
			System.exit(-1);
		}
	}
}

// Clase "Juego"
class Juego {
	
	// Variables para la gestión de la clase "Juego"
	static Jugador[] tablero = new Jugador[9]; // El tablero se concibe como un array de objetos "Jugador"
	public Boolean jugando = true;
	public int turno = 0;
	static Jugador jugadorActivo;
	public int numeroJugadores;
	public SecretKey claveSecreta;
	public Cipher cipher;

	// Este método valora si se ha dado un evento de victoria en el tablero
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

	// Este método evita que tengan lugar múltiples eventos en el juego
	public synchronized void checkJugada(int posicion, Jugador jugador) {
		if (jugador != jugadorActivo) {
			throw new IllegalStateException("No es tu turno. ");
		} else if (jugador.oponente == null) {
			throw new IllegalStateException("No tienes oponente");
		} else if (tablero[posicion] != null) {
			throw new IllegalStateException("Celda ocupada");
		}
	}
	
	// Este método crea un registro encriptado de los resultados de la partida y lo escribe en un documento "logDePartidas.txt"
	public void crearRegistroEncriptadoEnLog(String nombreGanador, String nombrePerdedor) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("logDePartidas.txt", true));
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        
        String fechaFormateada = fechaHoraActual.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
		String frase = "Partida con fecha " + fechaFormateada + " -> Ganador: " + nombreGanador + ", Perdedor: " + nombrePerdedor;
		String textoEncriptado = Base64.getEncoder().encodeToString(frase.getBytes());

		writer.write(textoEncriptado + "\n");
		writer.flush();
		writer.close();
	}

	// Este método desencripta el contenido de "logDePartidas.txt", lo almacena en una variable "resultado" y lo devuelve
	public String getRegistrosDesencriptados() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		BufferedReader reader = new BufferedReader(new FileReader("logDePartidas.txt"));
		String frase, resultado = "";

		while ((frase = reader.readLine()) != null) {	
			byte[] fraseDesencriptadaBytes = Base64.getDecoder().decode(frase.getBytes());
			String faseDesencriptada = new String(fraseDesencriptadaBytes);
			resultado+=faseDesencriptada+"\n";
		}

		reader.close();
		return resultado;
	}

	
	/* Clase "Jugador" (dentro de la clase "Juego") que implementa la interfaz "Runnable".
	   Es necesario que la clase implemente "Runnable" para poder trabajar con ella dentro de los hilos de ejecución */	 	
	class Jugador implements Runnable {

		// Variables para la gestión de la clase "Jugador"
		public DataInputStream dis;
		public DataOutputStream dos;
		String nombreJugador;
		Jugador oponente;
		Socket socketDelCliente;
		char simbolo;

		// Constructor 
		public Jugador(String nombreJugador, Socket socketDelCliente, char simbolo) {
			this.nombreJugador = nombreJugador;
			this.socketDelCliente = socketDelCliente;
			this.simbolo = simbolo;
		}

		// Getters y Setters
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

		// Método "run()" propio de la interfaz "Runnable", se definen las tareas a realizar por los objetos de la clase Jugador
		@Override
		public void run() {
			try {
				dis = new DataInputStream(socketDelCliente.getInputStream());
				dos = new DataOutputStream(socketDelCliente.getOutputStream());

				// Asignar el jugador activo y el oponente dependiendo del símbolo asignado
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

				/* Envío de la marca al cliente, los dos jugadores mandan su marca a su respectivo cliente.
				   1 + DATO: estamos recibiendo el simbolo para asignarselo al cliente*/
				dos.writeUTF("1" + this.simbolo);			

				/* A partir de este momento el jugador va a estar a la escucha de la posicion pulsada por el cliente -> enviarInfo()
				   Mientras la variable "jugando" == true y el número de turnos sea menor que 9... (nº máximo de turnos posibles, nº de casillas) */								
				while (jugando && turno<9) {
					try {

						// Aquí se espera a que el cliente presione un botón y se comprueba la jugada
						int posicion = dis.readInt(); 
						checkJugada(posicion, this);
						
						// Se actualiza el tablero (el tablero es un array de objetos "Jugador")
						tablero[posicion] = this;

						// Se cambian los roles y se envía la posición al oponente
						jugadorActivo = this.oponente;
						oponente.dos.writeUTF("4" + posicion);
						dos.writeUTF("5");

						// Se comprueba si se ha ganado en el tablero. Si se ha ganado:
						if (seHaGanado(tablero)) {

							// Se crea un nuevo registro en "logDePartidas.txt" y se consiguen los resultados 
							crearRegistroEncriptadoEnLog(this.nombreJugador, oponente.getNombreJugador());
							String resultado = getRegistrosDesencriptados();
							
							// Se envían los resultados de la partida al jugador activo y al oponente
							dos.writeUTF("2Has ganado, " + this.getNombreJugador()+"\n"+resultado);
							oponente.dos.writeUTF("2Has perdido, " + oponente.getNombreJugador()+"\n"+resultado);

							/* Se pone la variable "jugando" a false, se reinicia el número de jugadores, se cierran los flujos de 
							   comunicación y se cierra el programa*/
							jugando = false;
							numeroJugadores -= 2;
							dis.close();
							dos.close();
							oponente.dos.close();
							System.exit(0);
						}
					} catch (SocketException e) {	
						/* Si el oponente se rinde, se va a producir una "SocketException", así que se manda un código "6" para
						   que el cliente gestione ese escenario, se ponen los jugadores a null y se cierra el servidor.
						   Si hay algún problema en el proceso, se cierra el programa igualmente. */
						try {						
							oponente.dos.writeUTF("6");
							oponente.oponente = null;
							oponente = null;
							System.out.println("Estado del servidor: INACTIVO");
							System.exit(0);
						} catch (Exception f) {
							System.out.println("Estado del servidor: INACTIVO");
							System.exit(0);
						}
					} catch (IOException e) {
						e.printStackTrace();
						
						/* Si tiene lugar una IllegalException de las que se contemplan en el método "checkJugada", se informa
						   de ello al cliente mediante el codigo "3" y se reduce un turno para que el turno no cuente como válido*/
					} catch (IllegalStateException e) {
						dos.writeUTF("3" + e.getMessage());
						turno--;
					} catch (InvalidKeyException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (NoSuchPaddingException e) {
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						e.printStackTrace();
					} catch (BadPaddingException e) {
						e.printStackTrace();
					}
					
					// En cada iteración se suma un turno
					turno++;
				}
				
				/* Pasados los nueve turnos, si no existe resultado se considera empate entre los jugadores y 
				   se envía un "7" a los clientes, lo cual permite que los clientes gestione el evento de empate.
				   Acto seguido se cierran los flujos de comunicación y se mantiene el servidor encendido*/
				dos.writeUTF("7");
				oponente.dos.writeUTF("7");
				dis.close();
				dos.close();
				
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}
}
