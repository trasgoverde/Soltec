export interface IProviders {
  id?: string;
  agreementParis?: number | null;
  certifiedSustianable?: boolean | null;
}

export const defaultValue: Readonly<IProviders> = {
  certifiedSustianable: false,
};
