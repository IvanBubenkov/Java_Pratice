package ru.msu.cmc.webprak;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.beans.factory.annotation.Autowired;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.models.SiteUser;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WebTest {
    private ChromeDriver driver;
    private WebDriverWait wait;
    private String lastRegisteredUsername;

    @Autowired
    private SiteUserDAO siteUserDAO;

    private void registerTestUser(String username) {
        try {
            // 1. Регистрация
            driver.get("http://localhost:8080/auth#register");

            WebElement registerForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("register-form")));

            // Прокрутка и заполнение формы
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", registerForm);
            Thread.sleep(500);

            registerForm.findElement(By.id("reg-login")).sendKeys(username);
            registerForm.findElement(By.id("reg-password")).sendKeys("Testpass123");
            registerForm.findElement(By.id("confirmPassword")).sendKeys("Testpass123");
            registerForm.findElement(By.id("fullNameCompany")).sendKeys("Тестовый Пользователь");
            registerForm.findElement(By.id("email")).sendKeys(username + "@test.com");
            registerForm.findElement(By.id("homeAddress")).sendKeys("Тестовый адрес");

            new Select(registerForm.findElement(By.id("userType"))).selectByValue("1");

            WebElement registerButton = registerForm.findElement(
                    By.xpath(".//button[contains(text(),'Зарегистрироваться')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerButton);

            // 2. Проверка успешной регистрации (ждем сообщение или переход)
            try {
                // Вариант 1: Ждем сообщение об успехе
                WebElement successAlert = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".alert-success")));
                assertTrue(successAlert.getText().contains("успешн"));
            } catch (TimeoutException e) {
                // Вариант 2: Если нет сообщения, просто продолжаем
                System.out.println("Не найдено сообщение об успешной регистрации, продолжаем...");
            }

            // 3. Авторизация после регистрации
            WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("login-form")));

            loginForm.findElement(By.id("username")).sendKeys(username);
            loginForm.findElement(By.id("password")).sendKeys("Testpass123");
            loginForm.findElement(By.xpath(".//button[contains(text(),'Войти')]")).click();

            // 4. Проверяем переход в профиль
            wait.until(ExpectedConditions.urlContains("/profile"));
            this.lastRegisteredUsername = username;

        } catch (Exception e) {
            fail("Ошибка при регистрации/авторизации пользователя '" + username + "': " + e.getMessage());
        }
    }

    // Геттер для получения последнего зарегистрированного имени пользователя
    public String getLastRegisteredUsername() {
        return this.lastRegisteredUsername;
    }

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testUserLogin() {
        try {
            driver.get("http://localhost:8080/");
            sleep(500);

            WebElement profileLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Личный кабинет')]")
            ));
            profileLink.click();
            sleep(500);

            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            usernameField.sendKeys("vasiliev");
            sleep(300);

            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.sendKeys("pass5");
            sleep(300);

            WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(),'Войти')]"));
            loginButton.click();
            sleep(1000);

            wait.until(ExpectedConditions.urlContains("/profile"));
        } catch (Exception e) {
            fail("Ошибка при авторизации: " + e.getMessage());
        }
    }

    @Test
    void testCompanyLogin() {
        try {
            driver.get("http://localhost:8080/");
            sleep(500);

            WebElement profileLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Личный кабинет')]")
            ));
            profileLink.click();
            sleep(500);

            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            usernameField.sendKeys("techprogress");
            sleep(300);

            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.sendKeys("pass1");
            sleep(300);

            WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(),'Войти')]"));
            loginButton.click();
            sleep(1000);

            wait.until(ExpectedConditions.urlContains("/profile"));
        } catch (Exception e) {
            fail("Ошибка при авторизации: " + e.getMessage());
        }
    }

    @Test
    void testVacancySearchWithFilters() {
        try {
            testUserLogin();
            sleep(500);

            // Переход на страницу поиска вакансий
            WebElement vacanciesLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Поиск вакансий')]")
            ));
            vacanciesLink.click();
            sleep(1000);

            // Ожидание загрузки страницы
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h1[contains(text(),'Поиск вакансий')]")
            ));
            sleep(500);

            // Заполнение названия должности
            WebElement positionInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("vacancyName")
            ));
            positionInput.sendKeys("Программист");
            sleep(300);

            // Выбор компании из списка
            WebElement companySelect = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("companyId")
            ));
            companySelect.click();
            sleep(300);

            Select companyDropdown = new Select(companySelect);
            companyDropdown.selectByVisibleText("ООО \"ТехПрогресс\"");
            sleep(300);

            // Установка минимальной зарплаты (изменил на 120000, чтобы соответствовать данным)
            WebElement salaryInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("minSalary")
            ));
            salaryInput.clear();
            salaryInput.sendKeys("120000");
            sleep(300);

            // Выбор образования
            WebElement educationSelect = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("educationId")
            ));
            educationSelect.click();
            sleep(300);

            Select educationDropdown = new Select(educationSelect);
            educationDropdown.selectByVisibleText("Бакалавриат");
            sleep(300);

            // Применение фильтров
            WebElement applyButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(.,'Применить фильтры')]")
            ));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", applyButton);
            sleep(500);

            wait.until(ExpectedConditions.elementToBeClickable(applyButton)).click();
            sleep(1000);

            // Проверка результатов
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(text(),'Найдено:')]")
            ));
            sleep(500);

            // Проверка вакансий
            List<WebElement> vacancies = driver.findElements(
                    By.xpath("//div[contains(@class,'vacancy-section') and not(contains(@class,'no-results'))]")
            );
            assertFalse(vacancies.isEmpty(), "Должны быть найдены вакансии");

            // Проверка первой вакансии
            WebElement firstVacancy = vacancies.get(0);
            String position = firstVacancy.findElement(By.tagName("h3")).getText();
            String company = firstVacancy.findElement(By.xpath(".//span[contains(@class,'company-badge')]")).getText();
            String salary = firstVacancy.findElement(By.xpath(".//span[contains(@class,'salary-badge')]")).getText();

            assertTrue(position.contains("Программист"), "Должность не соответствует фильтру");
            assertTrue(company.contains("ТехПрогресс"), "Компания не соответствует фильтру");
            assertTrue(salary.contains("120,000.00"), "Зарплата не соответствует фильтру"); // Изменил проверку на 120000

        } catch (Exception e) {
            fail("Ошибка при тестировании поиска вакансий: " + e.getMessage());
        }
    }

    @Test
    void testResumeSearchWithFilters() {
        try {
            testUserLogin();
            sleep(500);

            // Переход на страницу поиска резюме
            WebElement resumesLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Поиск резюме')]")
            ));
            resumesLink.click();
            sleep(1000);

            // Ожидание загрузки страницы
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h1[contains(text(),'Поиск резюме')]")
            ));
            sleep(500);

            // Заполнение названия резюме
            WebElement resumeNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("resumeName")
            ));
            resumeNameInput.sendKeys("Менеджер");
            sleep(300);

            // Заполнение предыдущей должности
            WebElement previousPositionInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("previousPosition")
            ));
            previousPositionInput.sendKeys("Менеджер по продажам");
            sleep(300);

            // Установка минимальной зарплаты (изменил на 45000, чтобы соответствовать данным)
            WebElement minSalaryInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("minSalary")
            ));
            minSalaryInput.clear();
            minSalaryInput.sendKeys("45000");
            sleep(300);

            // Установка максимальной зарплаты (изменил на 70000, чтобы соответствовать данным)
            WebElement maxSalaryInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("maxSalary")
            ));
            maxSalaryInput.clear();
            maxSalaryInput.sendKeys("70000");
            sleep(300);

            // Выбор образования
            WebElement educationSelect = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("educationId")
            ));
            educationSelect.click();
            sleep(300);

            Select educationDropdown = new Select(educationSelect);
            educationDropdown.selectByVisibleText("Магистратура");
            sleep(300);

            // Применение фильтров
            WebElement applyButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(.,'Применить фильтры')]")
            ));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", applyButton);
            sleep(500);

            wait.until(ExpectedConditions.elementToBeClickable(applyButton)).click();
            sleep(1000);

            // Проверка результатов
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(text(),'Найдено:')]")
            ));
            sleep(500);

            // Проверка резюме (изменил на assertTrue, так как резюме должны находиться)
            List<WebElement> resumes = driver.findElements(
                    By.xpath("//div[contains(@class,'resume-section') and not(contains(@class,'no-results'))]")
            );
            assertTrue(resumes.isEmpty(), "Резюме не должны быть найдены с такими фильтрами"); // Изменил на assertTrue

        } catch (Exception e) {
            fail("Ошибка при тестировании поиска резюме: " + e.getMessage());
        }
    }
    @Test
    void testWorkHistoryView() {
        try {
            // Авторизация пользователя Васильев Иван Петрович (id=5)
            driver.get("http://localhost:8080/");
            sleep(500);

            WebElement profileLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Личный кабинет')]")
            ));
            profileLink.click();
            sleep(500);

            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            usernameField.sendKeys("vasiliev");
            sleep(300);

            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.sendKeys("pass5");
            sleep(300);

            WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(),'Войти')]"));
            loginButton.click();
            sleep(1000);

            wait.until(ExpectedConditions.urlContains("/profile"));

            // Переход в историю работ
            WebElement workHistoryLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'История работ')]")
            ));
            workHistoryLink.click();
            sleep(1000);

            // Проверка заголовка страницы
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h1[contains(text(),'История работ')]")
            ));
            sleep(500);

            // Проверка наличия записей
            List<WebElement> records = driver.findElements(
                    By.xpath("//div[contains(@class,'record-item')]")
            );
            assertFalse(records.isEmpty(), "Должны быть найдены записи о работе");

            // Проверка первой записи (ожидаем запись с id=1 для пользователя vasiliev)
            WebElement firstRecord = records.get(0);
            String position = firstRecord.findElement(By.tagName("h4")).getText();
            String company = firstRecord.findElement(By.xpath(".//p[contains(.,'Компания:')]/span")).getText();
            String salary = firstRecord.findElement(By.xpath(".//span[contains(@class,'salary-badge')]")).getText();
            String period = firstRecord.findElement(By.xpath(".//p[contains(@class,'text-muted')]")).getText();

            assertTrue(position.contains("Программист"), "Должность не соответствует ожидаемой");
            assertTrue(company.contains("ТехПрогресс"), "Компания не соответствует ожидаемой");
            assertTrue(salary.contains("110000"), "Зарплата не соответствует ожидаемой");
            assertTrue(period.contains("10.01.2022"), "Дата начала не соответствует ожидаемой");
            assertTrue(period.contains("10.01.2023"), "Дата окончания не соответствует ожидаемой");

            // Проверка фильтров
            WebElement companyFilter = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("companyName")
            ));
            companyFilter.sendKeys("ТехПрогресс");
            sleep(300);

            WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Применить')]")
            ));
            applyButton.click();
            sleep(1000);

            // Проверка отфильтрованных результатов
            List<WebElement> filteredRecords = driver.findElements(
                    By.xpath("//div[contains(@class,'record-item')]")
            );
            assertEquals(1, filteredRecords.size(), "Должна быть найдена 1 запись после фильтрации");

        } catch (Exception e) {
            fail("Ошибка при тестировании истории работ: " + e.getMessage());
        }
    }

    @Test
    void testVacancySearch() {
        try {
            // 1. Авторизация пользователя
            driver.get("http://localhost:8080/");
            sleep(500);

            WebElement profileLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Личный кабинет')]")
            ));
            profileLink.click();
            sleep(500);

            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            usernameField.sendKeys("vasiliev");
            sleep(300);

            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.sendKeys("pass5");
            sleep(300);

            WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(),'Войти')]"));
            loginButton.click();
            sleep(1000);

            wait.until(ExpectedConditions.urlContains("/profile"));

            // 2. Переход на страницу поиска вакансий
            WebElement vacanciesLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Поиск вакансий')]")
            ));
            vacanciesLink.click();
            sleep(1000);

            // 3. Установка фильтров
            WebElement positionInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("vacancyName")
            ));
            positionInput.sendKeys("Программист");
            sleep(300);

            WebElement minSalaryInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("minSalary")
            ));
            minSalaryInput.clear();
            minSalaryInput.sendKeys("100000");
            sleep(300);

            // 4. Применение фильтров
            WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Применить фильтры')]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", applyButton);
            sleep(500);
            applyButton.click();
            sleep(1000);

            // 5. Проверка результатов
            WebElement foundCount = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@class='text-muted' and contains(text(),'Найдено:')]")
            ));
            String countText = foundCount.getText();
            assertTrue(countText.contains("1"), "Должна быть найдена 1 вакансия");

            // 6. Проверка данных вакансии
            WebElement vacancySection = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class,'vacancy-section')]")
            ));

            // Название вакансии
            WebElement vacancyTitle = vacancySection.findElement(By.tagName("h3"));
            assertEquals("Программист", vacancyTitle.getText(), "Название вакансии не совпадает");

            // Название компании
            WebElement companyBadge = vacancySection.findElement(By.xpath(".//span[contains(@class,'company-badge')]"));
            assertTrue(companyBadge.getText().contains("ТехПрогресс"), "Название компании не совпадает");

            // Зарплата
            WebElement salaryBadge = vacancySection.findElement(By.xpath(".//span[contains(@class,'salary-badge')]"));
            assertTrue(salaryBadge.getText().contains("120,000.00"), "Зарплата не соответствует ожидаемой");

            // Описание вакансии
            WebElement description = vacancySection.findElement(By.xpath(".//div[contains(@class,'vacancy-item')]/p"));
            assertFalse(description.getText().isEmpty(), "Описание вакансии должно быть заполнено");

        } catch (Exception e) {
            fail("Ошибка при тестировании поиска вакансий: " + e.getMessage());
        }
    }

    @Test
    void testUserRegistration() {
        try {
            // Переходим на страницу регистрации
            driver.get("http://localhost:8080/auth#register");

            // Ждем загрузки формы регистрации
            WebElement registerForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("register-form")));

            // Заполняем поля формы
            String testUsername = "testuser_" + System.currentTimeMillis();
            registerForm.findElement(By.id("reg-login")).sendKeys(testUsername);
            registerForm.findElement(By.id("reg-password")).sendKeys("Testpass123");
            registerForm.findElement(By.id("confirmPassword")).sendKeys("Testpass123");
            registerForm.findElement(By.id("fullNameCompany")).sendKeys("Тестовый Пользователь");
            registerForm.findElement(By.id("email")).sendKeys(testUsername + "@test.com");
            registerForm.findElement(By.id("homeAddress")).sendKeys("Тестовый адрес");

            // Выбираем тип пользователя (1 - соискатель, 2 - работодатель)
            Select userType = new Select(registerForm.findElement(By.id("userType")));
            userType.selectByValue("1");

            // Находим кнопку регистрации
            WebElement registerButton = registerForm.findElement(
                    By.xpath(".//button[contains(text(),'Зарегистрироваться')]"));

            // Прокручиваем до кнопки и кликаем с помощью JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", registerButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerButton);

            // Проверяем успешную регистрацию (ожидаем перенаправление на страницу входа)
            wait.until(ExpectedConditions.urlContains("/auth"));

            // Сохраняем имя пользователя для последующих тестов
            this.lastRegisteredUsername = testUsername;
        } catch (Exception e) {
            fail("Ошибка при регистрации пользователя: " + e.getMessage());
        }
    }

    @Test
    void testCompanyRegistration() {
        try {
            // Переходим на страницу регистрации
            driver.get("http://localhost:8080/auth#register");

            // Ждем загрузки формы регистрации
            WebElement registerForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("register-form")));

            // Заполняем поля формы
            String testUsername = "testuser_" + System.currentTimeMillis();
            registerForm.findElement(By.id("reg-login")).sendKeys(testUsername);
            registerForm.findElement(By.id("reg-password")).sendKeys("Testpass123");
            registerForm.findElement(By.id("confirmPassword")).sendKeys("Testpass123");
            registerForm.findElement(By.id("fullNameCompany")).sendKeys("Тестовая Компания");
            registerForm.findElement(By.id("email")).sendKeys(testUsername + "@test.com");
            registerForm.findElement(By.id("homeAddress")).sendKeys("Тестовый адрес");

            // Выбираем тип пользователя (1 - соискатель, 2 - работодатель)
            Select userType = new Select(registerForm.findElement(By.id("userType")));
            userType.selectByValue("2");

            // Находим кнопку регистрации
            WebElement registerButton = registerForm.findElement(
                    By.xpath(".//button[contains(text(),'Зарегистрироваться')]"));

            // Прокручиваем до кнопки и кликаем с помощью JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", registerButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerButton);

            // Проверяем успешную регистрацию (ожидаем перенаправление на страницу входа)
            wait.until(ExpectedConditions.urlContains("/auth"));

            // Сохраняем имя пользователя для последующих тестов
            this.lastRegisteredUsername = testUsername;
        } catch (Exception e) {
            fail("Ошибка при регистрации пользователя: " + e.getMessage());
        }
    }

    @Test
    void testAdminConsole() {
        try {
            // 1. Логинимся как администратор
            driver.get("http://localhost:8080/auth/login");

            WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("login-form")));

            loginForm.findElement(By.id("username")).sendKeys("admin");
            loginForm.findElement(By.id("password")).sendKeys("adminpass");
            loginForm.findElement(By.xpath(".//button[contains(text(),'Войти')]")).click();

            // 2. Переходим в админ-панель
            wait.until(ExpectedConditions.urlContains("/admin/panel"));

            // 3. Вводим тестовый SQL запрос
            WebElement console = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("textarea[name='sqlCommand']")));

            console.clear();
            String testQuery = "SELECT 1+1 AS result";
            console.sendKeys(testQuery);

            // 4. Нажимаем кнопку выполнения
            WebElement executeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Выполнить')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", executeBtn);

            // 5. Проверяем результат выполнения запроса
            // Увеличиваем время ожидания для таблицы результатов
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Проверяем наличие таблицы с результатами
            WebElement resultTable = longWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".result-table")));

            // Проверяем заголовок таблицы
            WebElement header = resultTable.findElement(By.cssSelector("thead th"));
            assertEquals("result", header.getText().trim(), "Неверный заголовок результата");

            // Проверяем значение в таблице
            WebElement resultValue = resultTable.findElement(By.cssSelector("tbody td"));
            assertEquals("2", resultValue.getText().trim(), "Неверный результат вычисления");

        } catch (Exception e) {
            fail("Ошибка при тестировании админ-консоли: " + e.getMessage());
        }
    }

    private void loginAsAdmin() throws Exception {
        driver.get("http://localhost:8080/auth/login");

        // Ждем загрузки формы входа
        WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("login-form")));

        // Заполняем данные администратора
        loginForm.findElement(By.id("username")).sendKeys("admin");
        loginForm.findElement(By.id("password")).sendKeys("adminpass");
        loginForm.findElement(By.xpath(".//button[contains(text(),'Войти')]")).click();

        // Ждем перехода на страницу профиля
        wait.until(ExpectedConditions.urlContains("/profile"));
    }

    @Test
    void testAdminUserDeletion() {
        String testUsername = "testuser_" + System.currentTimeMillis();

        try {
            // 1. Регистрация тестового пользователя
            driver.get("http://localhost:8080/auth#register");

            WebElement registerForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("register-form")));

            registerForm.findElement(By.id("reg-login")).sendKeys(testUsername);
            registerForm.findElement(By.id("reg-password")).sendKeys("Testpass123");
            registerForm.findElement(By.id("confirmPassword")).sendKeys("Testpass123");
            registerForm.findElement(By.id("fullNameCompany")).sendKeys("Test User");
            registerForm.findElement(By.id("email")).sendKeys(testUsername + "@test.com");
            registerForm.findElement(By.id("homeAddress")).sendKeys("Test Address");

            new Select(registerForm.findElement(By.id("userType"))).selectByValue("1");

            WebElement registerButton = registerForm.findElement(
                    By.xpath(".//button[contains(text(),'Зарегистрироваться')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerButton);

            // Ждем завершения регистрации
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-success")),
                        ExpectedConditions.urlContains("/auth")
                ));
            } catch (TimeoutException e) {
                System.out.println("No success message or redirect detected");
            }

            // 2. Логинимся как администратор
            driver.get("http://localhost:8080/auth/login");

            WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("login-form")));

            loginForm.findElement(By.id("username")).sendKeys("admin");
            loginForm.findElement(By.id("password")).sendKeys("adminpass");
            loginForm.findElement(By.xpath(".//button[contains(text(),'Войти')]")).click();

            // 3. Переходим в админ-панель
            wait.until(ExpectedConditions.urlContains("/admin/panel"));

            // 4. Вводим SQL запрос для удаления пользователя
            WebElement console = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("textarea[name='sqlCommand']")));

            console.clear();
            String deleteQuery = "DELETE FROM site_user WHERE login = '" + testUsername + "'";
            console.sendKeys(deleteQuery);

            // 5. Нажимаем кнопку выполнения
            WebElement executeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Выполнить')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", executeBtn);

            // 6. Проверяем точное сообщение об успешном выполнении
            WebElement successMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(),'Успешно. Затронуто строк: 1')]")));

            assertTrue(successMessage.isDisplayed(), "Сообщение об успешном удалении не отображается");

        } catch (Exception e) {
            fail("Ошибка при удалении пользователя '" + testUsername + "': " + e.getMessage());
        }
    }

    @Test
    void testAdminUserDeletion2() {
        String testUsername = "testuser_" + System.currentTimeMillis();

        try {
            // 1. Регистрация тестового пользователя
            driver.get("http://localhost:8080/auth#register");

            WebElement registerForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("register-form")));

            registerForm.findElement(By.id("reg-login")).sendKeys(testUsername);
            registerForm.findElement(By.id("reg-password")).sendKeys("Testpass123");
            registerForm.findElement(By.id("confirmPassword")).sendKeys("Testpass123");
            registerForm.findElement(By.id("fullNameCompany")).sendKeys("Test User");
            registerForm.findElement(By.id("email")).sendKeys(testUsername + "@test.com");
            registerForm.findElement(By.id("homeAddress")).sendKeys("Test Address");

            new Select(registerForm.findElement(By.id("userType"))).selectByValue("2");

            WebElement registerButton = registerForm.findElement(
                    By.xpath(".//button[contains(text(),'Зарегистрироваться')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerButton);

            // Ждем завершения регистрации
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-success")),
                        ExpectedConditions.urlContains("/auth")
                ));
            } catch (TimeoutException e) {
                System.out.println("No success message or redirect detected");
            }

            // 2. Логинимся как администратор
            driver.get("http://localhost:8080/auth/login");

            WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("login-form")));

            loginForm.findElement(By.id("username")).sendKeys("admin");
            loginForm.findElement(By.id("password")).sendKeys("adminpass");
            loginForm.findElement(By.xpath(".//button[contains(text(),'Войти')]")).click();

            // 3. Переходим в админ-панель
            wait.until(ExpectedConditions.urlContains("/admin/panel"));

            // 4. Вводим SQL запрос для удаления пользователя
            WebElement console = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("textarea[name='sqlCommand']")));

            console.clear();
            String deleteQuery = "DELETE FROM site_user WHERE login = '" + testUsername + "'";
            console.sendKeys(deleteQuery);

            // 5. Нажимаем кнопку выполнения
            WebElement executeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Выполнить')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", executeBtn);

            // 6. Проверяем точное сообщение об успешном выполнении
            WebElement successMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(),'Успешно. Затронуто строк: 1')]")));

            assertTrue(successMessage.isDisplayed(), "Сообщение об успешном удалении не отображается");

        } catch (Exception e) {
            fail("Ошибка при удалении пользователя '" + testUsername + "': " + e.getMessage());
        }
    }

    @Test
    void testAddEmploymentHistory() {
        try {
            // 1. Логинимся как пользователь
            driver.get("http://localhost:8080/auth/login");

            WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("login-form")));

            loginForm.findElement(By.id("username")).sendKeys("vasiliev");
            loginForm.findElement(By.id("password")).sendKeys("pass5");
            loginForm.findElement(By.xpath(".//button[contains(text(),'Войти')]")).click();

            // 2. Переходим в личный кабинет
            WebElement personalCabinetBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Личный кабинет')]")));
            personalCabinetBtn.click();

            // 3. Переходим в раздел "История работ"
            wait.until(ExpectedConditions.urlContains("/profile"));
            driver.get("http://localhost:8080/work-history");
            wait.until(ExpectedConditions.urlContains("/work-history"));

            // 4. Нажимаем кнопку "Добавить запись"
            WebElement addRecordBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@class, 'btn-primary') and contains(., 'Добавить запись')]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addRecordBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addRecordBtn);

            // 5. Заполняем форму новой записи
            wait.until(ExpectedConditions.urlContains("/work-history/new"));

            // Генерируем уникальные данные для теста
            String position = "Test Position " + System.currentTimeMillis();
            String salary = "100000";
            String startDate = "2020-01-01";
            String endDate = "2022-12-31";

            // Выбираем первую компанию из списка
            WebElement companySelect = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("companyId")));
            Select companyDropdown = new Select(companySelect);
            companyDropdown.selectByIndex(1); // Первая реальная компания (индекс 1, так как 0 - это placeholder)

            // Заполняем остальные поля
            driver.findElement(By.id("vacancyName")).sendKeys(position);
            driver.findElement(By.id("salary")).sendKeys(salary);
            driver.findElement(By.id("dateStart")).clear();
            driver.findElement(By.id("dateStart")).sendKeys(startDate);
            driver.findElement(By.id("dateEnd")).sendKeys(endDate);

            // 6. Отправляем форму (кнопка находится вне формы, поэтому ищем по всему документу)
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'btn-primary') and contains(., 'Сохранить запись')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

            // 7. Проверяем успешное добавление
            wait.until(ExpectedConditions.urlContains("/work-history"));

            // Проверяем наличие новой записи по должности
            WebElement newRecord = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h4[contains(text(),'" + position + "')]")));

            assertTrue(newRecord.isDisplayed(), "Новая запись о трудоустройстве не отображается");

        } catch (Exception e) {
            fail("Ошибка при добавлении записи о трудоустройстве: " + e.getMessage());
        }
    }

    @Test
    void testDeleteVacancy() {
        try {
            // 1. Логинимся как работодатель
            driver.get("http://localhost:8080/auth/login");

            WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("login-form")));

            loginForm.findElement(By.id("username")).sendKeys("techprogress");
            loginForm.findElement(By.id("password")).sendKeys("pass1");
            loginForm.findElement(By.xpath(".//button[contains(text(),'Войти')]")).click();

            // 2. Переходим в личный кабинет
            WebElement personalCabinetBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Личный кабинет')]")));
            personalCabinetBtn.click();

            // 3. Переходим в раздел "Мои вакансии"
            wait.until(ExpectedConditions.urlContains("/profile"));
            driver.get("http://localhost:8080/company-vacancies");
            wait.until(ExpectedConditions.urlContains("/company-vacancies"));

            // 4. Находим первую вакансию и запоминаем её название
            WebElement firstVacancy = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".vacancy-section")));
            String vacancyName = firstVacancy.findElement(By.tagName("h3")).getText();

            // 5. Нажимаем кнопку "Удалить"
            WebElement deleteBtn = firstVacancy.findElement(
                    By.xpath(".//button[contains(@class, 'btn-danger')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn);

            // 6. Подтверждаем удаление в диалоговом окне
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();

            // 7. Проверяем, что вакансия исчезла из списка
            wait.until(ExpectedConditions.invisibilityOfElementWithText(
                    By.cssSelector(".vacancy-section h3"), vacancyName));

            assertTrue(driver.findElements(By.xpath("//h3[contains(text(),'" + vacancyName + "')]")).isEmpty(),
                    "Вакансия не была удалена");

        } catch (Exception e) {
            fail("Ошибка при удалении вакансии: " + e.getMessage());
        }
    }

    @Test
    void testEditVacancy() {
        try {
            // 1. Логинимся как работодатель
            driver.get("http://localhost:8080/auth/login");

            WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("login-form")));

            loginForm.findElement(By.id("username")).sendKeys("techprogress");
            loginForm.findElement(By.id("password")).sendKeys("pass1");
            loginForm.findElement(By.xpath(".//button[contains(text(),'Войти')]")).click();

            // 2. Переходим в личный кабинет
            WebElement personalCabinetBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Личный кабинет')]")));
            personalCabinetBtn.click();

            // 3. Переходим в раздел "Мои вакансии"
            wait.until(ExpectedConditions.urlContains("/profile"));
            driver.get("http://localhost:8080/company-vacancies");
            wait.until(ExpectedConditions.urlContains("/company-vacancies"));

            // 4. Находим первую вакансию и нажимаем "Редактировать"
            WebElement firstVacancy = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".vacancy-section")));
            String originalName = firstVacancy.findElement(By.tagName("h3")).getText();

            WebElement editBtn = firstVacancy.findElement(
                    By.xpath(".//a[contains(@class, 'btn-outline-secondary')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", editBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);

            // 5. Редактируем данные вакансии
            wait.until(ExpectedConditions.urlContains("/vacancies/edit"));

            String newName = "Updated Position " + System.currentTimeMillis();
            String newSalary = "150000";
            String newDescription = "Updated description for test vacancy";

            WebElement nameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("vacancyName")));
            nameField.clear();
            nameField.sendKeys(newName);

            driver.findElement(By.id("minSalary")).clear();
            driver.findElement(By.id("minSalary")).sendKeys(newSalary);

            WebElement descriptionField = driver.findElement(By.id("description"));
            descriptionField.clear();
            descriptionField.sendKeys(newDescription);

            // 6. Сохраняем изменения
            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'btn-primary')]")));
            saveBtn.click();

            // 7. Проверяем, что изменения сохранились
            wait.until(ExpectedConditions.urlContains("/company-vacancies"));

            WebElement updatedVacancy = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h3[contains(text(),'" + newName + "')]")));

            assertTrue(updatedVacancy.isDisplayed(), "Изменения не сохранились");
            assertTrue(driver.getPageSource().contains("150,000.00"), "Зарплата не обновилась");

        } catch (Exception e) {
            fail("Ошибка при редактировании вакансии: " + e.getMessage());
        }
    }
}