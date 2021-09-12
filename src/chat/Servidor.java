package chat;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread {
	
	private static Vector clientes;
	private Socket conexao;
	private String meuNome;
	static PrintWriter gravarArq;
	
	public Servidor(Socket s){
		conexao = s;
	}
	
	public static void main(String[] args) {
		try {
			gravarArq = new PrintWriter("log.txt");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			clientes = new Vector();
			
			ServerSocket s = new ServerSocket(2000);
			while (true) {
				System.out.print("Esperando conectar...");
				Socket conexao = s.accept();
				System.out.println(" Conectou!");
				Thread t = new Servidor(conexao);
				t.start();
			}
		} catch (IOException e) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	public void run(){
		BufferedReader entrada = null;
		try {
			entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			PrintStream saida = new PrintStream(conexao.getOutputStream());
			meuNome = entrada.readLine();
			if (meuNome == null){
				return;
			}
			clientes.add(saida);
			String linha = entrada.readLine();
			while ((linha != null) && (!linha.trim().equals(""))){
				sendToAll(saida,": ",linha);
				recordLog(linha);
				linha = entrada.readLine();
			}
			sendToAll(saida," saiu "," do Chat!");
			clientes.remove(saida);
			conexao.close();
		} catch (IOException e) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void sendToAll(PrintStream saida, String acao, String linha) throws IOException {
		@SuppressWarnings("rawtypes")
		Enumeration e = clientes.elements();		
		while (e.hasMoreElements()) {
			PrintStream chat = (PrintStream) e.nextElement();
			if (chat != saida) {
				chat.println(meuNome + acao + linha);
			}
			if (acao.equals(" saiu ")) {
				if (chat == saida)
					chat.println("");
			}
		}
	}
	
	private void recordLog(String msg) {
		gravarArq.println("<" + conexao.getInetAddress().getHostName() + ">@<" 
				+ conexao.getLocalAddress() + ">@<" + conexao.getPort() + ">@<" + msg);
		gravarArq.flush();
	};
}
