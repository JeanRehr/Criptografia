public class Main
{
	public static void main(String[] args)
	{
		Criptografia c = new Criptografia();
		//if (!c.checkConsole())
			//return;

		c.userInput();
		c.leArquivo();

		if (c.getCont() == 'c' || c.getCont() == 'C') {
			c.ciframento();
		} else if (c.getCont() == 'd' || c.getCont() == 'D') {
			c.deciframento();
		} else {
			System.out.println("invalido");
		}
	}
}