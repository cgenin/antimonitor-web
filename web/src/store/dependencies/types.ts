// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface Dependencies {
  [key: string]: string[];
}


export interface DependenciesState {
  dependencies: Dependencies,
  resources: string[],
}


export class ResourceDto {
  public label: string;
  public sublabel: string;
  public value: string;

  constructor(label: string, sublabel = 'Java') {
    this.label = label;
    this.value = label;
    this.sublabel = sublabel;
  }
}

export interface UpdateOneDependency {
  resource: string
  content: any
}
