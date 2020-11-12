export interface MigrationInfo {
  type: any
  checkSum: number
  version: any
  description: string
  script: string
  state: string
  installedOn: string
  installedBy: string
  executionTime: number
}

export interface FlywayInfo {
  appliedMigration: MigrationInfo[]
  currentMigration: MigrationInfo[]
  pendingMigration: MigrationInfo[]
}

export interface Health {
  up: boolean
  flyway?: FlywayInfo
}

export interface ServerState {
  health: Health
  deletedCollections: string[],
}
