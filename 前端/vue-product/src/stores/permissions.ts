// src/permissions.ts
export const rolePermissions: Record<string, string[]> = {
    employee: ['create', 'read', 'updateStatus'],
    manager: ['create', 'read', 'updateStatus', 'updateDetails'],
    superadmin: ['create', 'read', 'updateStatus', 'updateDetails', 'delete', 'editRoles'],
  }
  