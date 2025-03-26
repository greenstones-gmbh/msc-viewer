package de.greenstones.gsmr.msc.ssh;

import de.greenstones.gsmr.msc.parser.MscParser;

public class TestSshMsc {

	public static void main(String[] args) {

		// JSch.setConfig("kex",
		// JSch.getConfig("kex")+",diffie-hellman-group-exchange-sha1");
		// JSch.setConfig("server_host_key",
		// JSch.getConfig("server_host_key")+",ssh-dss");
		//

		MscParser parser = new MscParser();

		// SshShell sshShell = new SshShell(null, "r4_mss2", 2222);
		SshClient sshShell = new SshClient("AREAMG", "127.0.0.1", 2222);
		sshShell.setPassword("D2ROP76");

		// sshShell.loadUserConfig();

		sshShell.setOutputTransform(null);
		sshShell.setOutputSplitter(OutputSplitter.withRegExp(".*< \b $"));
		sshShell.setOutputWriter(null);

		sshShell.connect();

		String a = sshShell.run("ZEPO;");
		System.err.println(a);

		// {
		// String a = sshShell.run("ZEPO;");
		// Output.write(a, "cells-full.txt");
		//
		// }

		// {
		// String a = sshShell.run("ZEPO::IDE;");
		// Output.write(a, "cells-ide.txt");
		//
		// }

		// {
		// String a = sshShell.run("ZEPO:NO=65102;");
		// Output.write(a, "cells-65102.txt");
		//
		// }

		//
		// {
		// String a = sshShell.run("ZELO;");
		// Output.write(a, "lacs.txt");
		// }

		// {
		// String a = sshShell.run("ZELO:LAC=00555;");
		// Output.write(a, "lacs-00555.txt");
		// }
		//
		//
		// {
		// String a = sshShell.run("ZRUI;");
		// Output.write(a, "asns.txt");
		// }
		//
		// {
		// String a = sshShell.run("ZHCO;");
		// Output.write(a, "cell_lists.txt");
		// }
		//
		// {
		// String a = sshShell.run("ZHCO:CLNAME=CLIST2000;");
		// Output.write(a, "cell_lists-2000.txt");
		// }

		//
		// {
		// String a = sshShell.run("ZEPJ:ALL;");
		// Output.write(a, "ltes.txt");
		// }

		// {
		// String a = sshShell.run("EPJ:ECGI:ECI=1111,EMCC=998,EMNC=06;");
		// Output.write(a, "ltes-1111.txt");
		// }
		//
		//
		// {
		// String a = sshShell.run("ZHAO;");
		// Output.write(a, "gcas.txt");
		// }
		//

		// {
		// String a = sshShell.run("ZHAO:GCAN=GCAN09999;");
		// Output.write(a, "gcas-9999.txt");
		// }

		//
		// {
		// String a = sshShell.run("ZHGO;");
		// Output.write(a, "gca-groups.txt");
		// }

		// {
		// String a = sshShell.run("ZHGO:GCREF=85004507:::STYPE=VGCS;");
		// Output.write(a, "gca-groups-85004507.txt");
		// }
		//

		sshShell.run("Z;");

		sshShell.disconnect();

	}

}
