package de.greenstones.gsmr.msc.clients;

import de.greenstones.gsmr.msc.ApplicationException;
import de.greenstones.gsmr.msc.ssh.OutputSplitter;
import de.greenstones.gsmr.msc.ssh.SshClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public class SshMscClient implements MscClient {

	String host;
	int port = 22;
	String user = null;
	String password;

	@Override
	public MscSession connect() {
		SshClient sshClient = new SshClient(getUser(), getHost(), getPort());
		sshClient.setPassword(getPassword());
		// sshShell.loadUserConfig();

		sshClient.setOutputTransform(null);
		sshClient.setOutputSplitter(OutputSplitter.withRegExp(".*< \b $"));
		sshClient.setOutputWriter(null);

		try {
			sshClient.connect();
		} catch (Exception e) {
			throw new ApplicationException(
					"Failed to connect to " + getHost() + " as " + getUser(), e);
		}

		log.info("connected to {}:{} as {}", getHost(), getPort(), getUser());

		return new MscSession() {

			@Override
			public String execute(String cmd) {
				log.info("execute: {}", cmd + ";");
				return sshClient.run(cmd + ";");
			}

			@Override
			public void disconnect() {
				sshClient.run("Z;");
				sshClient.disconnect();
				log.info("disconnected from {}", getHost());
			}
		};
	}

}
