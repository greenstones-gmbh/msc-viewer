package de.greenstones.gsmr.msc.ssh;

public class TestSshAws {

	private static final String HOST = "host";
	private static final String FILE = "file";

	public static void main(String[] args) {

		SshClient sshShell = new SshClient("ec2-user", HOST, 22);
		sshShell.setIdentityFile(FILE);

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
