import { Table } from "react-bootstrap";
import {
  getMscPropValue,
  isMultiValueProp,
  isValueProp,
  MscField,
  MscMultiValueProp,
  MscObj,
  MscProp,
  MscSection,
  MscValueProp,
} from "./MsgObj";

// class MscObjViewConfigBuilder {
//   name: string;
//   hint?: string;
//   options: FieldOptions<MscObj, string>;
// }

export function MscObjView({
  obj,
  skipProps,
  fields,
  context,
}: {
  obj: MscObj;
  fields?: MscField[];
  skipProps?: string[];
  context?: any;
}) {
  return (
    <>
      {obj.sections.map((section, i) => (
        <MscObjSectionView
          obj={obj}
          section={section}
          key={i}
          skipProps={skipProps}
          fields={fields}
          context={context}
        />
      ))}
    </>
  );
}

export function MscObjSectionView({
  obj,
  section,
  skipProps,
  fields,
  context,
}: {
  section: MscSection;
  skipProps?: string[];
  fields?: MscField[];
  obj: MscObj;
  context?: any;
}) {
  return (
    <Table className="mb-4" size="sm" hover>
      {section.name && (
        <thead>
          <tr>
            <th colSpan={3}>
              <b>{section.name}</b>
            </th>
          </tr>

          {section.columns && (
            <tr>
              {section.columns?.map((c) => (
                <th className="bg-light">{c}</th>
              ))}
            </tr>
          )}
        </thead>
      )}
      <tbody>
        {section.props?.map((prop, i) => (
          <MscPropView
            key={i}
            obj={obj}
            prop={prop}
            skipProps={skipProps}
            fields={fields}
            context={context}
          />
        ))}

        {section.objects?.map((obj, i) => (
          <tr>
            {section.columns?.map((c) => (
              <td>{getMscPropValue(obj, c)}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </Table>
  );
}

export function MscPropView({
  obj,
  prop,
  skipProps,
  fields = [],
  context,
}: {
  prop: MscProp;
  skipProps?: string[];
  fields?: MscField[];
  obj: MscObj;
  context?: any;
}) {
  if (isValueProp(prop)) {
    const field = fields.find(
      (f) => !f.hint && (f.name === prop.name || f.name === prop.shortName)
    );
    return (
      <MscValuePropView
        obj={obj}
        prop={prop}
        skipProps={skipProps}
        field={field}
        context={context}
      />
    );
  }

  if (isMultiValueProp(prop)) {
    return (
      <MscMultiValuePropView
        obj={obj}
        prop={prop}
        skipProps={skipProps}
        fields={fields}
        context={context}
      />
    );
  }

  return (
    <tr>
      <td colSpan={3}>{JSON.stringify(prop)}</td>
    </tr>
  );
}

function MscValuePropView({
  obj,
  prop,
  skipProps = [],
  field,
}: {
  prop: MscValueProp;
  skipProps?: string[];
  field?: MscField;
  obj: MscObj;
  context?: any;
}) {
  if (skipProps.includes(prop.name)) return null;
  return (
    <tr>
      <td>{prop.name}</td>
      <td style={{ width: "10em" }}>{prop.shortName}</td>
      <td style={{ width: "20em" }}>
        {field ? field.render(obj) : prop.value}
      </td>
    </tr>
  );
}

function MscMultiValuePropView({
  obj,
  prop,
  skipProps = [],
  fields = [],
  context,
}: {
  prop: MscMultiValueProp;
  skipProps?: string[];
  obj: MscObj;
  fields?: MscField[];
  context?: any;
}) {
  if (skipProps.includes(prop.name)) return null;

  const findField = (p: MscValueProp) =>
    fields.find((f) => f.name === prop.name && p.name === f.hint);
  return (
    <>
      {prop.props.map((p) => (
        <MscValuePropView
          obj={obj}
          prop={{
            name: `${prop.name} ${p.name}`,
            value: p.value,
          }}
          field={findField(p)}
          skipProps={skipProps}
          context={context}
        />
      ))}
    </>
  );
}
