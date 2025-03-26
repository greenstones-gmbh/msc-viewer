package de.greenstones.gsmr.msc.types;

import java.util.List;

import de.greenstones.gsmr.msc.core.Command;
import de.greenstones.gsmr.msc.core.Command.Params;
import de.greenstones.gsmr.msc.core.IdConverter;
import de.greenstones.gsmr.msc.core.ParamMapping;
import de.greenstones.gsmr.msc.model.Obj;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a configuration type (MSC LAC,BTS,..)with commands to find all or
 * one object,
 * an ID converter, a schema, and a graph node type.
 */
@AllArgsConstructor
@Getter
public class ConfigType {
	Command<List<Obj>> findAll;
	Command<Obj> findOne;
	IdConverter idConverter;
	ParamMapping idMapping;
	NodeConfigurer node;
	FrontendConfigurer frontend;

	/**
	 * Constructs a ConfigType with the specified commands, ID converter, and
	 * schema.
	 * 
	 * @param findAll     the command to find all objects
	 * @param findOne     the command to find one object
	 * @param idConverter the ID converter
	 */
	public ConfigType(Command<List<Obj>> findAll, Command<Obj> findOne, IdConverter idConverter,
			ParamMapping idMapping) {
		super();
		this.findAll = findAll;
		this.findOne = findOne;
		this.idConverter = idConverter;
		this.idMapping = idMapping;
	}

	public ConfigType(Command<List<Obj>> findAll, Command<Obj> findOne, IdConverter idConverter, ParamMapping idMapping,
			NodeConfigurer node) {
		super();
		this.findAll = findAll;
		this.findOne = findOne;
		this.idConverter = idConverter;
		this.idMapping = idMapping;
		this.node = node;
	}

	/**
	 * Gets the ID of the specified object.
	 * 
	 * @param obj the object
	 * @return the ID of the object
	 */
	public String getId(Obj obj) {
		Params params = Params.from(idMapping.map(obj));
		return idConverter.toRequestId(params);
	}
}