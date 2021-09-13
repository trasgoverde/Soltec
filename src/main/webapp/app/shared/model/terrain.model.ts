export interface ITerrain {
  id?: string;
  energyForCommunity?: number | null;
  reInversion?: number | null;
}

export const defaultValue: Readonly<ITerrain> = {};
