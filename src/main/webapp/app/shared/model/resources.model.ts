export interface IResources {
  id?: string;
  waterConsumtion?: number | null;
  reforestryIndex?: number | null;
}

export const defaultValue: Readonly<IResources> = {};
