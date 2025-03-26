export function parseIdParams(id: string) {
  const idParams = id.split(",").reduce((p, c) => {
    const parts = c.split("=");
    p[parts[0]] = parts[1];
    return p;
  }, {} as any);
  return idParams;
}

export function formatTemplate(
  template: string,
  values?: Record<string, string>
): string {
  const valuesMap = values ?? {};
  const pattern = /\$\{(.+?)\}/g;

  // Extract placeholders from the template
  const placeholders = new Set<string>(
    Array.from(template.matchAll(pattern), (match) => match[1])
  );

  // Extract keys from the map
  const mapKeys = new Set(Object.keys(valuesMap));

  // Check for placeholders without corresponding map entries
  const missingKeys = new Set(
    [...Array.from(placeholders)].filter(
      (placeholder) => !mapKeys.has(placeholder)
    )
  );

  // Check for map entries without corresponding placeholders
  const extraKeys = new Set(
    [...Array.from(mapKeys)].filter((key) => !placeholders.has(key))
  );

  // If there are any missing or extra keys, throw an exception
  if (missingKeys.size > 0 || extraKeys.size > 0) {
    throw new Error(
      `Mismatch between placeholders and map keys. Missing keys: ${Array.from(
        missingKeys
      )}, Extra keys: ${Array.from(extraKeys)}`
    );
  }

  // Replace placeholders with corresponding values from the map
  return template.replace(
    pattern,
    (_, variableName) => valuesMap[variableName]
  );
}

export function formatAny(template: string, v: any) {
  const varPattern = new RegExp("\\$\\{(\\w+)\\}", "g");
  const varNames = new Set<string>(
    Array.from(template.matchAll(varPattern), (match) => match[1])
  );

  const data: Record<string, string> = {};
  Object.keys(v)
    .filter((k) => varNames.has(k))
    .forEach((k) => {
      data[k] = v[k];
    });
  return formatTemplate(template, data);
}
