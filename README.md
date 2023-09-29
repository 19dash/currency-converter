# Конвертер валют
## Демонстрация работы
![](https://github.com/currency-converter/demo.gif)
## Описание
Приложение предназначено для получения актуальной информации о курсах валют. Приложение выполняет следующие функции:
1. Возможность свободного обмена валюты одной страны на валюту другой страны на текущую дату. Котировки подгружаются с сайта ЦБ РФ в xml формате (http://www.cbr.ru/scripts/XML_daily.asp).
2. Получение информации о динамике курса валюты в графическом виде. Есть возможность выбора периода наблюдения: неделя, месяц или год. Данные берутся с сайта ЦБ РФ с использованием параметров (например, http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=02/03/2001&date_req2=14/03/2001&VAL_NM_RQ=R01235).
3. Получение информации о курсах валют Альфабанка и Тинькофф Банка на текущий момент. Для передачи данных в JSON формате используются АПИ обоих банков.
## Используемые технологии
* Java 17
* Maven
* Spring Boot 3.1.2
* Jackson 2.15.2
* Thymeleaf
* PostgreSQL 13
* HTML5/CSS
* Javascript/Jquery
* Tomcat
* XML Parsing: SAX/DOM
* RestTemplate
## Запуск приложения
Данный проект можно импортировать в Eclipse или аналогичной IDE следующим образом: нажмите File -> Import ... -> Maven -> Existing Maven Projects -> Next.
Создайте Maven проект, чтобы установить все необходимые зависимости.<br/>
Чтобы настроить базу данных, установите PostgreSql. Создайте любую базу данных.<br/>
Затем обновите три поля конфигурации ниже в файле application.properties внутри папки /resources.
```
spring.datasource.url=jdbc:postgresql://localhost:5432/dsolo
spring.datasource.username=dsolo
spring.datasource.password=1234
```

