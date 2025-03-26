package de.greenstones.gsmr.msc.clients;

import de.greenstones.gsmr.msc.ApplicationException;
import lombok.SneakyThrows;

public class FakeMscClient implements MscClient {

	@Override
	public MscSession connect() {
		return new MscSession() {

			@Override
			@SneakyThrows
			public String execute(String cmd) {
				throw new ApplicationException(
						"The command '" + cmd
								+ "' cannot be executed.\nThis MSC instance is for demonstration purposes only and does not support command execution.");
			}

			@Override
			public void disconnect() {
			}

		};
	}
}