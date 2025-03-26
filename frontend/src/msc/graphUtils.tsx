export function mscLabel(msc: string | undefined, label?: string) {
  return `${label || ""}:\`${msc || "-"}\` `;
}

export function encodeAsLabel(msc: string | undefined) {
  return `\`${msc || "-"}\` `;
}
