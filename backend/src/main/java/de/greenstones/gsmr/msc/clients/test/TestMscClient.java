package de.greenstones.gsmr.msc.clients.test;

import de.greenstones.gsmr.msc.clients.MscClient;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class TestMscClient implements MscClient {

	TestOutputService outputService;

	@Override
	public MscSession connect() {
		return new MscSession() {

			@Override
			@SneakyThrows
			public String execute(String cmd) {
				return outputService.getOutput(cmd);
			}

			@Override
			public void disconnect() {
			}

		};
	}

}