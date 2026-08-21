package de.greenstones.gsmr.msc.ssh;

import de.greenstones.gsmr.msc.parser.MscParser;

public class TestSshMsc2 {

	private static final String HOST = "host";
	private static final String PWD = "pwd";

	public static void main(String[] args) {

		MscParser parser = new MscParser();

		SshClient sshShell = new SshClient(null, HOST, 2222);
		sshShell.setPassword(PWD);
		sshShell.loadUserConfig();

		sshShell.setOutputTransform(null);
		sshShell.setOutputSplitter(OutputSplitter.withRegExp(".*< \b $"));
		sshShell.setOutputWriter(null);

		sshShell.connect();
		String a = sshShell.run("ZEPO;");

		sshShell.run("Z;");

		sshShell.disconnect();

	}

}
