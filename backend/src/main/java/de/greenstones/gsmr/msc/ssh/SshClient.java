package de.greenstones.gsmr.msc.ssh;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.OpenSSHConfig;
import com.jcraft.jsch.Session;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SshClient {

	JSch jsch;
	Session session;
	Channel channel;
	InputStream inputStream;
	PrintStream commander;

	@Setter
	PrintWriter outputWriter = new PrintWriter(System.out, true);

	@Setter
	String user;

	@Setter
	String host;

	@Setter
	String password;

	@Setter
	String identityFile;

	@Setter
	int port = 22;

	@Setter
	OutputSplitter outputSplitter = OutputSplitter.withPrompt();

	@Setter
	OutputTransform outputTransform;

	@Setter
	String kexAlgorithms;

	static {
		JSch.setConfig("kex", JSch.getConfig("kex") + ",diffie-hellman-group-exchange-sha1");
		JSch.setConfig("server_host_key", JSch.getConfig("server_host_key") + ",ssh-dss");
	}

	public SshClient(String user, String host, int port) {
		this.user = user;
		this.host = host;
		this.port = port;
		this.jsch = new JSch();
	}

	@SneakyThrows
	public void loadUserConfig() {
		String configFile = System.getProperty("user.home") + File.separator + ".ssh" + File.separator + "config";
		File file = new File(configFile);
		if (file.exists()) {
			final OpenSSHConfig openSSHConfig = OpenSSHConfig.parseFile(file.getAbsolutePath());
			jsch.setConfigRepository(openSSHConfig);
		}
	}

	@SneakyThrows
	public void connect() {

		if (identityFile != null)
			jsch.addIdentity(identityFile);

		session = jsch.getSession(user, host, port);

		if (password != null)
			session.setPassword(password);

		// JSch.setConfig("kex", session.getConfig("kex") +
		// ",diffie-hellman-group-exchange-sha1,diffie-hellman-group14-sha1");

		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();

		channel = session.openChannel("shell");
		channel.setInputStream(null);
		inputStream = channel.getInputStream();

		commander = new PrintStream(channel.getOutputStream(), true);
		channel.connect();
		Thread.sleep(200l);
		clear();
	}

	public String run(String cmd) {
		commander.println(cmd + "\r");
		commander.flush();
		return OutputTransform.transform(waitForCommandOutput(), outputTransform);

	}

	@SneakyThrows
	public void disconnect() {
		channel.disconnect();
		session.disconnect();

	}

	@SneakyThrows
	protected String waitForCommandOutput() {
		byte[] buffer = new byte[1024];
		StringBuilder output = new StringBuilder();

		while (true) {
			while (inputStream.available() > 0) {
				int i = inputStream.read(buffer, 0, 1024);
				if (i < 0)
					break;

				String chunk = new String(buffer, 0, i);
				output.append(chunk);
				if (outputWriter != null) {
					outputWriter.print(chunk);
					outputWriter.flush();
				}

				if (outputSplitter.isCommandFinished(output.toString())) {
					return output.toString();
				}
			}

			if (channel.isClosed()) {
				if (inputStream.available() > 0)
					continue;
				System.out.println("Exit Status: " + channel.getExitStatus());
				break;
			}
			Thread.sleep(20);
		}
		return output.toString();
	}

	@SneakyThrows
	protected void clear() {
		if (inputStream.available() > 0) {
			byte[] chars = new byte[inputStream.available()];
			inputStream.read(chars);

			if (outputWriter != null) {
				String s = new String(chars, Charset.defaultCharset());

				outputWriter.print(s);
				outputWriter.flush();
			}

		}
	}

	public static void main(String[] args) {

		SshClient sshShell = new SshClient("ec2-user", "35.157.29.238", 22);
		sshShell.setIdentityFile("/Users/artfh/.ssh/aws-eu-central-1.pem");

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