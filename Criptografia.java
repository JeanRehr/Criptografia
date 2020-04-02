import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Criptografia
{
	private String nomeArquivo;
	private String senha;
	private byte[] bufferArquivo;
	private byte[] bufferAux;
	private byte[] bytes;
	private char cont;

	public boolean checkConsole()
	{
		if (System.console() != null)
			return true;

		return false;
	}

	public void userInput()
	{
		//if (!checkConsole())
			//return;

		//Console console = System.console();
		Scanner scanner = new Scanner(System.in);

		System.out.print("Gostaria de criptografar(c) ou descriptografar(d)? ");
		//this.cont = console.readLine().charAt(0);
		this.cont = scanner.nextLine().charAt(0);

		System.out.print("Nome do arquivo a ser lido. ");
		//this.nomeArquivo = console.readLine();
		this.nomeArquivo = scanner.nextLine();

		System.out.print("Senha do arquivo (Ate 16 caracteres). ");
		/*char[] ch = console.readPassword();
		while (ch.length > 16) {
			System.out.print("Senha tem mais que 16 caracteres, tente novamente.");
			ch = console.readPassword();
		}
		this.senha = String.valueOf(ch); // Convertendo senha para string.*/
		this.senha = scanner.nextLine();

		scanner.close();
	}

	public byte gerarChaveID()
	{
		byte chaveByte = 0;
		int a = 0;
		for (short i = 0; i < senha.length(); i++) {
			a = (int) senha.charAt(i);
			a += a;
		}
		chaveByte = (byte) (a % 256);

		return chaveByte;
	}

	public int leArquivoPad() {
		int arraySize = 0;
		final Path path = Paths.get("teste.txt");	
		try {
			final FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
		    final int bufsize = ((int) channel.size() + 2) & ~2;
		    final ByteBuffer buf = ByteBuffer.allocate(bufsize);
		    channel.read(buf);
		    arraySize = buf.array().length;
		} catch (Exception e) {
			System.out.println(e);
		}
		return arraySize;
	}

	public void leArquivo()
	{
		final Path path = Paths.get("teste.txt");	
		try {
			final FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
		    int bufSize = ((int) channel.size() + 9) & ~9;
			ByteBuffer buf = ByteBuffer.allocate(bufSize);
			channel.read(buf);
			bufferArquivo = new byte[buf.array().length];
			bufferArquivo = Files.readAllBytes(Paths.get(nomeArquivo));
			byte[] ar = bufferArquivo.clone();
			bufferAux = new byte[bufferArquivo.length];
			bytes = new byte[bufferArquivo.length];
		} catch (Exception e) {
			System.out.println("Arquivo nao existe, encerrando execucao.");
		}
	}

	public void ciframento()
	{
		cifraPrimeiroPasso(bufferArquivo, bufferAux);

		cifraSegundoPasso(bufferAux);

		cifraTerceiroPasso(bufferAux);

		cifraQuartoPasso(bufferAux, bytes);

		gravaArquivoCifrado(bytes);
	}

	public void deciframento()
	{
		cifraQuartoPasso(bufferArquivo, bufferAux);

		decifraTerceiroPasso(bufferAux);

		cifraSegundoPasso(bufferAux);

		cifraPrimeiroPasso(bufferAux, bytes);

		gravaArquivoDecifrado(bytes);
	}

	public void cifraPrimeiroPasso(byte[] buffer, byte[] bytes)
	{
		byte a;
		byte b;
		for (int i = 0; i < bytes.length; i++) {
			a = (byte) (buffer[i] << 4);
			b = (byte) (buffer[i] >>> 4 & 15);
			bytes[i] = (byte) (a | b);
		}
	}

	public void cifraSegundoPasso(byte[] buffer)
	{
		byte a = 0;
		byte b = 0;
		for (int i = 0; i < buffer.length; i += 2) {
			a = buffer[i];
			b = buffer[i + 1];
			buffer[i] = b;
			buffer[i + 1] = a;
		}
	}

	public void cifraTerceiroPasso(byte[] buffer)
	{
		byte a = 0;
		byte b = 0;
		byte c = 0;
		int i = 0;
		while (i < buffer.length) {
			a = buffer[i];
			b = buffer[i + 1];
			c = buffer[i + 2];
			buffer[i] = b;
			buffer[i + 1] = c;
			buffer[i + 2] = a;
			i += 3;

			a = buffer[i];
			c = buffer[i + 2];
			buffer[i] = c;
			buffer[i + 2] = a;
			i += 3;

			a = buffer[i];
			b = buffer[i + 1];
			c = buffer[i + 2];
			buffer[i] = c;
			buffer[i + 1] = a;
			buffer[i + 2] = b;
			i += 3;
		}
	}

	public void decifraTerceiroPasso(byte[] buffer)
	{
		byte a = 0;
		byte b = 0;
		byte c = 0;
		int i = 0;
		while (i < buffer.length) {
			a = buffer[i];
			b = buffer[i + 1];
			c = buffer[i + 2];
			buffer[i] = c;
			buffer[i + 1] = a;
			buffer[i + 2] = b;
			i += 3;

			a = buffer[i];
			c = buffer[i + 2];
			buffer[i] = c;
			buffer[i + 2] = a;
			i += 3;

			a = buffer[i];
			b = buffer[i + 1];
			c = buffer[i + 2];
			buffer[i] = b;
			buffer[i + 1] = c;
			buffer[i + 2] = a;
			i += 3;
		}
	}

	public void cifraQuartoPasso(byte[] buffer, byte[] bytes)
	{
		byte byteCifrado;
		byte chaveID = gerarChaveID();
		int i = 0;
		while (i < buffer.length) {
			byteCifrado = (byte) (buffer[i] ^ chaveID);
			bytes[i] = byteCifrado;
			i++;
		}
	}

	public void gravaArquivoCifrado(byte[] bytes)
	{
		try {
			FileOutputStream fos = new FileOutputStream(nomeArquivo + ".cif");
			fos.write(bytes);
			fos.close();
		} catch (Exception e) {
			System.out.println("Nao foi possivel salvar arquivo");
		}
	}

	public void gravaArquivoDecifrado(byte[] bytes)
	{
		try {
			FileOutputStream fos = new FileOutputStream(nomeArquivo + ".dec");
			fos.write(bytes);
			fos.close();
		} catch (Exception e) {
			System.out.println("Nao foi possivel salvar arquivo");
		}
	}

	public char getCont()
	{
		return cont;
	}
}