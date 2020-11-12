export interface MonithorNpmByName {
  [key: string]: any;
}

export interface ByName {
  name: string
  content: any
}

export interface MonithorNpm {
  list?: any[],
  byName: MonithorNpmByName
}

export interface Metrics {
  [key: string]: number
}

export interface Environnement{
  [key: string]: string
}

export interface ServerInServiceContent {
  host: string
  metrics: Metrics[]
  env:Environnement[]
}

export interface ServiceContent {
  servers: ServerInServiceContent[]
}

export interface MonithorService {
  service?: any
  server?: any
  content?: ServiceContent
}

export interface MonithorState {
  url?: string,
  service: MonithorService,
  npm: MonithorNpm
  servers: any[]
}

export interface Component {
  name: string
}


export interface Server {
  server: string
}

export interface ServiceOnServer {
  service: string
  server: string
}
