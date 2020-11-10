export const health = state => state.health;
export const flyway = state => {
  const f = state.health.flyway;
  if (!f) {
    return [];
  }
  return [...f.appliedMigration, ...f.pendingMigration];
};
export const deletedCollections = state => state.deletedCollections;
