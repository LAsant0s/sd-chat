package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Servidor extends Thread {
	
	private static Map<String, PrintStream> clientes = new HashMap<String, PrintStream>();
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
			clientes.put(meuNome, saida);
			String linha = entrada.readLine();
			while ((linha != null) && (!linha.trim().equals(""))){
				if(linha.startsWith("/p")) {
					String regex = "^\\/p\\\"(.*?)\\\"";
					
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(linha);
					
					linha = linha.replaceAll(regex, "");
					
					while (m.find()) {
						sendToOne(m.group(1), saida," sussurou para você: ",linha);
					}
				} else {
					sendToAll(saida,": ",linha);
				}
				recordLog(linha);
				linha = entrada.readLine();
			}
			sendToAll(saida," saiu "," do Chat!");
			clientes.remove(meuNome);
			conexao.close();
		} catch (IOException e) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void sendToAll(PrintStream saida, String acao, String linha) throws IOException {		
		for (String key : clientes.keySet()) {
			PrintStream chat = (PrintStream) clientes.get(key);
			if (chat != saida) {
				chat.println(meuNome + acao + linha);
			}
			if (acao.equals(" saiu ")) {
				if (chat == saida)
					chat.println("");
			}
    	}
	}
	
	private void sendToOne(String targetName, PrintStream saida, String acao, String linha) {		
		PrintStream chat = clientes.get(targetName);
		if (chat != saida) {
			chat.println(meuNome + acao + linha);
		}
		if (acao.equals(" saiu ")) {
			if (chat == saida)
				chat.println("");
		}
	}
	
	private void recordLog(String msg) {
		gravarArq.println("<" + conexao.getInetAddress().getHostName() + ">@<" 
				+ conexao.getLocalAddress() + ">@<" + conexao.getPort() + ">@<" + msg);
		gravarArq.flush();
	};
}