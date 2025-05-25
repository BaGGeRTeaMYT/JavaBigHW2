# Синхронное межсервисное взаимодействие

## Архитектура

В приложении есть три микросервиса

1. **API Gateway (Port 8080)**: Направляет запросы на нужный микросервис.
2. **File Storing Service (Port 8081)**: Отвечает за загрузку, хранение, получение и удаление файлов.
3. **File Analysis Service (Port 8082)**: Проводит анализ файла и проверку на плагиат.

## Сборка приложения

```bash
mvn clean install
```

## Запуск сервисов

Каждый сервис необходимо запустить в отдельном терминале

1. API Gateway:
```bash
cd api-gateway
mvn spring-boot:run
```

2. File Storing Service:
```bash
cd file-storing-service
mvn spring-boot:run
```

3. File Analysis Service:
```bash
cd file-analysis-service
mvn spring-boot:run
```

Поскольку я ленивый пользователь Windows 10, вот альтернативный способ запустить все сервисы сразу
```powershell
.\start-services.ps1
```

## Swagger

У каждого сервиса есть Swagger:

- File Storage Service Swagger UI: http://localhost:8081/swagger-ui.html
- File Analysis Service Swagger UI: http://localhost:8082/swagger-ui.html

### File Storage Service (через API Gateway)

- Загрузка файла:
  ```
  POST http://localhost:8080/api/files/upload
  ```

- Скачивание файла:
  ```
  GET http://localhost:8080/api/files/{id}
  ```

### File Analysis Service (через API Gateway)

- Проанализировать файл:
  ```
  POST http://localhost:8080/api/analysis/analyze/{fileId}
  ```

- Получить результат анализа:
  ```
  GET http://localhost:8080/api/analysis/{fileId}
  ```