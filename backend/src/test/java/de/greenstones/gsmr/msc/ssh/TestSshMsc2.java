package de.greenstones.gsmr.msc.ssh;

import de.greenstones.gsmr.msc.parser.MscParser;
import de.greenstones.gsmr.msc.ssh.output.Output;

public class TestSshMsc2 {

	public static void main(String[] args) {

		MscParser parser = new MscParser();

		SshClient sshShell = new SshClient(null, "r4_mss2", 2222);
		sshShell.setPassword("D2ROP76");
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
