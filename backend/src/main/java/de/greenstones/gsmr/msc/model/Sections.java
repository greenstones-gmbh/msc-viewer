package de.greenstones.gsmr.msc.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.greenstones.gsmr.msc.model.Props.Prop;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a section/part of the MSC configuration.
 */
public class Sections {

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
	@JsonSubTypes({ @JsonSubTypes.Type(PropSection.class), @JsonSubTypes.Type(TableSection.class) })
	@Getter
	public static class Section {
		String name;
	}

	/**
	 * Represents a section with named properties.
	 */
	@NoArgsConstructor
	@ToString
	@Getter
	public static class PropSection extends Section {

		public PropSection(String name, List<Prop> props) {
			this.name = name;
			this.props = props;
		}

		List<Prop> props = new ArrayList<Prop>();
	}

	/**
	 * Represents a section with a table.
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@Getter
	public static class TableSection extends Section {

		List<String> columns = new ArrayList<String>();
		List<Obj> objects = new ArrayList<Obj>();

		public TableSection(String name, List<String> columns, List<Obj> objects) {
			this.name = name;
			this.columns = columns;
			this.objects = objects;
		}

	}

}