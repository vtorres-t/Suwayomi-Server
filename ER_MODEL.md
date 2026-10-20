+-----------------------------------+             +-----------------------------------+

|             category              |             |              history              |
+-----------------------------------+             +-----------------------------------+

| PK | id           : BIGINT        |             | PK | id           : BIGINT        |
|    | name         : VARCHAR       |             | FK | chapter_id   : BIGINT        |
|    | sort         : INT           |             |    | last_read    : BIGINT        |
|    | flags        : INT           |             +-----------------------------------+
+-----------------+-----------------+                              |

                    | 1                                              |
                    |                                                |
                    | 0..*                                           | 1..*
+-----------------v-----------------+                              |

|          manga_category           |                              |
+-----------------------------------+                              |

| FK | manga_id     : BIGINT        |                              |
| FK | category_id  : BIGINT        |                              |
+-----------------^-----------------+                              |

                    | 0..*                                           |
                    |                                                |
                    | 1                                              |
+-----------------+------------------------------------------------+------------------+

|                                      manga                                          |
+-------------------------------------------------------------------------------------+

| PK | id                  : BIGINT                                                   |
|    | source_id           : BIGINT    -> ID numérico de la extensión / fuente        |
|    | url                 : VARCHAR   -> Ruta interna en la web de origen            |
|    | title               : VARCHAR   -> Nombre del manga                            |
|    | artist              : VARCHAR                                                  |
|    | author              : VARCHAR                                                  |
|    | description         : TEXT                                                     |
|    | genre               : VARCHAR   -> Lista de tags/géneros por comas             |
|    | status              : INT       -> Estado (1=En curso, 2=Completo, etc.)       |
|    | thumbnail_url       : VARCHAR                                                  |
|    | favorite            : BOOLEAN   -> True si está en la biblioteca               |
|    | initialized         : BOOLEAN   -> Datos iniciales cargados desde la web       |
|    | date_added          : BIGINT    -> Timestamp de adición a la biblioteca        |
|    | viewer              : INT       -> Tipo de lector web asignado                 |
|    | chapter_flags       : INT       -> Filtros/orden de capítulos (Bitmask)        |
|    | cover_last_modified : BIGINT    -> Control de caché de carátula                |
|    | version             : BIGINT    -> Control técnico de concurrencia optimista   |
|    | is_syncing          : BOOLEAN   -> Estado de actualización en segundo plano    |
+-----------------+------------------------------------------------+------------------+

                    | 1                                              | 1
                    |                                                |
                    | 0..*                                           | 0..*
+-----------------v-----------------+             +----------------v------------------+

|              chapter              |             |               track               |
+-----------------------------------+             +-----------------------------------+

| PK | id             : BIGINT      |             | PK | id                : BIGINT   |
| FK | manga_id       : BIGINT      |             | FK | manga_id          : BIGINT   |
|    | url            : VARCHAR     |             |    | sync_id           : INT      |
|    | name           : VARCHAR     |             |    | remote_id         : BIGINT   |
|    | scanlator      : VARCHAR     |             |    | library_id        : BIGINT   |
|    | read           : BOOLEAN     |             |    | title             : VARCHAR  |
|    | bookmark       : BOOLEAN     |             |    | last_chapter_read : FLOAT    |
|    | last_page_read : INT         |             |    | total_chapters    : INT      |
|    | chapter_number : FLOAT       |             |    | score             : FLOAT    |
|    | source_order   : INT         |             |    | status            : INT      |
|    | date_fetch     : BIGINT      |             |    | remote_url        : VARCHAR  |
|    | date_upload    : BIGINT      |             +-----------------------------------+
|    | last_modified  : BIGINT      |
+-----------------+-----------------+
| 1
|
| 0..1
+-----------------v-----------------+

|          download_queue           |
+-----------------------------------+

| PK | id             : BIGINT      |
| FK | chapter_id     : BIGINT      |
|    | download_status: INT         |
|    | downloaded_page: INT         |
+-----------------------------------+
