package de.greenstones.gsmr.msc.ssh;

public class TestSshAm {

	private static final String HOST = "host";
	private static final String PWD = "pwd";

	public static void main(String[] args) {

		SshClient sshShell = new SshClient("user", HOST, 22);
		sshShell.setPassword(PWD);

		sshShell.setOutputTransform(OutputTransform.createCommandTransform());
		sshShell.setOutputSplitter(OutputSplitter.withPrompt());
		sshShell.setOutputWriter(null);

		sshShell.connect();

		String a = sshShell.run("sleep 1 && ls");
		String b = sshShell.run("pwd");

		sshShell.disconnect();

		System.out.println("-----");
		System.out.println(a);
		System.out.println("-----");
		System.out.println(b);

	}

}
