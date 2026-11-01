package com.example.site.exception;

/* Виняток для обгортання всіх системних помилок,
що виникають під час парсингу, валідації чи XSLT-перетворення XML.
 */
public class SiteException extends Exception {

    // Створює новий виняток із текстовим повідомленням та початковою причиною (cause).
    public SiteException(String message, Throwable cause) {
        super(message, cause);
    }
}