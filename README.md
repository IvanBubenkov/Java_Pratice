# Кадровое агентство

## Схема сайта
![website_navigation](images/website_navigation.png)

## Описание страниц
С любой страницы можно попасть на **Главную страницу**. С любой страницы, кроме **Страницы администратора**, **Страницы регистрации и входа** и **Страницы регистрации** можно попасть в **Личный кабине**.

На сайте есть несколько **ролей**:
* Соискатель (Ap)
* Работодатель (Em)
* Администратор (Ad)

### Главная страница
* Переход на страницу с вакансиями
* Переход на страницу с резюме
* Переход в личный кабинет
* Списком будут отображены самые популярные вакансии (в базе данных для этого храниться специальный атрибут)
* Списком будут отображены самые популярные резюме (в базе данных для этого храниться специальный атрибут)

### Личный кабинет
* Фамилия Имя Отчество (Ap) или Название компании (Em)
* Email
* Ступень образования (Ap)
* Домашний адрес (Ap)
* Статус соискателя (Информация о том, ищет ли он работу или нет) (Ap)
* Переход на историю трудоустройств соискателя (Ap)
* Переход на страницу с вакансиями работодателя (Em)
* Переход на страницу с резюме соискателя (Ap)
* Кнопка **Редактировать профиль** нужна чтобы изменить данные в профиле (осуществиться переход на **Страницу изменения профиля**)
* Кнопка **Мои отклики** реализует переход на страницу с ответами на заявки к конкретным вакансиям

### Страница регистрации и входа
* Поле для ввода логина
* Поле для ввода пароля
* Кнопка регистрации (При отсутствии аккаунта/особом желании пользователь может зарегестрировать новый аккаунт)

Если логин и пароль принадлежат (Ad), то осуществляется переход на специальную **Страницу администратора**.

### Страница администратора (Ad)
* Поле для ввода команд, управляющих БД
* Поле для получения обратной и отладочной информации от БД

### Страница регистрации
* Поле для ввода Фамилии Имени и Отчества нового пользователя (Ap) или названия компании (Em)
* Домашний адрес (Ap)
* Поле для ввода логина нового пользователя
* Поле для ввода пароля нового пользователя
* Поле для подтверждения пароля, который пользователь ввёл выше

### Страница откликов
* Специальные фильтры для более удобного вывода информации
  * Работодатель
  * Зароботная плата
  * Статус (_На модерации_, _Одобрено_, _Отклонено_)
  * Дата подачи
* Далее будут расположены все обращения или ответы на резюме (Ap) и вакансии (Em) соответственно
* На каждом из пунктов списка будет кнопка, которая реализует переход на страницу с подробной информацией о заявке
* Статус отклика

### Страница поиска вакансий
* Специальные фильтры для более удобного вывода информации
  * Компания
  * Заработная плата
  * Должностям
  * Степени образования
* Далее будет расположен список вакансий, удовлетворяющий условиям фильтрации
* На каждом из пунктов списка будет кнопка, которая реализует переход на страницу с подробной информацией о вакансии

### Страница иформации о вакансии
* Название вакансии
* Компания, которая выставила данное предложение
* Краткое или развёрнутое описание заявки
* Предлагаемая заработная плата
* Необходимое образование
* Требования к послужному списку

### Страница поиска резюме
* Специальные фильтры для более удобного вывода информации
  * Образование
  * Компании, в которых люди работали
  * Занимаемые должности
  * Требуемые Заработные платы
* Далее будет расположен список резюме, удовлетворяющий условиям фильтрации
* На каждом из пунктов списка будет кнопка, которая реализует переход на страницу с подробной информацией о резюме

### Страница информации о резюме
* Фамилия Имя Отчество
* Домашний адрес
* Образование
* Заработная плата, которую хочет соискатель
* Прошлый опыт в компаниях
* Занимаемые должности

### История работ пользователя
* Специальные фильтры для более удобного вывода информации
  * Название компании
  * Должность
  * Заработная плата
  * Временной интервал
* Далее будет список строк, содержащих краткую информацию о послужносм списке соискателя:
  * Название компании
  * Должность
  * Заработная плата
  * Даты начала работы на этой должности
  * Даты окончания работы на этой должности
* Переход на форму для создания новой записи

### Страница новой записи
* Название компании
* Должность
* Заработная плата
* Даты начала работы на этой должности
* Даты окончания работы на этой должности
* Кнопка подтверждения

### Страница всех вакансий компании (Em)
* Специальные фильтры для более удобного вывода информации
  * Должность
  * Заработная плата
  * Необходимая ступень образования
  * Требования к послужному списку
* Далее будет расположен список вакансий, удовлетворяющий условиям фильтрации
* На каждом из пунктов списка будет кнопка, которая реализует переход на страницу с подробной информацией о вакансии
* Переход на форму создания вакансии

### Страница вакансии (Em)
* Должность
* Компания
* Описание
* Заработная плата
* Образование
* Требования к послужному списку
* Кнопка для сохранения изменений в иформации о вакансии
* Кнопка удаления вакансии

### Страница всех резюме пользователя
* Специальные фильтры для более удобного вывода информации
  * Должность
  * Образование (Возможно на разные вакансии нужны разные дипломы)
  * Заработная плата
  * Опыт в компаниях (Для работодателя могут быть важны схожие компании)
  * Занимаемые должности (На разные должности важны близкий должности)
* Далее будет расположен список резюме, удовлетворяющий условиям фильтрации
* На каждом из пунктов списка будет кнопка, которая реализует переход на страницу с подробной информацией о вакансии
* Переход на форму создания резюме

### Страница резюме (Ap)
* Фамилия Имя Отчество
* Домашний адрес
* Образование (Возможно на разные вакансии нужны разные дипломы)
* Требование к заработной плате
* Опыт в компаниях (Для работодателя могут быть важны схожие компании)
* Занимаемые должности (На разные должности важны близкий должности)
* Кнопка для сохранения изменений в иформации о резюме

### Страница изменения профиля
* Фамилия Имя Отчество
* Email
* Домашний адрес
* Образование
* Статус (Ищет ли пользователь рабочее место для своих резюме)
* Кнопка для сохранения изменений в иформации в профиле


## Сценарии использования

1. Получение списка резюме по образованию, компаниям, в которых люди работали, по занимавшимся должностям, зарплатам
    * Главная страница ➤ Страница поиска резюме ➤ Выставление необходимых фильтров
2. Получение списка вакансий по компаниям, должностям, зарплатам
    * Главная страница ➤ Страница поиска вакансий ➤ Выставление необходимых фильтров
3. Получение истории работы для данного человека
    * Главная страница ➤ Личный кабинет ➤ История работ пользователя
4. Поиск подходящих вакансий на резюме и подходящих резюме на вакансию
    * Главная страница ➤ Страница поиска вакансий ➤ Выставление необходимых фильтров
    * Главная страница ➤ Страница поиска резюме ➤ Выставление необходимых фильтров
5. Добавление и удаление данных о человеке, чтение и редактирование данных о нем, добавление данных о новом трудоустройстве
    * Страница регистрации регистрации и входа ➤ Страница регистрации ➤ Ввод данных человека
    * Страница регистрации регистрации и входа ➤ Страница администратора ➤ Использование команд, необходимых для удаления пользователя (Ad)
    * Главная страница ➤ Личный кабинет (Ap)
    * Главная страница ➤ Личный кабинет ➤ Страница изменения профиля (Ap)
    * Главная страница ➤ Личный кабинет ➤ История работ пользователя ➤ Страница новой записи (Ap)
6. Добавление и удаление компании, чтение и редактирование данных о них, добавление, удаление и редактирование вакансий
    * Страница регистрации регистрации и входа ➤ Страница регистрации ➤ Ввод данных компании (Em)
    * Страница регистрации регистрации и входа ➤ Страница администратора ➤ Использование команд, необходимых для удаления пользователя (Ad)
    * Главная страница ➤ Личный кабинет (Em)
    * Главная страница ➤ Личный кабинет ➤ Страница изменения профиля (Em)
    * Главная страница ➤ Личный кабинет ➤ Страница всех вакансий компании ➤ Страница вакансии ➤ Заполнение данных новой вакансии
    * Главная страница ➤ Личный кабинет ➤ Страница всех вакансий компании ➤ Страница вакансии ➤ Удаление вакансии
    * Главная страница ➤ Личный кабинет ➤ Страница всех вакансий компании ➤ Страница вакансии ➤ Редактирование вакансии ➤ Подтверждение изменений

## Схема базы данных сайта
![website_database](images/website_database.png)

## Ссылки

:link: [Схема для навигации по сайту](https://lucid.app/lucidchart/a6af747b-6c6d-4f59-aaa1-a0dbc57633f6/edit?viewport_loc=-2723%2C-1153%2C7703%2C3775%2C0_0&invitationId=inv_c224ca37-4974-4817-ab0c-7283a90b435d)
<br>
:link: [База данных сайта](https://lucid.app/lucidchart/7a7d68a0-9e5e-422f-9425-8dd1acd80e99/edit?viewport_loc=-609%2C-1109%2C4037%2C1978%2C0_0&invitationId=inv_4ccf47c9-7e6a-4e33-99c8-0b6a56a0c30f)
