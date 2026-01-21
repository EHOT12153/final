Общие сведения

Нагрузочное и функциональное тестирование приложения MyShop выполняется на выделенном тестовом стенде, развернутом в виртуальной машине под управлением Ubuntu 24.04.3 LTS.
Стенд предназначен для воспроизводимого проведения нагрузочных испытаний, анализа деградаций и формирования отчётности.

Вся система (приложение + инфраструктурные компоненты + мониторинг) разворачивается локально в Docker Compose и работает в изолированной Docker-сети.

Аппаратная конфигурация стенда
Хост / ВМ

ОС: Ubuntu 24.04.3 LTS (Server)

CPU: 8 vCPU

RAM: 16 GB

Disk: 60 GB (SSD / virtual disk)

Тип развертывания: Virtual Machine

Данная конфигурация используется как ограниченный тестовый стенд, что позволяет:

наблюдать эффекты насыщения ресурсов (CPU / memory),

корректно сравнивать прогоны между собой,

не маскировать проблемы за счёт избыточных ресурсов.

Абсолютные значения RPS не эквивалентны production и используются исключительно для сравнительного анализа и методики.

Программное окружение
Базовое ПО

Docker Engine (24.x+)

Docker Compose v2

OpenJDK 17 (внутри контейнеров сервисов)

Python 3.12+ (для анализа и генерации отчётов)

Git

Состав стенда
Прикладной уровень

UI — веб-интерфейс приложения

порт: 8088

API Gateway

единая точка входа

маршрутизация запросов

аутентификация

Бизнес-сервисы (Spring Boot)

Auth Service

Catalog Service

Cart Service

Order Service

Payment Service

Inventory Service

Все сервисы:

регистрируются в Discovery (Eureka),

экспонируют метрики через /actuator/prometheus.

Инфраструктура

PostgreSQL — основная БД

Redis — кэш / сессии

Kafka + Zookeeper — асинхронные события

Kafka-UI — наблюдение за очередями

Observability

Prometheus — сбор метрик

Grafana — визуализация

Grafana Image Renderer — экспорт графиков в PNG

node-exporter — метрики хоста

cAdvisor — метрики контейнеров

Сетевая модель

Все контейнеры работают в одной Docker-сети (shop-net)

Внешний доступ:

UI: http://<host>:8088

Grafana: http://<host>:3000

Prometheus: http://<host>:9090

Kafka-UI: http://<host>:8080 (если включён)

Развёртывание MyShop (пошагово)
1. Подготовка системы

Обновить систему и установить зависимости:

sudo apt update && sudo apt upgrade -y
sudo apt install -y ca-certificates curl gnupg git

Установка Docker
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker


Проверка:

docker --version
docker compose version

2. Получение проекта
git clone <repo_url>
cd <repo_root>


Структура проекта (упрощённо):

.
├── deployment/
│   └── docker-compose.yml
├── shop/
│   ├── api-gateway
│   ├── auth-service
│   ├── catalog-service
│   └── ...
├── jmeter/
├── scripts/
└── docs/

3. Запуск стенда

Перейти в каталог деплоя:

cd deployment


Запуск:

docker compose up -d --build


Первый запуск может занять несколько минут (сборка образов).

4. Проверка работоспособности
Контейнеры
docker ps


Все сервисы должны быть в состоянии Up.

Проверка доступности

UI:

http://<host>:8088


Grafana:

http://<host>:3000


Prometheus targets:

http://<host>:9090/targets


Все targets должны быть в состоянии UP.

5. Подготовка к нагрузочному тестированию

Перед запуском нагрузки убедиться, что:

сервисы стабилизировались после старта (1–2 минуты),

метрики поступают в Prometheus,

в Grafana корректно отображаются дашборды.

6. Запуск нагрузочных тестов
Через Jenkins (рекомендуемый способ)

Job loadtest-max-search

выбрать сценарий

запустить тест

Job LLM-Analysis

TEST_TYPE: max_search или confirm

TEST_ID: UUID прогона

Результат:

JTL-файлы

PNG-графики

итоговый DOCX-отчёт

7. Остановка и очистка стенда
docker compose down


Полная очистка (удаляет volumes):

docker compose down -v

Назначение стенда

Данный стенд предназначен для:

проведения capacity и stability тестов,

анализа деградаций под нагрузкой,

демонстрации методики нагрузочного тестирования,

использования в портфолио Performance Engineer.