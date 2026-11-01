package com.example.site.parser;

import com.example.site.model.*;

public class PageBuilder {

    // Тимчасові поля для збору даних сторінки
    private String id;
    private String title;
    private PageType type;
    private boolean email;
    private boolean news;
    private boolean archives;
    private VotingType voting;
    private DownloadType downloads;
    private boolean authorize;

    // Записує та очищає значення тега з XML
    public PageBuilder withField(String tag, String value) {
        if (tag == null || value == null) {
            return this;
        }

        String trimmedValue = value.trim();

        switch (tag) {
            case XmlTags.ATTR_ID:
                this.id = trimmedValue;
                break;
            case XmlTags.TITLE:
                this.title = trimmedValue;
                break;
            case XmlTags.TYPE:
                this.type = PageType.valueOf(trimmedValue);
                break;
            case XmlTags.EMAIL:
                this.email = Boolean.parseBoolean(trimmedValue);
                break;
            case XmlTags.NEWS:
                this.news = Boolean.parseBoolean(trimmedValue);
                break;
            case XmlTags.ARCHIVES:
                this.archives = Boolean.parseBoolean(trimmedValue);
                break;
            case XmlTags.VOTING:
                this.voting = VotingType.valueOf(trimmedValue);
                break;
            case XmlTags.DOWNLOADS:
                this.downloads = DownloadType.valueOf(trimmedValue);
                break;
            case XmlTags.AUTHORIZE:
                this.authorize = Boolean.parseBoolean(trimmedValue);
                break;
            default:
                // Теги обгортки (Page, Chars, Site) пропускаємо
                break;
        }

        return this;
    }

    //Збирає з накопичених полів готовий Page і повертає його.
    public Page build() {
        Chars chars = new Chars(
                email,
                news,
                archives,
                voting,
                downloads
        );

        return new Page(
                id,
                title,
                type,
                chars,
                authorize
        );
    }
}