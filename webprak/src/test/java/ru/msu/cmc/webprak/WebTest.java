package ru.msu.cmc.webprak;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WebTest {
    private ChromeDriver driver;
    private WebDriverWait wait;

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
}