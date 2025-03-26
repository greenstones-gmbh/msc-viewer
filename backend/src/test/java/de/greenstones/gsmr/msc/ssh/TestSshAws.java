package de.greenstones.gsmr.msc.ssh;

public class TestSshAws {

	
	public static void main(String[] args) {
		
		SshClient sshShell = new SshClient("ec2-user", "35.157.29.238", 22);
		sshShell.setIdentityFile("/Users/artfh/.ssh/aws-eu-central-1.pem");

		sshShell.setOutputTransform(OutputTransform.createCommandTransform());
		sshShell.setOutputSplitter(OutputSplitter.withPrompt());
		sshShell.setOutputWriter(null);

		sshShell.connect();

		String a = sshShell.run("sleep 1 && ls");
		String b=sshShell.run("pwd");

		sshShell.disconnect();

		System.out.println("-----");
		System.out.println(a);
		System.out.println("-----");
		System.out.println(b);
		
	}


	
	
}
