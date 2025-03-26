package de.greenstones.gsmr.msc.clients;
public interface MscClient {
	MscSession connect();
	
	public interface MscSession {
		String execute(String cmd);
		void disconnect();
	}
}