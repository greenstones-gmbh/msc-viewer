package de.greenstones.gsmr.msc.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class Props {

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
	@JsonSubTypes({ @JsonSubTypes.Type(ValueProp.class), @JsonSubTypes.Type(MultiValueProp.class),
			@JsonSubTypes.Type(UnknownProp.class) })
	public static class Prop {

	}

	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	@Getter
	public static class ValueProp extends Props.Prop {
		String name;
		String shortName;
		String value;
	}

	@RequiredArgsConstructor
	@NoArgsConstructor
	@ToString
	@Getter
	public static class MultiValueProp extends Props.Prop {
		@NonNull
		String name;
		List<ValueProp> props = new ArrayList<ValueProp>();

	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class UnknownProp extends Props.Prop {
		String content;
	}
}