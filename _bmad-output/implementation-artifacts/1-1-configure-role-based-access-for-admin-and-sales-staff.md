# Story 1.1: Configure Role-Based Access for Admin and Sales Staff

## Story

As an Admin,
I want role-based access control configured for Admin and Sales Staff,
So that sensitive actions are restricted while daily selling can proceed safely.

## Status

done

## Acceptance Criteria

**AC1:** Given a user with Admin role, When the user accesses product, inventory, and reporting endpoints, Then access is granted for all protected operations And audit-relevant endpoints return successful authorization.

**AC2:** Given a user with Sales Staff role, When the user attempts sensitive endpoints (user management, critical stock adjustment, destructive product actions), Then access is denied with a clear permission error And allowed sales operations remain accessible.

## Completion Notes
Role-based access control has been implemented using Spring Security. Admin role has full access, while Sales role is restricted from destructive or sensitive operations.
