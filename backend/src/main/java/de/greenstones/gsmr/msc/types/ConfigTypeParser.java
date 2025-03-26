package de.greenstones.gsmr.msc.types;

import java.util.List;

import de.greenstones.gsmr.msc.core.Command.CommandOutput;
import de.greenstones.gsmr.msc.model.Obj;

public interface ConfigTypeParser {

    public CommandOutput<List<Obj>> list(String content);

    public CommandOutput<Obj> obj(String content);
}
