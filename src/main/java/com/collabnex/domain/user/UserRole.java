package com.collabnex.domain.user;

/**
 * Defines the roles available in the CollabNex system.
 * <ul>
 *   <li>{@code CLIENT} — Can post jobs and view shortlisted candidates.</li>
 *   <li>{@code FREELANCER} — Can browse jobs and apply to them.</li>
 *   <li>{@code ADMIN} — Manages shortlisting; authenticated via environment credentials, not stored in DB.</li>
 * </ul>
 */
public enum UserRole {
    CLIENT,
    FREELANCER,
    ADMIN
}