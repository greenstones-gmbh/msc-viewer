package de.greenstones.gsmr.msc.core;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.greenstones.gsmr.msc.core.Command.CommandResult;
import de.greenstones.gsmr.msc.core.Command.Params;
import de.greenstones.gsmr.msc.core.MscService.Shell;
import de.greenstones.gsmr.msc.gis.CsvFeatureProvider;
import de.greenstones.gsmr.msc.gis.FeatureProvider;
import de.greenstones.gsmr.msc.model.Obj;
import de.greenstones.gsmr.msc.types.ConfigType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a MSC instance with a set of configuration types and a service to
 * execute commands.
 */
@AllArgsConstructor
@Getter
public class MscInstance {

	Map<String, ConfigType> types;
	MscService mscService;
	Map<String, FeatureProvider> featureProviders;

	public <R> R execute(Job<R> job) {
		return mscService.executeJob(shell -> {
			MscRepository runner = new MscRepository(types, shell);
			return job.run(runner);

		});
	}

	@FunctionalInterface
	public interface Job<R> {
		R run(MscRepository repository);
	}

	@Slf4j
	@AllArgsConstructor
	public static class MscRepository {
		@Getter
		private Map<String, ConfigType> types;
		private Shell shell;

		public CommandResult<List<Obj>> findAll(String typeName, boolean force) {
			log.debug("findAll {} force:{}", typeName, force);
			ConfigType type = types.get(typeName);
			return type.getFindAll().run(null, cmd -> shell.executeQuery(cmd, force));
		}

		public CommandResult<Obj> findOne(String typeName, String id, boolean force) {
			log.debug("findOne {} id:{} force:{}", typeName, id, force);
			ConfigType type = types.get(typeName);
			Params params = type.getIdConverter().fromRequestId(id);
			return type.getFindOne().run(params, cmd -> shell.executeQuery(cmd, force));
		}

		public ListAndDetails getListAndDetails(String typeName, boolean force) {
			CommandResult<List<Obj>> list = findAll(typeName, force);
			ConfigType type = types.get(typeName);
			List<CommandResult<Obj>> items = list.getData().stream().map(c -> {
				String id = type.getId(c);
				return findOne(typeName, id, force);
			}).collect(Collectors.toList());
			return new ListAndDetails(list, items);
		}

	}

	@AllArgsConstructor
	@Getter
	public static class ListAndDetails {
		CommandResult<List<Obj>> list;
		List<CommandResult<Obj>> details;
	}

}
