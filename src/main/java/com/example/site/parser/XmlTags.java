package com.example.site.parser;

public final class XmlTags {

    private XmlTags() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String PAGE = "Page";           // Елемент однієї сторінки
    public static final String ATTR_ID = "id";          // Атрибут унікального ідентифікатора сторінки
    public static final String TITLE = "Title";         // Назва сторінки
    public static final String TYPE = "Type";           // Тип сторінки (ADVERTISING, NEWS, PORTAL, MIRROR)
    public static final String CHARS = "Chars";         // Блок додаткових характеристик сторінки
    public static final String EMAIL = "Email";         // Наявність електронної пошти (true/false)
    public static final String NEWS = "News";           // Наявність новин (true/false)
    public static final String ARCHIVES = "Archives";   // Наявність архівів для завантаження (true/false)
    public static final String VOTING = "Voting";       // Режим голосування (NONE, ANONYMOUS, AUTHORIZED)
    public static final String DOWNLOADS = "Downloads"; // Умови завантаження контенту (FREE, PAID)
    public static final String AUTHORIZE = "Authorize"; // Необхідність авторизації (true/false)
}