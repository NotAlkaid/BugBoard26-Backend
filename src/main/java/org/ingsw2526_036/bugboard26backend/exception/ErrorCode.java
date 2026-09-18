package org.ingsw2526_036.bugboard26backend.exception;

public enum ErrorCode {
    // Autenticazione & Permessi
    BAD_CREDENTIALS,
    TOKEN_EXPIRED,
    ACCESS_DENIED,
    USER_NOT_FOUND,

    // Risorse non trovate
    RESOURCE_NOT_FOUND,
    PROJECT_NOT_FOUND,
    ISSUE_NOT_FOUND,
    LABEL_NOT_FOUND,

    // Conflitti & Risorse duplicate
    EMAIL_ALREADY_IN_USE,
    USERNAME_ALREADY_IN_USE,
    PROJECT_NAME_ALREADY_EXISTS,
    LABEL_ALREADY_EXISTS,
    USER_ALREADY_PARTICIPANT,
    DUPLICATE_RESOURCE,

    // Regole di Dominio & Vincoli di Business
    MAX_LABELS_EXCEEDED,
    ISSUE_NOT_IN_PROJECT,
    USER_NOT_IN_PROJECT,
    NOT_PROJECT_CREATOR,
    INVALID_STATE_TRANSITION,
    DATA_INTEGRITY_VIOLATION,

    // Validazione dati & Errori generici
    VALIDATION_ERROR,
    BAD_REQUEST,
    INTERNAL_SERVER_ERROR
}
