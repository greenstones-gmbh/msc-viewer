package de.greenstones.gsmr.msc.ssh;

public class TestSshAm {

	public static void main(String[] args) {

		SshClient sshShell = new SshClient("user", "demo.gs.de", 22);
		sshShell.setPassword("pwd");

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
