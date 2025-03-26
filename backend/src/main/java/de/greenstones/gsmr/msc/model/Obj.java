package de.greenstones.gsmr.msc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.greenstones.gsmr.msc.ApplicationException;
import de.greenstones.gsmr.msc.model.Props.MultiValueProp;
import de.greenstones.gsmr.msc.model.Props.Prop;
import de.greenstones.gsmr.msc.model.Props.ValueProp;
import de.greenstones.gsmr.msc.model.Sections.PropSection;
import de.greenstones.gsmr.msc.model.Sections.Section;
import de.greenstones.gsmr.msc.model.Sections.TableSection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a single MSC configuration/object with a set of sections.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Obj {

	protected List<Section> sections = new ArrayList<>();

	public Optional<Prop> getProp(String name) {
		return sections.stream().filter(s -> s instanceof PropSection).map(s -> (PropSection) s)
				.flatMap(s -> s.getProps().stream())
				.filter(p -> p instanceof ValueProp || p instanceof MultiValueProp).filter(p -> {
					if (p instanceof ValueProp) {
						var vp = (ValueProp) p;
						return (name.equals(vp.getShortName()) || name.equals(vp.getName()));
					}

					if (p instanceof MultiValueProp) {
						var vp = (MultiValueProp) p;
						return name.equals(vp.getName());
					}

					return false;
				}).findFirst();
	}

	public String getValue(String name) {
		return getProp(name).filter(p -> p instanceof ValueProp).map(p -> (ValueProp) p).map(p -> p.getValue())
				.orElseThrow(() -> new ApplicationException(
						"ValueProp " + name + " not exists."));
	}

	public String getValue(String name, String hint) {
		Optional<MultiValueProp> map = getProp(name).filter(p -> p instanceof MultiValueProp)
				.map(p -> (MultiValueProp) p);

		MultiValueProp mvp = map
				.orElseThrow(() -> new ApplicationException(
						"MultiValueProp " + name + "|" + hint + " not exists. Name nof found."));

		return mvp.getProps().stream().filter(p -> hint.equals(p.getName())).findFirst().map(p -> p.getValue())
				.orElseThrow(() -> new ApplicationException(
						"MultiValueProp " + name + "|" + hint + " not exists. Hint nof found."));

	}

	public Section findSection(String name) {
		return sections.stream().filter(s -> name.equals(s.getName())).findFirst()
				.orElseThrow(() -> new ApplicationException(
						"Section " + name + " not exists. Existing sections: "
								+ sections.stream().map(s -> s.getName()).collect(Collectors.joining(",")) + " obj:"
								+ sections));
	}

	public TableSection findTableSection(String name) {
		Section section = findSection(name);
		return (TableSection) section;
	}

}