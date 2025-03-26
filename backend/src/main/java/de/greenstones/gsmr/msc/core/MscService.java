package de.greenstones.gsmr.msc.core;

import de.greenstones.gsmr.msc.clients.MscClient;
import de.greenstones.gsmr.msc.clients.MscClient.MscSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for executing MSC commands and caching their results.
 */
@AllArgsConstructor
public class MscService {

	MscClient client;
	CommandCache cache;

	public String executeQuery(String cmd, boolean force) {
		return executeJob(shell -> shell.executeQuery(cmd, force));
	}

	public String executeUpdate(String cmd) {
		return executeJob(shell -> shell.executeUpdate(cmd));
	}

	public <R> R executeJob(Job<R> job) {
		JobTemplate t = new JobTemplate(client, cache);
		R result = job.run(t);
		t.disconnect();
		return result;
	}

	public void clearCache() {
		cache.clear();
	}

	/**
	 * A job that can be executed on the MSC.
	 */
	@FunctionalInterface
	public interface Job<R> {
		R run(Shell shell);
	}

	/**
	 * A shell for executing MSC commands.
	 */
	public interface Shell {
		String executeQuery(String cmd, boolean force);

		String executeUpdate(String cmd);
	}

	@Slf4j
	static class JobTemplate implements Shell {

		MscClient client;
		CommandCache cache;

		MscSession session = null;

		public JobTemplate(MscClient client, CommandCache cache) {
			this.client = client;
			this.cache = cache;
		}

		public String executeQuery(String cmd, boolean force) {
			log.debug("executeQuery {} {} cache:{}", cmd, force, cache.contains(cmd));
			if (!force && cache.contains(cmd)) {
				return cache.read(cmd);
			}
			String output = execute(cmd);
			cache.write(cmd, output);
			return output;
		}

		public String executeUpdate(String cmd) {
			return execute(cmd);
		}

		protected String execute(String cmd) {
			if (session == null) {
				session = client.connect();
			}
			return session.execute(cmd);
		}

		public void disconnect() {
			if (session != null) {
				session.disconnect();
			}

		}
	}

}
