<?xml version="1.0" encoding="UTF-8"?>
<!--
  Перетворює плоский список сторінок на групи за типом (Type).
  Результат: GroupedSite -> Group (атрибут type) -> Page.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <!-- Прибираємо порожній текст між тегами вхідного файлу -->
    <xsl:strip-space elements="*"/>

    <!-- Ключ для швидкого пошуку всіх сторінок за значенням Type -->
    <xsl:key name="pages-by-type" match="Page" use="Type"/>

    <xsl:template match="/Site">
        <GroupedSite>
            <!-- Беремо лише першу сторінку кожного типу, щоб отримати по одній групі -->
            <xsl:for-each select="Page[generate-id() = generate-id(key('pages-by-type', Type)[1])]">
                <!-- Групи впорядковуємо за назвою типу -->
                <xsl:sort select="Type"/>
                <Group type="{Type}">
                    <!-- Переносимо всі сторінки цього типу через шаблон нижче -->
                    <xsl:apply-templates select="key('pages-by-type', Type)"/>
                </Group>
            </xsl:for-each>
        </GroupedSite>
    </xsl:template>

    <!-- Відтворює будь-який елемент із атрибутами та вкладеним вмістом без зайвих оголошень namespace -->
    <xsl:template match="*">
        <xsl:element name="{name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>