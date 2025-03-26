package de.greenstones.gsmr.msc.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import de.greenstones.gsmr.msc.parser.RegexpUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Represents a MSC command that can be executed with specific parameters and
 * parsed for results.
 *
 * @param <ResultType> the type of the result produced by this command
 */
@AllArgsConstructor
public class Command<ResultType> {

	final String commandTemplate;
	final Function<String, CommandOutput<ResultType>> parser;

	/**
	 * Creates a command string by interpolating the given parameters into the
	 * command template.
	 *
	 * @param params the parameters to interpolate into the command template
	 * @return the interpolated command string
	 */
	public String createCmd(Params params) {
		return RegexpUtils.interpolate(commandTemplate, params);
	}

	/**
	 * Parses the given output string using the command's parser.
	 *
	 * @param output the output string to parse
	 * @return the parsed command output
	 */
	public CommandOutput<ResultType> parse(String output) {
		return parser.apply(output);
	}

	/**
	 * Runs the command with the given parameters and executor function.
	 *
	 * @param params   the parameters to use for the command
	 * @param executor the function to execute the command
	 * @return the result of the command execution
	 */
	public CommandResult<ResultType> run(Params params, Function<String, String> executor) {
		String cmd = createCmd(params);
		String output = executor.apply(cmd);
		CommandOutput<ResultType> commandOutput = parse(output);
		CommandResult<ResultType> result = CommandResult.from(cmd + ";", commandOutput);
		return result;
	}

	/**
	 * Represents the parameters for a command.
	 */
	public static class Params extends HashMap<String, String> {
		/**
		 * Creates a Params object from the given map.
		 *
		 * @param v the map to create the Params object from
		 * @return the created Params object
		 */
		public static Params from(Map<String, String> v) {
			Params p = new Params();
			p.putAll(v);
			return p;
		}

		/**
		 * Joins the current Params object with another Params object.
		 *
		 * @param v the Params object to join with
		 * @return the joined Params object
		 */
		public Params join(Params v) {
			Params p = new Params();
			p.putAll(this);
			p.putAll(v);
			return p;
		}
	}

	/**
	 * Represents the output of a command.
	 *
	 * @param <Type> the type of the data contained in the command output
	 */
	@Data
	@AllArgsConstructor
	public static class CommandOutput<Type> {
		String version;
		String info;
		String timestamp;
		Type data;

		// public <Type> CommandOutput<Type> map(Function<CmdData, Type> transform) {
		// return new CommandOutput<Type>(version, info, timestamp,
		// transform.apply(data));
		// }
	}

	/**
	 * Represents the command, the result of a execution and meta data.
	 *
	 * @param <Type> the type of the data contained in the command result
	 */
	@Getter
	public static class CommandResult<Type> extends CommandOutput<Type> {

		String command;
		Map<String, Object> meta;

		public CommandResult(String command, String version, String info, String timestamp, Type data) {
			super(version, info, timestamp, data);
			this.command = command;
			this.meta = new HashMap<String, Object>();
		}

		/**
		 * Creates a CommandResult object from the given command string and command
		 * output.
		 *
		 * @param command the command string
		 * @param output  the command output
		 * @param <Type>  the type of the data contained in the command result
		 * @return the created CommandResult object
		 */
		public static <Type> CommandResult<Type> from(String command, CommandOutput<Type> output) {
			return new CommandResult<Type>(command, output.getVersion(), output.getInfo(), output.getTimestamp(),
					output.getData());
		}

	}

}