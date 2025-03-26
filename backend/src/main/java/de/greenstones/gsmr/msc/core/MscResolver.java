package de.greenstones.gsmr.msc.core;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.greenstones.gsmr.msc.ApplicationException;
import lombok.AllArgsConstructor;

/**
 * Resolves MSC instances by their ID.
 */
@AllArgsConstructor
public class MscResolver {

	Map<String, MscInstance> mscs;

	public MscInstance find(String id) {
		return Optional.ofNullable(mscs.get(id)).orElseThrow(() -> new MscNotFoundException(id));
	}

	public Set<String> getMscIds() {
		return mscs.keySet();
	}

	public static class MscNotFoundException extends ApplicationException {
		private static final long serialVersionUID = 7901798750223164708L;

		public MscNotFoundException(String mscId) {
			super("MSC not found. MSC ID: " + mscId);
		}
	}

}
