export interface IHumanResources {
  id?: string;
  investmentsLocally?: number | null;
  laborAccidentsindex?: number | null;
}

export const defaultValue: Readonly<IHumanResources> = {};
