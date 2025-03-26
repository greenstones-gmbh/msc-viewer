export interface MscResponse<Data> {
  info?: string;
  version?: string;
  command?: string;
  data: Data;
}
