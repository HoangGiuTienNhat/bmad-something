# Issue: Story 1.1: Configure Role-Based Access for Admin and Sales Staff

## Status: OPEN

### Description
As an Admin,
I want role-based access control configured for Admin and Sales Staff,
So that sensitive actions are restricted while daily selling can proceed safely.

### Acceptance Criteria:
- Given a user with Admin role
- When the user accesses product, inventory, and reporting endpoints
- Then access is granted for all protected operations
- And audit-relevant endpoints return successful authorization.

- Given a user with Sales Staff role
- When the user attempts sensitive endpoints (user management, critical stock adjustment, destructive product actions)
- Then access is denied with a clear permission error
- And allowed sales operations remain accessible.
