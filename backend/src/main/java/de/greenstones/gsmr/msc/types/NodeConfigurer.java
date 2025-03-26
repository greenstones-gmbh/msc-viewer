package de.greenstones.gsmr.msc.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.greenstones.gsmr.msc.core.ParamMapping;
import de.greenstones.gsmr.msc.core.Command.CommandResult;
import de.greenstones.gsmr.msc.core.MscInstance.ListAndDetails;
import de.greenstones.gsmr.msc.core.MscInstance.MscRepository;
import de.greenstones.gsmr.msc.model.Obj;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class NodeConfigurer {
    String label;
    String color;
    String nameTemplate;
    String displayName;
    ParamMapping propsMapping;
    boolean useDetails = false;
    List<RelationType> relations = new ArrayList<RelationType>();

    /**
     * Constructs a GraphNodeType with the specified label and property mapping.
     * 
     * @param label        the label of the graph node
     * @param propsMapping the property mapping
     */
    public NodeConfigurer(String label, ParamMapping propsMapping) {
        super();
        this.label = label;
        this.propsMapping = propsMapping;
    }

    public NodeConfigurer typeLabel(String label) {
        this.label = label;
        return this;
    }

    public NodeConfigurer propMapping(String propsMapping) {
        this.propsMapping = ParamMapping.from(propsMapping);
        return this;
    }

    public NodeConfigurer propMapping(String propsMapping, Map<String, String> valueMapping) {
        this.propsMapping = ParamMapping.from(propsMapping);
        this.propsMapping.setValueMapping(valueMapping);
        return this;
    }

    public NodeConfigurer color(String color) {
        this.color = color;
        return this;
    }

    public NodeConfigurer nameTemplate(String nameTemplate) {
        this.nameTemplate = nameTemplate;
        return this;
    }

    public NodeConfigurer displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Enables the use of details for this graph node type.
     * 
     * @return this GraphNodeType instance
     */
    public NodeConfigurer useDetails() {
        this.useDetails = true;
        return this;
    }

    /**
     * Creates a new GraphNodeType with the specified label and mapping.
     * 
     * @param label   the label of the graph node
     * @param mapping the property mapping
     * @return a new GraphNodeType instance
     */
    public static NodeConfigurer create(String label, String mapping) {
        return new NodeConfigurer(label, ParamMapping.from(mapping));
    }

    /**
     * Loads data from the repository for this graph node type.
     * 
     * @param repository the repository
     * @param type       the type of data
     * @param force      whether to force reload
     * @return a list of objects
     */
    public List<Obj> loadData(MscRepository repository, String type, boolean force) {
        if (isUseDetails()) {
            ListAndDetails listAndDetails = repository.getListAndDetails(type, force);
            return listAndDetails.getDetails().stream().map(m -> m.getData()).collect(Collectors.toList());
        } else {
            CommandResult<List<Obj>> all = repository.findAll(type, force);
            return all.getData();
        }
    }

    /**
     * Adds a relation to this graph node type.
     * 
     * @param target  the target of the relation
     * @param relName the name of the relation
     * @param mapping the property mapping
     * @return this GraphNodeType instance
     */
    public NodeConfigurer relation(String target, String relName, String mapping) {
        RelationType r = new RelationType(target, relName, ParamMapping.from(mapping), null);
        relations.add(r);
        return this;
    }

    /**
     * Adds a relation to this graph node type with a table section.
     * 
     * @param target       the target of the relation
     * @param relName      the name of the relation
     * @param tableSection the table section
     * @param mapping      the property mapping
     * @return this GraphNodeType instance
     */
    public NodeConfigurer relation(String target, String relName, String tableSection, String mapping) {
        RelationType r = new RelationType(target, relName, ParamMapping.from(mapping), tableSection);
        relations.add(r);
        return this;
    }

    @AllArgsConstructor
    @Getter
    public static class RelationType {
        String target;
        String name;
        ParamMapping mapping;
        String tableSection;

    }

}