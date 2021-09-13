export interface ITeams {
  id?: string;
  originMaterials?: number | null;
  originSteal?: number | null;
  originAluminium?: number | null;
  sustainableProviders?: boolean | null;
}

export const defaultValue: Readonly<ITeams> = {
  sustainableProviders: false,
};
